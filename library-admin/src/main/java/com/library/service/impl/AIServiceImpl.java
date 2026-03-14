package com.library.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.library.service.IAIService;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
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
            requestBody.put("messages", messages);
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
            Map<String, String> testMessage = new java.util.HashMap<>();
            testMessage.put("role", "user");
            testMessage.put("content", "Hello");
            List<Map<String, String>> testMessages = new java.util.ArrayList<>();
            testMessages.add(testMessage);
            chat(testMessages);
            return true;
        } catch (Exception e) {
            log.error("AI服务连接测试失败", e);
            return false;
        }
    }
}
