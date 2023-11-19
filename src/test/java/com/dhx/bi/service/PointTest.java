package com.dhx.bi.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.dhx.bi.common.BaseResponse;
import com.dhx.bi.model.DO.PointChangeEntity;
import com.dhx.bi.model.DO.UserEntity;
import com.dhx.bi.model.DTO.user.VerifyCodeRegisterRequest;
import com.dhx.bi.model.enums.PointChangeEnum;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * @author adorabled4
 * @className PointTest
 * @date : 2023/11/19/ 23:47
 **/
// https://blog.csdn.net/m0_49194578/article/details/123571893
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringRunner.class)
public class PointTest {


    /*
Springboot集成了websocket以后使用单元测试时会报错
javax.websocket.server.ServerContainer not available
这是由单元测试时并不会启动服务器，所以造成websocket报错
我们可以在测试注解中添加配置，让他启动以一个服务器环境
原文链接：https://blog.csdn.net/m0_49194578/article/details/123571893
    * */
    @Resource
    PointService pointService;

    @Resource
    UserService userService;

    @Resource
    PointChangeService pointChangeService;

    @Test
    @Transactional
    public void Test() {
        UserEntity user = new UserEntity();
        user.setUserId(System.currentTimeMillis());
        user.setUserName("test");
        user.setEmail("test123@test.com");
        user.setUserId(System.currentTimeMillis());
        userService.save(user);
        boolean alreadyGetted1 = pointService.isAlreadyGetted(user.getUserId());
        assert !alreadyGetted1;

        boolean dailyLoginPoint = pointService.getDailyLoginPoint(user.getUserId());
        assert dailyLoginPoint;

        boolean alreadyGetted2 = pointService.isAlreadyGetted(user.getUserId());
        assert !alreadyGetted2;

        boolean deduct = pointService.checkAndDeduct(user.getUserId(), PointChangeEnum.CHAT_DEDUCT);
        assert deduct;

        PointChangeEntity change = pointChangeService.getOne(new QueryWrapper<PointChangeEntity>().eq("user_id", user.getUserId())                        .eq("reason",PointChangeEnum.CHAT_DEDUCT.getReason())
        );
        assert change.getNewPoints() == 48;
    }

}
