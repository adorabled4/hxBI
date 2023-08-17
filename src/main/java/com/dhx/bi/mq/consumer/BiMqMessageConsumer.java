package com.dhx.bi.mq.consumer;

import com.dhx.bi.common.ErrorCode;
import com.dhx.bi.common.constant.AIConstant;
import com.dhx.bi.common.constant.BiMqConstant;
import com.dhx.bi.common.exception.BusinessException;
import com.dhx.bi.manager.AiManager;
import com.dhx.bi.model.DO.ChartEntity;
import com.dhx.bi.model.enums.ChartStatusEnum;
import com.dhx.bi.service.ChartService;
import com.dhx.bi.utils.ThrowUtils;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author adorabled4
 * @className BiMqMessageConsumer
 * @date : 2023/08/17/ 10:52
 **/
@Component
@Slf4j
public class BiMqMessageConsumer {

    @Resource
    AiManager aiManager;

    @Resource
    ChartService chartService;

    @RabbitListener(queues = BiMqConstant.AI_GENCHART_QUEUE, ackMode = "MANUAL")
    private void receiveMessage(String message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliverTag) throws IOException {
        log.info("receive message :{}", message);
        if (StringUtils.isBlank(message)) {
            // reject message
            channel.basicNack(deliverTag, false, false);
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "接收到的消息为空!");
        }
        long chartId = Long.parseLong(message);
        ChartEntity chartEntity = chartService.getById(chartId);
        if (chartEntity == null) {
            // reject message
            channel.basicNack(deliverTag, false, false);
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "图表为空!");
        }
        try {
            ChartEntity genChartEntity = new ChartEntity();
            genChartEntity.setId(chartId);
            genChartEntity.setStatus(ChartStatusEnum.RUNNING.getStatus());
            boolean b = chartService.updateById(genChartEntity);
            // throw异常
            ThrowUtils.throwIf(!b, new BusinessException(ErrorCode.SYSTEM_ERROR, "修改图表状态信息失败 " + chartId));
            String userInput = buildUserInput(chartEntity);
            // 系统预设 ( 简单预设 )
            /* 较好的做法是在系统（模型）层面做预设效果一般来说，会比直接拼接在用户消息里效果更好一些。*/
            String result = aiManager.doChat(userInput.toString(), AIConstant.BI_MODEL_ID);
            String[] split = result.split("【【【【【");
            // 第一个是 空字符串
            if (split.length < 3) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "AI 生成错误!");
            }
            // 图表代码
            String genChart = split[1].trim();
            // 分析结果
            String genResult = split[2].trim();
            // 更新数据
            ChartEntity updateChartResult = new ChartEntity();
            updateChartResult.setId(chartId);
            updateChartResult.setGenChart(genChart);
            updateChartResult.setGenResult(genResult);
            updateChartResult.setStatus(ChartStatusEnum.SUCCEED.getStatus());
            boolean updateGenResult = chartService.updateById(updateChartResult);
            ThrowUtils.throwIf(!updateGenResult, ErrorCode.SYSTEM_ERROR, "生成图表保存失败!");
        } catch (BusinessException e) {
            // reject
            channel.basicNack(deliverTag,false,false);
            ChartEntity updateChartResult = new ChartEntity();
            updateChartResult.setId(chartId);
            updateChartResult.setStatus(ChartStatusEnum.FAILED.getStatus());
            updateChartResult.setExecMessage(e.getDescription());
            boolean updateResult = chartService.updateById(updateChartResult);
            if (!updateResult) {
                log.info("更新图表FAILED状态信息失败 , chatId:{}", updateChartResult.getId());
            }
        }
        channel.basicAck(deliverTag, false);
    }

    private String buildUserInput(ChartEntity chart) {
        // 获取CSV
        // 构造用户输入
        StringBuilder userInput = new StringBuilder("");
        // 拼接图表类型;
        String userGoal = chart.getGoal();
        String chartType = chart.getChartType();
        String csvData = chart.getChartData();
        if (StringUtils.isNotBlank(chartType)) {
            userGoal += ", 请使用 " + chartType;
        }
        userInput.append("分析需求: ").append('\n');
        userInput.append(userGoal).append("\n");
        userInput.append("原始数据：").append("\n");
        userInput.append(csvData).append("\n");
        return userInput.toString();
    }

}
