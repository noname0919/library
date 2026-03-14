package com.library.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.library.service.IAIService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * AI服务实现类
 * 使用智谱AI的GLM-4-Flash模型（免费）
 */
@Slf4j
@Service
public class AIServiceImpl implements IAIService {

    @Value("${ai.apiKey:}")
    private String apiKey;

    @Value("${ai.model:glm-4-flash}")
    private String model;

    @Value("${ai.apiUrl:https://open.bigmodel.cn/api/paas/v4/chat/completions}")
    private String apiUrl;

    private final OkHttpClient httpClient;

    public AIServiceImpl() {
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    @Override
    public String chat(List<Map<String, String>> messages) {
        // 检查API Key是否配置
        if (apiKey == null || apiKey.isEmpty() || "your-api-key-here".equals(apiKey)) {
            log.error("AI API Key未配置");
            throw new RuntimeException("AI服务未配置，请联系管理员配置API Key");
        }

        try {
            // 构建请求体
            JSONObject requestBody = new JSONObject();
            requestBody.put("model", model);
            
            // 构建完整的消息列表，添加系统规则作为系统提示
            List<Map<String, String>> completeMessages = new ArrayList<>();
            
            // 添加系统规则提示
            Map<String, String> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", getSystemPrompt());
            completeMessages.add(systemMessage);
            
            // 添加用户和助手的对话历史
            completeMessages.addAll(messages);
            
            requestBody.put("messages", completeMessages);
            requestBody.put("temperature", 0.7);
            requestBody.put("max_tokens", 2048);

            // 构建请求
            RequestBody body = RequestBody.create(
                    requestBody.toJSONString(),
                    MediaType.parse("application/json; charset=utf-8")
            );

            Request request = new Request.Builder()
                    .url(apiUrl)
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("Content-Type", "application/json")
                    .post(body)
                    .build();

            // 发送请求
            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    String errorBody = response.body().string();
                    log.error("AI API调用失败: {}", errorBody);
                    throw new RuntimeException("AI服务调用失败: " + response.code());
                }

                String responseBody = response.body().string();
                JSONObject jsonResponse = JSON.parseObject(responseBody);

                // 解析回复
                if (jsonResponse.containsKey("choices") && !jsonResponse.getJSONArray("choices").isEmpty()) {
                    JSONObject choice = jsonResponse.getJSONArray("choices").getJSONObject(0);
                    JSONObject message = choice.getJSONObject("message");
                    return message.getString("content");
                } else {
                    log.error("AI API返回格式异常: {}", responseBody);
                    throw new RuntimeException("AI服务返回格式异常");
                }
            }
        } catch (IOException e) {
            log.error("AI API调用异常", e);
            throw new RuntimeException("AI服务连接失败: " + e.getMessage());
        }
    }

    @Override
    public boolean testConnection() {
        if (apiKey == null || apiKey.isEmpty() || "your-api-key-here".equals(apiKey)) {
            return false;
        }

        try {
            Map<String, String> testMessage = new HashMap<>();
            testMessage.put("role", "user");
            testMessage.put("content", "Hello");
            List<Map<String, String>> testMessages = new ArrayList<>();
            testMessages.add(testMessage);
            chat(testMessages);
            return true;
        } catch (Exception e) {
            log.error("AI服务连接测试失败", e);
            return false;
        }
    }

    /**
     * 获取系统提示，包含图书管理系统的规则
     */
    private String getSystemPrompt() {
        return "你是图书管理系统的智能助手，需要遵循以下规则：\n" +
                "1. 借阅规则：\n" +
                "   - 借阅期限为30天\n" +
                "   - 每个用户最多可同时借阅5本书\n" +
                "   - 用户不能有逾期未还的图书才能借书\n" +
                "   - 图书必须处于上架状态且有可借数量\n" +
                "2. 续借规则：\n" +
                "   - 每本书最多续借3次\n" +
                "   - 只能在图书未逾期且未归还的情况下续借\n" +
                "   - 每次续借延长30天\n" +
                "3. 归还规则：\n" +
                "   - 归还时系统自动更新图书状态\n" +
                "   - 系统会自动识别逾期图书\n" +
                "4. 提醒规则：\n" +
                "   - 每天上午9点执行逾期提醒\n" +
                "   - 包括即将逾期提醒（剩余1天内到期）和已逾期提醒\n" +
                "5. 状态规则：\n" +
                "   - 图书状态：0表示上架，其他状态表示不可借\n" +
                "   - 借阅记录状态：0表示借阅中，1表示已归还\n" +
                "\n" +
                "请基于这些规则回答用户问题，确保信息准确。如果用户的问题与系统规则相关，请严格按照规则回答。";
    }
}
