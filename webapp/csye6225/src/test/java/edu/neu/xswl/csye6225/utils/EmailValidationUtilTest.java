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
public class EmailValidationUtilTest {

    EmailValidationUtil emailValidationUtil = new EmailValidationUtilImpl();

    @Test
    public void isValidEmail() {
        List<String> validEmailList = new ArrayList<>();
        List<String> invalidEmailList = new ArrayList<>();

        validEmailList.add("wda@sad.sad");
        validEmailList.add("li.ang2@husky.neu.edu");

        invalidEmailList.add("sada123231");
        invalidEmailList.add("sadsad.asdsad.sad");

        for (String validEmail : validEmailList)
            Assert.assertTrue(emailValidationUtil.isEmail(validEmail));


        for (String invalidEmail : invalidEmailList)
            Assert.assertFalse(emailValidationUtil.isEmail(invalidEmail));
    }
}
