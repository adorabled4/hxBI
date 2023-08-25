package com.dhx.bi;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dhx.bi.model.DO.ChartEntity;
import com.dhx.bi.model.document.Chart;
import com.dhx.bi.service.ChartService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;
import java.util.Random;

@SpringBootTest
class BIApplicationTests {

    @Resource
    ChartService chartService;

    @Test
    void contextLoads() {
        List<ChartEntity> list = chartService.list(new QueryWrapper<ChartEntity>().le("user_id", 200));
        list.forEach(item -> {
            System.out.println(item);
        });
    }


    @Test
    public void insertMockData() {
        Random random = new Random();
        List<ChartEntity> list = chartService.list();
        list.forEach(item -> {
//            for (int i = 0; i < 1000; i++) {
            ChartEntity chartEntity = new ChartEntity();
            BeanUtil.copyProperties(item, chartEntity);
            chartEntity.setName("testChart" + System.currentTimeMillis());
//                chartEntity.setId(Math.abs(random.nextLong()));
            chartEntity.setUserId((long) Math.abs(random.nextInt(5000)));
            chartService.updateById(chartEntity);
//            }
        });
    }

    @Test
    void testSelectSpeed() {

    }

    @Test
    public void insertMongo() {
        List<ChartEntity> chartEntities = chartService.list(new QueryWrapper<ChartEntity>().eq("user_id", 2).last("LIMIT 0,10"));
        chartEntities.stream().map(item -> {
            Chart chart = BeanUtil.copyProperties(item, Chart.class);
            chartService.saveDocument(chart);
            return null;
        });
    }

}
