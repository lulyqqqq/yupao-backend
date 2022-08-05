package com.client.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.client.model.VO.TeamUserVO;
import com.client.model.domain.Team;
import com.client.model.domain.Users;
import com.client.model.dto.TeamQuery;
import com.client.model.request.TeamJoinRequest;
import com.client.model.request.TeamQuitRequest;
import com.client.model.request.TeamUpdateRequest;

import java.util.List;

/**
* @author 29769
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2023-05-26 19:57:02
*/
public interface TeamService extends IService<Team> {

    /**
     *   添加队伍
     * @param team
     * @param loginUser
     * @return
     */
    long addTeam(Team team, Users loginUser);


    /**
     * 搜索队伍
     * @param teamQuery
     * @param isAdmin
     * @return
     */
    List<TeamUserVO> listTeams(TeamQuery teamQuery,  boolean isAdmin);

    /**
     * 更新队伍
     * @param teamUpdateRequest
     * @return
     */
    boolean updateTeam(TeamUpdateRequest teamUpdateRequest,Users loginUser);


    /**
     * 用户加入队伍
     * @param teamJoinRequest
     * @param loginUser
     * @return
     */
    boolean joinTeam(TeamJoinRequest teamJoinRequest, Users loginUser);


    /**
     * 退出队伍
     * @param teamQuitRequest
     * @param loginUser
     * @return
     */
    boolean quitTeam(TeamQuitRequest teamQuitRequest, Users loginUser);


    /**
     * 队长解散队伍
     * @param id
     * @param loginUser
     */
    boolean deleteTeam(long id, Users loginUser);
}
