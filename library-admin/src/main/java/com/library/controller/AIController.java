package com.library.controller;

import com.library.common.core.domain.AjaxResult;
import com.library.service.IAIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * AI智能问答控制器
 */
@RestController
@RequestMapping("/ai")
public class AIController {

    @Autowired
    private IAIService aiService;

    /**
     * 发送消息到AI并获取回复
     *
     * @param request 包含messages列表的请求
     * @return AI回复
     */
    @PostMapping("/chat")
    public AjaxResult chat(@RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            List<Map<String, String>> messages = (List<Map<String, String>>) request.get("messages");
            
            if (messages == null || messages.isEmpty()) {
                return AjaxResult.error("消息不能为空");
            }
            
            String response = aiService.chat(messages);
            return AjaxResult.success("操作成功", response);
        } catch (Exception e) {
            return AjaxResult.error("AI服务调用失败: " + e.getMessage());
        }
    }

    /**
     * 测试AI服务是否可用
     *
     * @return 测试结果
     */
    @GetMapping("/test")
    public AjaxResult test() {
        try {
            boolean available = aiService.testConnection();
            if (available) {
                return AjaxResult.success("AI服务连接正常");
            } else {
                return AjaxResult.error("AI服务连接失败");
            }
        } catch (Exception e) {
            return AjaxResult.error("AI服务测试失败: " + e.getMessage());
        }
    }
}
