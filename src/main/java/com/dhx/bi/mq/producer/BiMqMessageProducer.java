package com.dhx.bi.mq.producer;

import com.dhx.bi.common.constant.BiMqConstant;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author adorabled4
 * @className BiMqMessageProducer
 * @date : 2023/08/17/ 11:07
 **/
@Component
public class BiMqMessageProducer {

    @Resource
    RabbitTemplate rabbitTemplate;

    /**
     * 发送生成图表消息
     *
     * @param message 消息
     */
    public void sendGenChartMessage(String message){
        rabbitTemplate.convertAndSend(BiMqConstant.BI_EXCHANGE_NAME,BiMqConstant.BI_ROUTING_KEY,message);
    }
}
