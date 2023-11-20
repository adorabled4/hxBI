package com.dhx.bi.mq.consumer;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.dhx.bi.common.ErrorCode;
import com.dhx.bi.common.constant.AIConstant;
import com.dhx.bi.common.constant.BiMqConstant;
import com.dhx.bi.common.exception.BusinessException;
import com.dhx.bi.common.exception.GenChartException;
import com.dhx.bi.manager.AiManager;
import com.dhx.bi.model.DO.ChartEntity;
import com.dhx.bi.model.enums.ChartStatusEnum;
import com.dhx.bi.model.enums.PointChangeEnum;
import com.dhx.bi.service.ChartLogService;
import com.dhx.bi.service.ChartService;
import com.dhx.bi.service.PointChangeService;
import com.dhx.bi.service.PointService;
import com.dhx.bi.utils.ChartUtil;
import com.dhx.bi.utils.ThrowUtils;
import com.dhx.bi.webSocket.WebSocketServer;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;

/**
 * @author adorabled4
 * @className BiMqMessageConsumer
 * @date : 2023/08/17/ 10:52
 **/
@Component
@Slf4j
public class CompensatePointConsumer {

    @Resource
    PointService pointService;

    @RabbitListener(bindings = @QueueBinding(value = @Queue(name = BiMqConstant.COMPENSATE_POINT_QUEUE_NAME), exchange = @Exchange(name = BiMqConstant.BI_EXCHANGE_NAME, type = ExchangeTypes.DIRECT), key = BiMqConstant.COMPENSATE_POINT_ROUTING_KEY))
    public void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliverTag) throws IOException {
        log.info("receive message :{}", message);
        if (StringUtils.isBlank(message)) {
            // reject message
            channel.basicNack(deliverTag, false, false);
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接收到的消息为空!");
        }
        JSONObject jsonObject = new JSONObject(message);
        long userId = Long.parseLong((String) jsonObject.get("userId"));
        PointChangeEnum pointChangeEnum = JSONUtil.toBean((String) jsonObject.get("pointChangeEnum"), PointChangeEnum.class);
        ThrowUtils.throwIf(!pointService.compensatePoint(userId, pointChangeEnum), ErrorCode.SYSTEM_ERROR,
                String.format("补偿用户积分操作失败:%s", jsonObject));
    }


}
