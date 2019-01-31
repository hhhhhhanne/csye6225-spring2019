package edu.neu.xswl.csye6225.controller;

import edu.neu.xswl.csye6225.pojo.Users;
import edu.neu.xswl.csye6225.service.UserService;
import edu.neu.xswl.csye6225.utils.CryptPassword;
import edu.neu.xswl.csye6225.utils.DecodeToken;
import edu.neu.xswl.csye6225.utils.PasswordUtil;
import edu.neu.xswl.csye6225.utils.PasswordUtilImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Date;
import java.util.Map;

@RequestMapping("/")
@RestController
public class UserController {
    @Autowired
    UserService userService;

    @RequestMapping(value = "", method = RequestMethod.GET)
    public Date getDate(@RequestHeader(value="token") String token){

        //decode the token
        DecodeToken dt = new DecodeToken(token);
        String username = dt.getUsername();
        String password = dt.getPassword();

        Users user = userService.getUserByUsername(username);
        String salt = user.getSalt();

        CryptPassword cp = new CryptPassword();
        String passwordHash = cp.hashPassword(password, salt);


        //authenticate the password
        if(user.getPassword().equals(passwordHash)) {
            return new Date();
        }
        else {
            return new Date();
        }

    }

    @RequestMapping(value = "user/regiser", method = RequestMethod.POST)
    public void register(@RequestParam(value="username") String username, @RequestParam(value="password") String password) {

        //check if password is strong enough
        PasswordUtilImpl passwordAuth = new PasswordUtilImpl();
        if(!passwordAuth.isStrongPassword(password)) {

            return;
        }

        //generate salt and crypt the password and store password hash into database
        CryptPassword cp = new CryptPassword();
        String salt = cp.generateSalt();
        String passwordHash = cp.hashPassword(password, salt);

        userService.addUser(username, passwordHash, salt);

    }

}
