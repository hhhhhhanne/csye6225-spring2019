package edu.neu.xswl.csye6225;

import edu.neu.xswl.csye6225.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DaoTest {

    @Autowired
    UserService userService;

    @Test
    public void testGetByUserId(){
        System.out.println(userService.getUserById(1));
    }

    @Test
    public void testGetByUserName(){
        System.out.println(userService.checkUsername("sb"));
    }

    @Test
    public void testAddUser(){
        userService.addUser("test22","test22","test22");
    }

    @Test
    public void testCheckUsername(){
        System.out.println("1"+userService.checkUsername("sb"));
        System.out.println("2"+userService.checkUsername("lzsb"));
    }

}
