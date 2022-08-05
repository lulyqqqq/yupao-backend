package com.client.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.client.model.domain.UserTeam;
import com.client.mapper.UserTeamMapper;
import com.client.service.UserTeamService;
import org.springframework.stereotype.Service;

/**
* @author 29769
* @description 针对表【user_team(用户队伍关系)】的数据库操作Service实现
* @createDate 2023-05-26 20:05:20
*/
@Service
public  class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService {
}




