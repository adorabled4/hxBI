package com.dhx.bi.controller;

import com.dhx.bi.common.BaseResponse;
import com.dhx.bi.common.ErrorCode;
import com.dhx.bi.model.DO.ChartLogEntity;
import com.dhx.bi.model.DTO.ChartLogDTO;
import com.dhx.bi.service.ChartLogService;
import com.dhx.bi.utils.ResultUtil;
import com.dhx.bi.utils.ThrowUtils;
import com.dhx.bi.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author adorabled4
 * @className ChartLogController
 * @date : 2023/09/03/ 13:54
 **/
@RestController
@Slf4j
public class ChartLogController {

    @Resource
    ChartLogService logService;


    @GetMapping("/chart/log")
    public BaseResponse<List<ChartLogDTO>> getLastDayLog(@RequestParam("count")Integer dayCount){
        ThrowUtils.throwIf(dayCount<0 || dayCount>90, ErrorCode.PARAMS_ERROR,"请求天数错误!");
        Long userId = UserHolder.getUser().getUserId();
        List<ChartLogDTO> logs=  logService.getLogs(dayCount,userId);
        return ResultUtil.success(logs);
    }

}
