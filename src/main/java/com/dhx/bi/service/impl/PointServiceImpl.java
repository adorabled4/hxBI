package com.dhx.bi.service.impl;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.dhx.bi.common.ErrorCode;
import com.dhx.bi.common.constant.BiMqConstant;
import com.dhx.bi.model.DO.PointChangeEntity;
import com.dhx.bi.model.DO.PointEntity;
import com.dhx.bi.model.enums.PointChangeEnum;
import com.dhx.bi.service.PointChangeService;
import com.dhx.bi.service.PointService;
import com.dhx.bi.mapper.PointMapper;
import com.dhx.bi.utils.ThrowUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
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

    @Resource
    RabbitTemplate rabbitTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean checkAndDeduct(long userId, PointChangeEnum pointChangeEnum) {
        return operatePointAndSave(userId, pointChangeEnum);
    }


    @Override
    public boolean getDailyLoginPoint(Long userId) {
        // 校验是否已经领取过
        if (isAlreadyGetted(userId)) return false;
        PointChangeEnum pointChangeEnum = PointChangeEnum.DAILY_LOGIN_ADD;
        // 查询积分
        return operatePointAndSave(userId, pointChangeEnum);
    }

    @Override
    public boolean compensatePoint(Long userId, PointChangeEnum pointChangeEnum) {
        return operatePointAndSave(userId, pointChangeEnum);
    }

    @Override
    public void sendCompensateMessage(Long userId, PointChangeEnum pointChangeEnum) {
        rabbitTemplate.convertAndSend(BiMqConstant.BI_EXCHANGE_NAME, BiMqConstant.COMPENSATE_POINT_ROUTING_KEY,
                new JSONObject().set("userId", userId).set("pointChangeEnum", pointChangeEnum));

    }

    @Override
    public boolean isAlreadyGetted(Long userId) {
        // 获取今天的日期
        LocalDate today = LocalDate.now();
        PointChangeEntity point = pointChangeService.getOne(
                new QueryWrapper<PointChangeEntity>()
                        .eq("user_id", userId)
                        .eq("reason", PointChangeEnum.DAILY_LOGIN_ADD.getReason())
                        .ge("create_time", today.atStartOfDay())  // 大于等于今天的开始时间
                        .lt("create_time", today.plusDays(1).atStartOfDay())  // 小于今天的开始时间
        );
        return point != null;
    }


    /**
     * 操作点并保存记录
     *
     * @param userId          用户id
     * @param pointChangeEnum 积分change枚举
     * @return boolean true表示操作成功, false表示操作失败
     */
    private boolean operatePointAndSave(long userId, PointChangeEnum pointChangeEnum) {
        // 查询积分
        PointEntity point = getOne(new QueryWrapper<PointEntity>().eq("user_id", userId));
        if (point == null) {
            point = new PointEntity();
            point.setUserId(userId);
            point.setTotalPoints(0);
            point.setRemainingPoints(0);
            point.setStatus(PointEntity.Status.VALID.ordinal());
            point.setLastOperationTime(LocalDateTime.now());
            point.setCreateTime(LocalDateTime.now());
            ThrowUtils.throwIf(!save(point), ErrorCode.SYSTEM_ERROR, "保存用户积分信息失败!");
        }
        // 操作积分
        if (pointChangeEnum.getChangeType() == PointChangeEnum.ChangeType.INCREASE) {
            point.setTotalPoints(point.getTotalPoints() + pointChangeEnum.getChangeAmount());
            point.setRemainingPoints(point.getRemainingPoints() + pointChangeEnum.getChangeAmount());
        } else if (pointChangeEnum.getChangeType() == PointChangeEnum.ChangeType.DECREASE) {
            // 扣除
            if (point.getRemainingPoints() < pointChangeEnum.getChangeAmount()) {
                return false;
            }
            point.setRemainingPoints(point.getRemainingPoints() - pointChangeEnum.getChangeAmount());
        }
        // 写入相关积分记录
        PointChangeEntity pointChangeEntity = new PointChangeEntity(pointChangeEnum, userId);
        pointChangeEntity.setNewPoints(point.getRemainingPoints());
        // 保存
        pointChangeService.save(pointChangeEntity);
        updateById(point);
        return true;
    }
}




