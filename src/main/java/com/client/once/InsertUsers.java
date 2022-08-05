package com.client.once;
import java.util.Date;

import com.client.mapper.UsersMapper;
import com.client.model.domain.Users;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import javax.annotation.Resource;

/**
 * @ClassName: InsertUsers
 * @author: mafangnian
 * @date: 2023/5/25 11:13
 */

@Component
public class InsertUsers {

    @Resource
    private UsersMapper usersMapper;

    /**
     * 批量插入用户
     * 定时任务 5s后执行插入这么多数据
     * fixedRate = Long.MAX_VALUE 执行频率最大值
     */
//    @Scheduled(initialDelay = 5000, fixedRate = Long.MAX_VALUE)
    private void doInsertUsers(){
        StopWatch stopWatch = new StopWatch();
        final int INSERT_NUM = 1000000;
        for (int i = 0; i < INSERT_NUM; i++) {
            Users users = new Users();
            users.setUsername("小丑假用户");
            users.setUserAccount("fakeUser");
            users.setAvatarUrl("https://lulyqqqq.github.io/Gallery/leimu/index/02.png");
            users.setGender(0);
            users.setUserPassword("12345678");
            users.setPhone("123456789");
            users.setEmail("123456789@qq.com");
            users.setUserStatus(0);
            users.setUserRole(0);
            users.setTags("[]");
            users.setProfile("这是自我介绍内容");
            users.setPlanetCode("");


        }
    }
}
