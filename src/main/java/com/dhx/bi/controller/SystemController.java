package com.dhx.bi.controller;

import com.dhx.bi.common.BaseResponse;
import com.dhx.bi.utils.ResultUtil;
import com.dhx.bi.webSocket.WebSocketServer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashSet;
import java.util.Map;

/**
 * @author adorabled4
 * @className WebSocketController
 * @date : 2023/08/17/ 19:13
 **/
@Controller
public class SystemController {

    @Resource
    WebSocketServer webSocketServer;

    /**
     * 推送消息 推送数据到websocket客户端 接口
     *
     * @param userId  userId
     * @param message 消息
     * @return {@link Map}
     */
    @GetMapping("/socket/push/{userId}")
    public BaseResponse pushMessage(@PathVariable("userId") String userId, String message) {
//        Map<String, Object> result = new HashMap<>();
        try {
            HashSet<String> userIds = new HashSet<>();
            userIds.add(userId);
            webSocketServer.sendMessage("服务端推送消息：" + message, userIds);
//            result.put("code", userId);
//            result.put("msg", message);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ResultUtil.success();
    }

}
