package edu.neu.xswl.csye6225.controller;

import edu.neu.xswl.csye6225.pojo.Users;
import edu.neu.xswl.csye6225.service.UserService;
import edu.neu.xswl.csye6225.utils.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RequestMapping("/")
@RestController
public class UserController {
    @Autowired
    UserService userService;

    /**
     * User Login
     * @param token token is a String combined by base64(username:password)
     * @return
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<?> getDate(@RequestHeader(value="Authorization", defaultValue = "null") String token){
        HttpHeaders headers = new HttpHeaders();
        //decode the token
        System.out.println(token);
        token = token.substring(6);
        HashMap<String, String> responseMessage = new HashMap<>();
        if(token == null || token.length() == 0 || token.equals("null")) {
            responseMessage.put("Warning", "You are not logged in!");
            return new ResponseEntity<>(responseMessage, headers, HttpStatus.UNAUTHORIZED);
        }
        DecodeToken dt;
        try {
            dt = new DecodeToken(token);
        } catch(Exception e) {
            responseMessage.put("Warning", "You username or password are not correct!");
            return new ResponseEntity<>(responseMessage, headers, HttpStatus.UNAUTHORIZED);
        }

        if(!dt.isValid()) {
            responseMessage.put("Warning", "The token is not valid!");
            return new ResponseEntity<>(responseMessage, headers, HttpStatus.UNAUTHORIZED);
        }

        String username = dt.getUsername();
        String password = dt.getPassword();

        if(username.length() == 0 || password.length() == 0) {
            responseMessage.put("Warning", "You username or password are not correct!");
            return new ResponseEntity<>(responseMessage, headers, HttpStatus.UNAUTHORIZED);
        }

        Users user = userService.getUserByUsername(username);
        String salt = user.getSalt();

        CryptPassword cp = new CryptPassword();
        String passwordHash = cp.hashPassword(password, salt);

        HashMap<String, Date> response = new HashMap<>();
        response.put("Date", new Date());
        //authenticate the password
        if(user.getPassword().equals(passwordHash)) {
            return new ResponseEntity<>(response, headers, HttpStatus.CREATED);
        }
        else {
            responseMessage.put("Warning", "You username or password are not correct!");
            return new ResponseEntity<>(responseMessage, headers, HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Register
     * @return
     */
    @RequestMapping(value = "user/register", method = RequestMethod.POST)
    public ResponseEntity<?> register(@RequestHeader(value="Authorization", defaultValue = "null") String token) {


        token = token.substring(6);
        DecodeToken dt = new DecodeToken(token);

        HttpHeaders headers = new HttpHeaders();
        HashMap<String, String> response = new HashMap<>();

        if(!dt.isValid()) {
            response.put("Warning", "Please enter username or password correctly!");
            return new ResponseEntity<>(response, headers, HttpStatus.BAD_REQUEST);
        }

        String username = dt.getUsername();
        String password = dt.getPassword();




        if(username.equals("null") || password.equals("null")) {
            response.put("Warning", "Please enter username or password!");
            return new ResponseEntity<>(response, headers, HttpStatus.BAD_REQUEST);
        }

        //check if password is strong enough
        PasswordUtil passwordAuth = new PasswordUtilImpl();
        EmailValidationUtil emailValidationUtil = new EmailValidationUtilImpl();


        if(null == username || username.equals("") || null == password || password.equals("")) {
            response.put("Warning", "Please enter username or password!");
            return new ResponseEntity<>(response, headers, HttpStatus.BAD_REQUEST);
        }
        if(userService.checkUsername(username)) {
            response.put("Warning", "The username already exists!");
            return new ResponseEntity<>(response, headers, HttpStatus.CONFLICT);
        }
        if(!passwordAuth.isStrongPassword(password)) {
            response.put("Warning", "Your password is too weak!");
            return new ResponseEntity<>(response, headers, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        if(!emailValidationUtil.isEmail(username)) {
            response.put("Warning", "Please use a valid email address as your username");
            return new ResponseEntity<>(response, headers, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        //generate salt and crypt the password and store password hash into database
        CryptPassword cp = new CryptPassword();
        String salt = cp.generateSalt();
        String passwordHash = cp.hashPassword(password, salt);
        System.out.println(username + " " + password);
        userService.addUser(username, passwordHash, salt);
        response.put("Message", "You have registered successfully!");
        return new ResponseEntity<>(response, headers, HttpStatus.OK);
    }

}
