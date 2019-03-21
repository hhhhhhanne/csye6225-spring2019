package edu.neu.xswl.csye6225.controller;

import com.alibaba.fastjson.JSON;
import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;
import edu.neu.xswl.csye6225.pojo.Users;
import edu.neu.xswl.csye6225.service.UserService;
import edu.neu.xswl.csye6225.utils.EmailValidationUtil;
import edu.neu.xswl.csye6225.utils.PasswordUtilImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

@RestController
public class IndexController {

    @Autowired
    EmailValidationUtil emailValidationUtil;

    @Autowired
    UserService userService;

    @Autowired
    PasswordUtilImpl passwordUtil;

    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    public static final StatsDClient statsd = new NonBlockingStatsDClient("my.prefix", "localhost", 8125);

    @RequestMapping(value = "/", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> welcome() {
        logger.info("/welcome.http.get");
        statsd.incrementCounter("/welcome");

        HashMap<String, String> response = new HashMap<>();

        if (SecurityContextHolder.getContext().getAuthentication() != null
                && SecurityContextHolder.getContext().getAuthentication() instanceof AnonymousAuthenticationToken) {
            logger.error("not logged in");
            response.put("message", "you are not logged in!!!");
        } else {
            logger.info("logged in successfully");
            response.put("message", "you are logged in. current time is " + new Date().toString());
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @RequestMapping(value = "/user/register", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> registerPost(@RequestBody String jsonUser) {
        logger.info("user register");
        statsd.incrementCounter("/user/register.http.post");
        Users user = JSON.parseObject(jsonUser, Users.class);
        HashMap<String, String> response = new HashMap<>();
        String username = user.getUsername();
        String password = user.getPassword();
        String uuid = UUID.randomUUID().toString();
//        System.out.println(username + password);
        if (null == username || username.equals("") || null == password || password.equals("")) {
            logger.error("username or password empty");
            response.put("Warning", "Please enter username or password!");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
        if (!emailValidationUtil.isEmail(username)) {
            logger.error("username isn't valid");
            response.put("Warning", "Please use a valid email address as your username");
            return new ResponseEntity<>(response, HttpStatus.UNPROCESSABLE_ENTITY);
        }
        if (!passwordUtil.isStrongPassword(password)) {
            logger.error("password isn't valid");
            response.put("Warning", "Your password is too weak!");
            return new ResponseEntity<>(response, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());
        Users user_db = userService.getUserByUsername(username);
        // System.out.println(user_db);
        if (user_db == null) {  // Check is username already exist
            logger.info("user register successfully");
            userService.addUser(uuid, username, passwordHash);
            response.put("Message", "You have registered successfully!");
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } else {
            logger.error("username duplicate");
            response.put("Warning", "The username already exists!");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }

    }
}
