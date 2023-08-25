package com.dhx.bi.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dhx.bi.model.DO.ChartEntity;
import com.dhx.bi.model.document.Chart;
import com.dhx.bi.repository.ChartRepository;
import com.dhx.bi.service.ChartService;
import com.dhx.bi.mapper.ChartMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author dhx
 * @description 针对表【t_chart(图表表)】的数据库操作Service实现
 * @createDate 2023-08-01 14:42:26
 */
@Service
public class ChartServiceImpl extends ServiceImpl<ChartMapper, ChartEntity>
        implements ChartService {

    @Resource
    ChartRepository chartRepository;

    @Override
    public boolean saveDocument(Chart chart) {
        Chart save = chartRepository.save(chart);
        return true;
    }
}




