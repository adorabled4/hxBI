package com.dhx.bi.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dhx.bi.common.ErrorCode;
import com.dhx.bi.model.DO.PointChangeEntity;
import com.dhx.bi.model.DO.PointEntity;
import com.dhx.bi.model.enums.PointChangeEnum;
import com.dhx.bi.service.PointChangeService;
import com.dhx.bi.service.PointService;
import com.dhx.bi.mapper.PointMapper;
import com.dhx.bi.utils.ThrowUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;

/**
 * @author dhx
 * @description 针对表【points】的数据库操作Service实现
 * @createDate 2023-11-19 13:24:09
 */
@Service
public class PointServiceImpl extends ServiceImpl<PointMapper, PointEntity>
        implements PointService {


    @Resource
    PointChangeService pointChangeService;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean checkAndDeduct(long userId, PointChangeEnum pointChangeEnum) {
        // 查询积分
        PointEntity point = getOne(new QueryWrapper<PointEntity>().eq("user_id", userId));
        if (point == null) {
            point = new PointEntity();
            point.setUserId(userId);
            point.setTotalPoints(0);
            point.setRemainingPoints(0);
            point.setStatus(PointEntity.Status.VALID);
            point.setLastOperationTime(LocalDateTime.now());
            point.setCreateTime(LocalDateTime.now());
            ThrowUtils.throwIf(!save(point), ErrorCode.SYSTEM_ERROR, "保存用户积分信息失败!");
            return false;
        }
        // 校验
        if (point.getRemainingPoints() < pointChangeEnum.getChangeAmount()) {
            return false;
        }
        // 扣除
        point.setRemainingPoints(point.getRemainingPoints() - pointChangeEnum.getChangeAmount());
        // 写入相关积分记录
        PointChangeEntity pointChangeEntity = new PointChangeEntity(pointChangeEnum,userId);
        pointChangeEntity.setNewPoints(point.getRemainingPoints());
        // 保存
        updateById(point);
        pointChangeService.save(pointChangeEntity);
        return true;
    }
}




