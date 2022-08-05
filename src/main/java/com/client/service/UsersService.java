package com.client.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.client.model.VO.UserVO;
import com.client.model.domain.Users;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author 29769
 * @description 针对表【users】的数据库操作Service
 * @createDate 2022-07-28 23:30:22
 */
public interface UsersService extends IService<Users> {


    /**
     * 用户注册
     *
     * @param userAccount   用户账号
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 新用户ID
     */
    long usersRegister(String userAccount, String userPassword, String checkPassword);

    /**
     * 用户登录
     *
     * @param userAccount  用户账号
     * @param userPassword 用户密码
     * @param request      获得session数据
     * @return 脱敏后的用户信息
     */
    Users userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 用户脱敏
     *
     * @param user
     * @return
     */
    Users getSafetyUser(Users user);

    /**
     * 退出
     *
     * @param request
     * @return
     */
    int userLogout(HttpServletRequest request);

    /**
     * 根据用户标签搜索用户
     * 内存
     * @param tagList
     * @return
     */
    List<Users> searchUsersByTags(List<String> tagList);


    /**
     * 根据用户标签搜索用户(by Sql)
     *
     * @param tagNameList
     * @return
     */
    List<Users> searchUsersByTagsSql(List<String> tagNameList);


    /**
     * 更新用户信息
     * @param user
     * @return
     */
    int updateUser(Users user,Users loginUser);

    /**
     * 当前是否为管理员
     * @param request
     * @return
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * isAdmin 方法重载
     * @param loginUser
     * @return
     */
    boolean isAdmin(Users loginUser);

    /**
     * 获得当前登录用户信息
     * @return
     */
    Users getLoginUser(HttpServletRequest request);

    /**
     * 获得标签最相近的用户匹配
     * @param num
     * @param loginUser
     * @return
     */
    List<Users> matchUsers(long num, Users loginUser);

}

