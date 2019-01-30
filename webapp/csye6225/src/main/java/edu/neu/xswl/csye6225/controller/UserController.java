package edu.neu.xswl.csye6225.controller;

import edu.neu.xswl.csye6225.pojo.Users;
import edu.neu.xswl.csye6225.service.UserService;
import edu.neu.xswl.csye6225.utils.CryptPassword;
import edu.neu.xswl.csye6225.utils.DecodeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Date;

@RequestMapping("/")
@RestController
public class UserController {
    @Autowired
    UserService userService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public Date getDate(@RequestHeader(value="token") String token){

        DecodeToken dt = new DecodeToken(token);
        String username = dt.getUsername();
        String password = dt.getPassword();

        Users user = userService.getUserByUsername(username);
        String salt = user.getSalt();

        CryptPassword cp = new CryptPassword();
        String passwordHash = cp.hashPassword(username, salt);

        if(user.getPassword().equals(passwordHash)) {
            return new Date();
        }
        else {

        }

    }

    @RequestMapping(value = "user/regiser", method = RequestMethod.POST)
}
