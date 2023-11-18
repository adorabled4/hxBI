package com.dhx.bi.controller;

import com.dhx.bi.manager.SparkManager;
import com.dhx.bi.model.DTO.spark.SparkChatListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import javax.annotation.Resource;
import java.util.concurrent.CompletableFuture;

/**
 * @author adorabled4
 * @className ChatController
 * @date : 2023/11/18/ 21:59
 **/
@RestController
@Slf4j
public class ChatController {

    @Resource
    SparkManager sparkManager;

    @GetMapping(value = "/test/spark", produces = {MediaType.TEXT_EVENT_STREAM_VALUE})
    public SseEmitter chat(@RequestParam("question") String question) {
        long userId = 132;
        final SseEmitter emitter = sparkManager.getConn(userId);
        CompletableFuture.runAsync(()->{
            StringBuilder answerCache = new StringBuilder();
            SparkChatListener sparkChatListener = sparkManager.doChat(userId, question, answerCache);
            int lastIdx = 0, len = 0;
            try {
                while (!sparkChatListener.getWsCloseFlag()) {
                    if(lastIdx < (len = answerCache.length())){
                        emitter.send(answerCache.substring(lastIdx, len).getBytes());
                        lastIdx = len;
                    }
                    Thread.sleep(100);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        return emitter;
    }

}
