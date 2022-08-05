package com.client.job;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.client.mapper.UsersMapper;
import com.client.model.domain.Users;
import com.client.service.UsersService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: PreCaCheJob
 * @author: mafangnian
 * @date: 2023/5/26 10:45
 *
 * 缓存预热
 */

@Component
@Slf4j
public class PreCaCheJob {

    @Resource
    private UsersMapper usersMapper;

    @Resource
    private UsersService usersService;
    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private RedissonClient redissonClient;

    //重点用户
    private List<Long> mainUserList = Arrays.asList(1L);

    /**
     * 每天23:58分执行预热重点用户推荐数据
     * redisson中拥有看门狗机制,----当多个线程或者服务器去抢占同一个服务器资源(访问接口或者内存数据)
     * 使用redissonClient创建锁对象，设置锁的过期时间为-1，默认使用看门狗机制
     * 意思是：当抢到锁的线程或者服务器在执行这端代码在规定的锁过期的时间段内未执行完代码,则redisson会自动将锁的过期时间续期
     * 一般续期时间未30s 为什么设置续期时间为30s---放在服务器宕机 延迟
     */
    @Scheduled(cron = "0 58 23 * * *")
    public void doCaCheReCommend(){

        RLock lock = redissonClient.getLock("yupao:precachejob:docache:lock");
        try {
            //第二个参数设置为-1 意思就是使用redisson里的看门狗机制
            if (lock.tryLock(0,-1,TimeUnit.MILLISECONDS)){
                for (Long userId:mainUserList){
                    QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
                    Page<Users> usersList = usersService.page(new Page<>(1,20), queryWrapper);
                    ValueOperations<String,Object> valueOperations = redisTemplate.opsForValue();
                    //设置key
                    String redisKey = String.format("yupao:user:recommend:%s",userId);
                    //写缓存
                    try {
                        //测试每60过期
                        valueOperations.set(redisKey,usersList,60, TimeUnit.MINUTES);
                    }catch (Exception e){
                        log.error("redis set key error",e);
                    }
                }
            }
        }catch (InterruptedException e){
            e.printStackTrace();
            log.error("yupao:precachejob:docache:lock",e);
        }finally {
            //释放某个服务器自己的锁 释放锁是一定要执行的
            if (lock.isHeldByCurrentThread()){
                lock.unlock();
            }
        }

    }

}
