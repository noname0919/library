package com.library.service;

import java.util.List;
import java.util.Map;

/**
 * AI服务接口
 */
public interface IAIService {

    /**
     * 发送消息到AI并获取回复
     *
     * @param messages 消息列表，包含role和content
     * @return AI的回复内容
     */
    String chat(List<Map<String, String>> messages);

    /**
     * 测试AI服务连接
     *
     * @return 是否连接成功
     */
    boolean testConnection();
}
