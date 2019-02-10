package edu.neu.xswl.csye6225.utils;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PasswordUtilTest {

    PasswordUtil passwordUtil = new PasswordUtilImpl();

    @Test
    public void isStrongPasswordTest() {
        List<String> strongPasswordList = new ArrayList<>();
        List<String> weakPasswordList = new ArrayList<>();
        strongPasswordList.add("1234abcd");
        strongPasswordList.add("123';',;',");
        strongPasswordList.add("./,.,/.,123");

        weakPasswordList.add("123abcd");
        weakPasswordList.add("12345678");
        weakPasswordList.add("abcdefgh");
        weakPasswordList.add(".,/.,/.,?.,asd.,");

        for (String password : strongPasswordList)
            Assert.assertTrue(passwordUtil.isStrongPassword(password));

        for (String password : weakPasswordList){
            Assert.assertFalse(passwordUtil.isStrongPassword(password));
        }
    }
}
