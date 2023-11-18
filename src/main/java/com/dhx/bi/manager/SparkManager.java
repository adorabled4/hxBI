package com.dhx.bi.manager;


import cn.hutool.json.JSONUtil;
import com.dhx.bi.model.DTO.spark.ChatRequest;
import com.dhx.bi.model.DTO.spark.SparkChatListener;
import com.dhx.bi.config.SparkConfig;
import com.dhx.bi.utils.AuthUtil;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author adorabled4
 * @className SparkManager
 * @date : 2023/11/17/ 10:54
 **/
@Service
@Slf4j
public class SparkManager {

    @Resource
    SparkConfig sparkConfig;

    private static final Map<Object, SseEmitter> SSE_CACHE = new ConcurrentHashMap<>();

    public SparkChatListener doChat(long userId, String question, StringBuilder answer) {
        // 构建鉴权url
        String authUrl = AuthUtil.genAuthUrl(sparkConfig.getApiKey(), sparkConfig.getApiSecret(),
                sparkConfig.getHost(), sparkConfig.getPath());
        if (authUrl == null) {
            throw new RuntimeException("authUrl 生成失败 !");
        }
        OkHttpClient client = new OkHttpClient.Builder().build();
        // 构建聊天请求
        ChatRequest chatRequest = buildChatRequest(userId, question);
        System.out.println(JSONUtil.toJsonStr(chatRequest));
        // 构建websocket请求
        Request request = new Request.Builder().url(authUrl).build();
        SparkChatListener sparkChat = new SparkChatListener(answer);
        // 发起websocket请求
        WebSocket webSocket = client.newWebSocket(request, sparkChat);
        webSocket.send(JSONUtil.toJsonStr(chatRequest));
        return sparkChat;
    }

    /**
     * 获取SseEmitter对象
     *
     * @param key 关键
     * @return {@link SseEmitter}
     */
    public SseEmitter getConn(Object key) {
        final SseEmitter sseEmitter = SSE_CACHE.get(key);
        if (sseEmitter != null) {
            return sseEmitter;
        } else {
            // 设置连接超时时间，需要配合配置项 spring.mvc.async.request-timeout: 600000 一起使用
            final SseEmitter emitter = new SseEmitter(600000L);
            // 注册超时回调，超时后触发
            emitter.onTimeout(() -> {
                log.info("连接已超时，正准备关闭，key = {}", key);
                SSE_CACHE.remove(key);
            });
            // 注册完成回调，调用 emitter.complete() 触发
            emitter.onCompletion(() -> {
                log.info("连接已关闭，正准备释放，key = {}", key);
                SSE_CACHE.remove(key);
                log.info("连接已释放，key = {}", key);
            });
            // 注册异常回调，调用 emitter.completeWithError() 触发
            emitter.onError(throwable -> {
                log.error("连接已异常，正准备关闭，key = {}", key, throwable);
                SSE_CACHE.remove(key);
            });
            SSE_CACHE.put(key, emitter);
            return emitter;
        }
    }

    /**
     * 构建聊天请求
     *
     * @param userId   用户id
     * @param question 问题
     * @return {@link ChatRequest}
     */
    private ChatRequest buildChatRequest(long userId, String question) {
        return ChatRequest.builder()
                .header(ChatRequest.Header.builder()
                        .app_id(sparkConfig.getAppId())
                        .uid(String.valueOf(userId))
                        .build())
                .parameter(ChatRequest.Parameter.builder()
                        .chat(ChatRequest.Chat.builder()
                                .domain(sparkConfig.getDomain())
                                .temperature(0.5)
                                .max_tokens(4096)
                                .build())
                        .build())
                .payload(ChatRequest.Payload.builder()
                        .message(ChatRequest.Message
                                .builder()
                                .text(Collections.singletonList(
                                        ChatRequest.Text.builder()
                                                .content(question)
                                                .role("user")
                                                .build()))
                                .build())
                        .build()).build();
    }

}
