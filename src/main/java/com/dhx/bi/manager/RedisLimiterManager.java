package com.dhx.bi.manager;

import com.dhx.bi.common.ErrorCode;
import com.dhx.bi.common.exception.BusinessException;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author adorabled4
 * @className RedisLimiterManager 专门提供Redislimiter 基础服务的manager(提供通用的服务,并非当前项目独有)
 * @date : 2023/08/14/ 19:18
 **/
@Service
public class RedisLimiterManager {

    @Resource
    private RedissonClient redissonClient;

    /**
     * 做速率限制
     *
     * @param key 用来区分不同的限流, 比如对于IP限流, 对于用户ID限流等
     */
    public void doRateLimit(String key){
        RRateLimiter rateLimiter = redissonClient.getRateLimiter(key);
        // create a limiter : twice a second
        rateLimiter.trySetRate(RateType.OVERALL,2, 1,RateIntervalUnit.SECONDS);
        // 1 token per operation
        boolean isAcquire = rateLimiter.tryAcquire(1);
        if(isAcquire){
            throw new BusinessException(ErrorCode.TOO_MANY_REQUEST);
        }
    }


}
