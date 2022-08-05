package com.client.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.client.common.BaseResponse;
import com.client.common.ErrorCode;
import com.client.common.ResultUtils;
import com.client.exception.BusinessException;
import com.client.model.VO.UserVO;
import com.client.model.domain.Users;
import com.client.model.request.userLoginRequest;
import com.client.model.request.userRegisterRequest;
import com.client.service.UsersService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.client.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户接口
 *
 * @ClassName: UserController
 * @author: mafangnian
 * @date: 2022/7/31 12:07
 * @Blog: null
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Resource
    private UsersService usersService;

    @Resource
    private RedisTemplate redisTemplate;

    @GetMapping("/current")
    public BaseResponse<Users> getCurrentUser(HttpServletRequest request){
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        Users currentUser = (Users) userObj;
        if (currentUser == null){
            throw new BusinessException(ErrorCode.NO_LOGIN);
        }
        long userId = currentUser.getId();
        // todo 用户校验 用户是否合法
        Users user = usersService.getById(userId);
        Users safetyUser = usersService.getSafetyUser(user);
        return ResultUtils.success(safetyUser);
    }

    @PostMapping("/register")
    public BaseResponse<Long> register(@RequestBody userRegisterRequest userRegisterRequest) {
        if (userRegisterRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAllBlank(userAccount, userPassword, checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"注册失败");
        }

        long result = usersService.usersRegister(userAccount, userPassword, checkPassword);
        if (result>0){
            return ResultUtils.success(result);
        }
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR,"注册失败");

    }

    @PostMapping("/login")
    public BaseResponse<Users> login(@RequestBody userLoginRequest userLoginRequest, HttpServletRequest request) {
        if (userLoginRequest == null) {
            throw new BusinessException(ErrorCode.NO_LOGIN);
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"登录失败");
        }

        Users users = usersService.userLogin(userAccount, userPassword, request);
        if (users!=null){
            return ResultUtils.success(users);
        }
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR,"登录失败");
    }

    @PostMapping("/logout")
    public BaseResponse<Integer> logout(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.NO_LOGIN);
        }
        int result = usersService.userLogout(request);
        if (result>0){
            return ResultUtils.success(result);
        }
        return ResultUtils.error(ErrorCode.PARAMS_ERROR,"退出失败");
    }

    @GetMapping("/search")
    public BaseResponse<List<Users>> searchUsers(@RequestParam String username, HttpServletRequest request) {

        //鉴权
        if (!usersService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }

        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
        if (StringUtils.isNotBlank(username)) {
            queryWrapper.like("username",username);
        }
        List<Users> usersList = usersService.list(queryWrapper);

        List<Users> list = usersList.stream().map(users -> usersService.getSafetyUser(users)).collect(Collectors.toList());
        if (list.size()>0){
            return ResultUtils.success(list);
        }
        return ResultUtils.error(ErrorCode.PARAMS_ERROR,"查询失败");
    }

    @GetMapping("/recommend")
    public BaseResponse<Page<Users>> recommendUsers(long pageSize, long pageNum, HttpServletRequest request) {

        Users loginUser = usersService.getLoginUser(request);
        ValueOperations<String,Object> valueOperations = redisTemplate.opsForValue();
        //设置key
        String redisKey = String.format("yupao:user:recommend:%s",loginUser.getId());
        //如果有缓存直接从缓存中取
        Page<Users> usersPage = (Page<Users>) valueOperations.get(redisKey);
        if (usersPage!=null){
            return ResultUtils.success(usersPage);
        }
        //无缓存,查询数据库
        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
        Page<Users> usersList = usersService.page(new Page<>(pageNum,pageSize), queryWrapper);
        //写缓存
        try {
            //测试每30s过期
            valueOperations.set(redisKey,usersList,5, TimeUnit.SECONDS);
        }catch (Exception e){
            log.error("redis set key error",e);
        }

        if (usersList.getSize()>0){
            return ResultUtils.success(usersList);
        }
        return ResultUtils.error(ErrorCode.PARAMS_ERROR,"查询失败");
    }

    @GetMapping("/search/tags")
    public BaseResponse<List<Users>> searchUsersByTags(@RequestParam(required = false) List<String> tagNameList){
        if (CollectionUtils.isEmpty(tagNameList)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<Users> usersList = usersService.searchUsersByTags(tagNameList);
        if (usersList.size() > 0){
            return ResultUtils.success(usersList);
        }
        return ResultUtils.error(ErrorCode.PARAMS_ERROR,"查询失败!");
    }

    @PostMapping("/delete")
    public BaseResponse deleteUser(@RequestBody long id, HttpServletRequest request) {

        //鉴权
        if (!usersService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Users loginUser = usersService.getLoginUser(request);
        boolean result = usersService.removeById(id);
        //删除缓存
        String redisKey = String.format("yupao:user:recommend:%s",loginUser.getId());
        redisTemplate.delete(redisKey);
        if (result){
            return ResultUtils.success(result);
        }else {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR,"请求错误");
        }
    }

    @PostMapping("/update")
    public BaseResponse<Integer> updateUser(@RequestBody Users user,HttpServletRequest request){
        //1.校验参数是否异常
        if (user == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Users loginUser = usersService.getLoginUser(request);
        int result = usersService.updateUser(user,loginUser);
        //删除缓存
        String redisKey = String.format("yupao:user:recommend:%s",loginUser.getId());
        redisTemplate.delete(redisKey);
        if (result>0){
            return ResultUtils.success(result);
        }
        return ResultUtils.error(ErrorCode.NULL_ERROR,"更新失败");
    }


    @GetMapping("/match")
    public BaseResponse<List<Users>> matchUsers(long num, HttpServletRequest request){
        if (num<=0 || num>20){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Users loginUser = usersService.getLoginUser(request);
        return  ResultUtils.success(usersService.matchUsers(num,loginUser));
    }
}
