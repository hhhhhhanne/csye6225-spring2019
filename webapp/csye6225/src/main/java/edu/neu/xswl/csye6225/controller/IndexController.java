package edu.neu.xswl.csye6225.controller;

import com.alibaba.fastjson.JSON;
import edu.neu.xswl.csye6225.pojo.Users;
import edu.neu.xswl.csye6225.service.UserService;
import edu.neu.xswl.csye6225.utils.EmailValidationUtil;
import edu.neu.xswl.csye6225.utils.PasswordUtilImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;

@RestController
public class IndexController {

    @Autowired
    EmailValidationUtil emailValidationUtil;

    @Autowired
    UserService userService;

    @Autowired
    PasswordUtilImpl passwordUtil;

    @RequestMapping(value = "/", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> welcome() {

        HashMap<String, String> response = new HashMap<>();

        if (SecurityContextHolder.getContext().getAuthentication() != null
                && SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken) {
            response.put("message", "you are not logged in!!!");
        } else {
            response.put("message", "you are logged in. current time is " + new Date().toString());
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @RequestMapping(value = "/user/register", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> registerPost(@RequestBody String jsonUser) {
        Users user = JSON.parseObject(jsonUser, Users.class);
        HashMap<String, String> response = new HashMap<>();
        String username = user.getUsername();
        String password = user.getPassword();
        System.out.println(username + password);
        if (null == username || username.equals("") || null == password || password.equals("")) {
            response.put("Warning", "Please enter username or password!");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if (!emailValidationUtil.isEmail(username)) {
            response.put("Warning", "Please use a valid email address as your username");
            return new ResponseEntity<>(response, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if (!passwordUtil.isStrongPassword(password)) {
            response.put("Warning", "Your password is too weak!");
            return new ResponseEntity<>(response, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());
        Users user_db = userService.getUserByUsername(username);

        if (user_db == null) {
            userService.addUser(username, passwordHash);
            response.put("Message", "You have registered successfully!");
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            response.put("Warning", "The username already exists!");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }

    }
}
