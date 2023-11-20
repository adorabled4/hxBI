package com.dhx.bi.controller;

import com.dhx.bi.common.ErrorCode;
import com.dhx.bi.manager.SparkManager;
import com.dhx.bi.model.DTO.spark.SparkChatListener;
import com.dhx.bi.model.enums.PointChangeEnum;
import com.dhx.bi.service.PointService;
import com.dhx.bi.utils.ThrowUtils;
import com.dhx.bi.utils.UserHolder;
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

    @Resource
    PointService pointService;

    @GetMapping(value = "/chat/spark", produces = {MediaType.TEXT_EVENT_STREAM_VALUE})
    public SseEmitter chat(@RequestParam("question") String question) {
        long userId = UserHolder.getUser().getUserId();
        final SseEmitter emitter = sparkManager.getConn(userId);
        // 扣除对应的积分
        boolean deduct = pointService.checkAndDeduct(userId, PointChangeEnum.CHAT_DEDUCT);
        ThrowUtils.throwIf(!deduct, ErrorCode.NO_AUTH_ERROR,"积分余额不足!");
        CompletableFuture.runAsync(()->{
            StringBuilder answerCache = new StringBuilder();
            SparkChatListener sparkChatListener = sparkManager.doChat(userId, question, answerCache);
            int lastIdx = 0, len = 0;
            try {
                while (!sparkChatListener.getWsCloseFlag()) {
                    if(lastIdx < (len = answerCache.length())){
                        emitter.send(answerCache.substring(lastIdx, len).getBytes(),MediaType.TEXT_EVENT_STREAM);
                        lastIdx = len;
                    }
                    Thread.sleep(100);
                }
                log.info("chat结束 , answer: {}",answerCache);
                emitter.complete();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        return emitter;
    }

}
