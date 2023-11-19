package com.dhx.bi.controller;

import com.dhx.bi.common.BaseResponse;
import com.dhx.bi.model.DTO.user.UserDTO;
import com.dhx.bi.service.PointService;
import com.dhx.bi.utils.ResultUtil;
import com.dhx.bi.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author adorabled4
 * @className PointController
 * @date : 2023/11/19/ 23:20
 **/
@RestController
@Slf4j
public class PointController {

    @Resource
    PointService pointService;

    @GetMapping("/get/daily/point")
    public BaseResponse getDailyLoginPoint() {
        UserDTO user = UserHolder.getUser();
        Long userId = user.getUserId();
        boolean res = pointService.getDailyLoginPoint(userId);
        if(res){
            return ResultUtil.success("领取成功!");
        }else{
            return ResultUtil.success("领取失败,请勿重复领取!");
        }
    }
}
