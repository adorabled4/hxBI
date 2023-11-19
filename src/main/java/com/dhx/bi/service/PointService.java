package com.dhx.bi.service;

import com.dhx.bi.model.DO.PointEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.dhx.bi.model.enums.PointChangeEnum;

/**
* @author dhx
* @description 针对表【points】的数据库操作Service
* @createDate 2023-11-19 13:24:09
*/
public interface PointService extends IService<PointEntity> {

    /**
     * 核对用户积分是否足够并扣除
     *
     * @param userId          用户id
     * @param pointChangeEnum 点变换枚举
     * @return boolean true:计分足够 false:积分不足
     */
    boolean checkAndDeduct(long userId , PointChangeEnum pointChangeEnum);

}
