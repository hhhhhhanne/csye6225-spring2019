package edu.neu.xswl.csye6225.utils;

import com.sun.source.tree.AssertTree;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class DecodeTokenTest {

    @Test
    public void isCorrectDecoded() {

        String username = "sadfb123@gmail.com";
        String password = "adfhef124";
        String token = "c2FkZmIxMjNAZ21haWwuY29tOmFkZmhlZjEyNA==";
        DecodeToken decodeToken = new DecodeToken(token);

        Assert.assertTrue(username.equals(decodeToken.getUsername()));
        Assert.assertTrue(password.equals(decodeToken.getPassword()));

    }

}
