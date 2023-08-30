package com.dhx.bi.service.execution;

import com.dhx.bi.model.DO.ChartEntity;
import com.dhx.bi.model.DTO.chart.BiResponse;
import com.dhx.bi.mq.producer.BiMqMessageProducer;
import com.dhx.bi.service.GenChartStrategy;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 通过MQ异步消息生成
 *
 * @author adorabled4
 * @className GenChartSync
 * @date 2023/08/30
 */
@Component(value = "gen_mq")
public class GenChartMQ implements GenChartStrategy {

    @Resource
    BiMqMessageProducer biMqMessageProducer;


    @Override
    public BiResponse executeGenChart(ChartEntity chartEntity) {
        long newChartId = chartEntity.getId();
        biMqMessageProducer.sendGenChartMessage(String.valueOf(newChartId));
        BiResponse biResponse = new BiResponse();
        biResponse.setChartId(newChartId);
        return biResponse;
    }
}
