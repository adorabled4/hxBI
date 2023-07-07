package com.dhx.bi.manager;

import com.dhx.bi.common.ErrorCode;
import com.dhx.bi.common.exception.BusinessException;
import com.yupi.yucongming.dev.client.YuCongMingClient;
import com.yupi.yucongming.dev.common.BaseResponse;
import com.yupi.yucongming.dev.model.DevChatRequest;
import com.yupi.yucongming.dev.model.DevChatResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author adorabled4
 * @className AiManager
 * @date : 2023/07/07/ 13:30
 **/
@Component
@Slf4j
public class AiManager {

    @Resource
    YuCongMingClient yuClient;


    /**
     * chat
     *
     * @param message 消息
     */
    public String doChat(String message,Long modelId){
        DevChatRequest chatRequest = new DevChatRequest();
        chatRequest.setMessage(message);
        chatRequest.setModelId(modelId);
        BaseResponse<DevChatResponse> response = yuClient.doChat(chatRequest);
        if(response==null){
            log.error("AI 响应错误!");
        }
        if(response.getCode()!=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,response.getMessage()+ ": "+message.length());
        }
        return response.getData().getContent();
    }

}
