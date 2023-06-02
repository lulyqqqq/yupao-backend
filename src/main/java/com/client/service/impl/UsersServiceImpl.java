package com.client.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.client.common.ErrorCode;
import com.client.constant.UserConstant;
import com.client.exception.BusinessException;
import com.client.mapper.UsersMapper;
import com.client.model.domain.Users;
import com.client.service.UsersService;
import com.client.utils.AlgorithmUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.client.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户中心实现类
 *
 * @author 02
 * @description 针对表【users】的数据库操作Service实现
 * @createDate 2022-07-28 23:30:22
 */
@Service
@Slf4j
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users> implements UsersService {


    /**
     * 盐值  混淆密码  加密
     */
    private static final String salt = "yupi";


    @Resource
    private UsersMapper usersMapper;

    /**
     * 用户注册
     *
     * @param userAccount   用户账号
     * @param userPassword  用户密码
     * @param checkPassword 校验密码
     * @return 用户id
     */
    @Override
    public long usersRegister(String userAccount, String userPassword, String checkPassword) {

        //1.校验注册
        if (StringUtils.isAllBlank(userAccount, userPassword, checkPassword)) {
            // todo 修改为自定义异常
            throw new BusinessException(ErrorCode.NULL_ERROR, "参数为空");

        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8 || checkPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }


        // .*[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？\\\\]+.*
        //账号不能包含特殊字符
        String regEx = "[\n`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*()——+|{}‘；：”“’。， 、？]";
        Matcher matcher = Pattern.compile(regEx).matcher(userAccount);
        if (matcher.find()) {
            return -1;
        }

        // 密码和校验密码相同
        if (!userPassword.equals(checkPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码校验不匹配");
        }

        //账号不能重复
        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        long count = usersMapper.selectCount(queryWrapper);
        if (count > 0) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR,"账号重复");
            return -1;
        }

        //2.对代码进行加密
        String newPassword = DigestUtils.md5DigestAsHex((salt + userPassword).getBytes());
        //3.插入数据
        Users users = new Users();
        users.setUserAccount(userAccount);
        users.setUserPassword(newPassword);
        boolean result = this.save(users);
        if (!result) {
            return -1;
        }
        return users.getId();
    }

    /**
     * 用户登录
     *
     * @param userAccount  用户账号
     * @param userPassword 用户密码
     * @param request      获得session数据
     * @return 用户基本信息
     */
    @Override
    public Users userLogin(String userAccount, String userPassword, HttpServletRequest request) {

        //1.校验登录
        if (StringUtils.isAllBlank(userAccount, userPassword)) {
            throw new BusinessException(ErrorCode.NULL_ERROR, "参数为空");
        }
        if (userAccount.length() < 4) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户账号过短");
        }
        if (userPassword.length() < 8) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户密码过短");
        }


        // .*[`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。，、？\\\\]+.*
        //账号不能包含特殊字符
        String regEx = "[\n`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*()——+|{}‘；：”“’。， 、？]";
        Matcher matcher = Pattern.compile(regEx).matcher(userAccount);
        if (matcher.find()) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "账户包含特殊字符");
        }

        //2.对代码进行加密
        String newPassword = DigestUtils.md5DigestAsHex((salt + userPassword).getBytes());
        //查询用户是否存在
        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("userAccount", userAccount);
        queryWrapper.eq("userPassword", newPassword);
        Users user = usersMapper.selectOne(queryWrapper);
        //用户不存在
        if (user == null) {
            log.info("user login failed;userAccount cannot match userPassword!");
            throw new BusinessException(ErrorCode.NO_LOGIN, "用户不存在");
        }

        //3.用户脱敏
        Users safetyUser = getSafetyUser(user);

        //4.记录用户登录态
        request.getSession().setAttribute(USER_LOGIN_STATE, safetyUser);


        return safetyUser;
    }


    /**
     * 用户脱敏
     *
     * @param user
     * @return
     */
    public Users getSafetyUser(Users user) {

        if (user == null) {
            throw new BusinessException(ErrorCode.NO_LOGIN, "用户不存在");
        }
        Users safetyUser = new Users();
        safetyUser.setId(user.getId());
        safetyUser.setUsername(user.getUsername());
        safetyUser.setUserAccount(user.getUserAccount());
        safetyUser.setAvatarUrl(user.getAvatarUrl());
        safetyUser.setGender(user.getGender());
        safetyUser.setPhone(user.getPhone());
        safetyUser.setEmail(user.getEmail());
        safetyUser.setUserRole(user.getUserRole());
        safetyUser.setUserStatus(user.getUserStatus());
        safetyUser.setCreateTime(user.getCreateTime());
        safetyUser.setTags(user.getTags());
        safetyUser.setProfile(user.getProfile());
        safetyUser.setPlanetCode(user.getPlanetCode());
        return safetyUser;
    }

    /**
     * 用户注销
     *
     * @param request
     */
    @Override
    public int userLogout(HttpServletRequest request) {
        //移除用户登录态
        request.getSession().removeAttribute(USER_LOGIN_STATE);
        return 1;
    }


    /**
     * 根据标签搜索用户(内存搜索)
     *
     * @param tagNameList 用户要拥有的标签
     * @return
     */
    @Override
    public List<Users> searchUsersByTags(List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //1.查询所有用户
        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
        List<Users> usersList = usersMapper.selectList(queryWrapper);
        Gson gson = new Gson();
        //判断用户的tags Json字符串中有没有要求的标签，如果都没有就不加入到搜索对应标签的用户列中
        // parallelStream() 并发流执行
        return usersList.stream().filter(users -> {
            String tagsStr = users.getTags();
            if (StringUtils.isBlank(tagsStr)) {
                return false;
            }
            Set<String> tagNameSet = gson.fromJson(tagsStr, new TypeToken<Set<String>>() {
            }.getType());
            //使用Java8中的Optional特性,判断传入可能为空的数据,如果为空就返回指定类型的参数 代替if进行判断,减少复杂度
            tagNameSet = Optional.ofNullable(tagNameSet).orElse(new HashSet<>());
            for (String tagName : tagNameList) {
                if (!tagNameSet.contains(tagName)) {
                    return false;
                }
            }
            return true;
        }).map(this::getSafetyUser).collect(Collectors.toList());
    }

    /**
     * 根据标签搜索用户(sql)
     *
     * @param tagNameList 用户要拥有的标签
     * @return
     */
    @Override
    public List<Users> searchUsersByTagsSql(List<String> tagNameList) {
        if (CollectionUtils.isEmpty(tagNameList)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //1.查询所有用户
        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
        //拼接 and 查询 sql查询
        //like '%Java%' and like '%C++%'
        for (String tagName : tagNameList) {
            queryWrapper = queryWrapper.like("tags", tagName);
        }
        List<Users> usersList = usersMapper.selectList(queryWrapper);
        return usersList.stream().map(this::getSafetyUser).collect(Collectors.toList());
    }


    public boolean isAdmin(HttpServletRequest request) {
        //鉴权
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        Users user = (Users) userObj;

        return user != null && user.getUserRole() == UserConstant.ADMIN_ROLE;
    }

    public boolean isAdmin(Users loginUser) {
        //鉴权
        return loginUser != null && loginUser.getUserRole() == UserConstant.ADMIN_ROLE;
    }

    public int updateUser(Users user, Users loginUser) {
        //1.查询
        long userId = user.getId();
        if (userId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //todo 补充更多校验 如果用户没有传任何值 需要判定

        //如果是管理员,允许更新任意用户
        //如果不是管理员,只允许更新自己
        if (!isAdmin(loginUser) && userId != loginUser.getId()) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        //更新数据
        Users oldUser = usersMapper.selectById(userId);
        if (oldUser == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }

        return usersMapper.updateById(user);
    }

    @Override
    public Users getLoginUser(HttpServletRequest request) {
        if (request == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);

        if (userObj == null) {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }

        return (Users) userObj;
    }

    /**
     * 推荐匹配用户
     *
     * @param num
     * @param loginUser
     * @return
     */
    @Override
    public List<Users> matchUsers(long num, Users loginUser) {
        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
        //只查id和tags字段存在的用户,减少查询时间
        queryWrapper.isNotNull("tags");
        queryWrapper.select("id", "tags");
        List<Users> userList = this.list(queryWrapper);

        String tags = loginUser.getTags();
        Gson gson = new Gson();
        //需要变成的目标tag列表
        List<String> tagList = gson.fromJson(tags, new TypeToken<List<String>>() {
        }.getType());
        // 用户列表的下表 => 相似度'
        List<Pair<Users, Long>> list = new ArrayList<>();
        // 依次计算当前用户和所有用户的相似度
        for (int i = 0; i < userList.size(); i++) {
            Users user = userList.get(i);
            String userTags = user.getTags();
            //无标签的 或当前用户为自己
            if (StringUtils.isBlank(userTags) || user.getId() == loginUser.getId()) {
                continue;
            }
            //将json字符串转换为字符串数组 用户的tag列表
            List<String> userTagList = gson.fromJson(userTags, new TypeToken<List<String>>() {
            }.getType());
            //计算分数 需要改变多少步变成对象数组
            long distance = AlgorithmUtils.minDistance(tagList, userTagList);
            list.add(new Pair<>(user, distance));
        }
        //按编辑距离由小到大排序 (a, b) -> (int)
        // (a.getValue() - b.getValue()) 优先队列的实现 将流的中value的数据进行两两比较,最小的排在最前面依次类推
        List<Pair<Users, Long>> topUserPairList = list.stream()
                .sorted((a, b) -> (int) (a.getValue() - b.getValue()))
                .limit(num)
                .collect(Collectors.toList());
        //有顺序的userID列表
        List<Long> userListVo = topUserPairList.stream().map(pari -> pari.getKey().getId()).collect(Collectors.toList());

        //根据id查询user完整信息
        QueryWrapper<Users> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.in("id", userListVo);
        Map<Long, List<Users>> userIdUserListMap = this.list(userQueryWrapper).stream()
                .map(this::getSafetyUser)
                .collect(Collectors.groupingBy(Users::getId));

        // 因为上面查询打乱了顺序，这里根据上面有序的userID列表赋值
        List<Users> finalUserList = new ArrayList<>();
        for (Long userId : userListVo) {
            finalUserList.add(userIdUserListMap.get(userId).get(0));
        }
        return finalUserList;
    }


}




