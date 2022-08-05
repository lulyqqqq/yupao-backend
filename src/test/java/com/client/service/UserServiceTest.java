package com.client.service;

import com.client.model.domain.Users;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * @ClassName: UserService
 * @author: mafangnian
 * @date: 2023/5/21 14:37
 */
@SpringBootTest
public class UserServiceTest {

    @Resource
    private UsersService usersService;
    @Test
    public void testSearchByUsersTags(){
        List<String> tagNameList = Arrays.asList("java", "python");
        List<Users> usersList = usersService.searchUsersByTags(tagNameList);
        Assert.assertNotNull(usersList);
    }

    @Test
    public void testJsonByUsersTags(){
        List<String> tagNameList = Arrays.asList("java");
        List<Users> usersList = usersService.searchUsersByTags(tagNameList);
        Assert.assertNotNull(usersList);
    }

    @Test
    public void testGetIdByUsersTags(){
        Users users = usersService.getById(1);
        System.out.println(users);
    }


}
