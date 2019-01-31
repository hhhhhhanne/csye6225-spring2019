package edu.neu.xswl.csye6225.controller;

import edu.neu.xswl.csye6225.pojo.Users;
import edu.neu.xswl.csye6225.service.UserService;
import edu.neu.xswl.csye6225.utils.CryptPassword;
import edu.neu.xswl.csye6225.utils.DecodeToken;
import edu.neu.xswl.csye6225.utils.PasswordUtil;
import edu.neu.xswl.csye6225.utils.PasswordUtilImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<?> getDate(@RequestHeader(value="token") String token){

        //decode the token
        DecodeToken dt = new DecodeToken(token);
        String username = dt.getUsername();
        String password = dt.getPassword();
        HttpHeaders headers = new HttpHeaders();

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
            return new ResponseEntity<>(new Date(), headers, HttpStatus.UNAUTHORIZED);
        }
    }

    /**
     * Register
     * @param username username
     * @param password must be a strong password
     * @return
     */
    @RequestMapping(value = "user/register", method = RequestMethod.POST)
    public ResponseEntity<?> register(@RequestParam(value="username") String username, @RequestParam(value="password") String password) {
        //check if password is strong enough
        PasswordUtilImpl passwordAuth = new PasswordUtilImpl();
        HttpHeaders headers = new HttpHeaders();
        if(userService.checkUsername(username)) {
            return new ResponseEntity<>("", headers, HttpStatus.CONFLICT);
        }
        if(!passwordAuth.isStrongPassword(password)) {
            return new ResponseEntity<>("", headers, HttpStatus.UNPROCESSABLE_ENTITY);
        }

        //generate salt and crypt the password and store password hash into database
        CryptPassword cp = new CryptPassword();
        String salt = cp.generateSalt();
        String passwordHash = cp.hashPassword(password, salt);
        System.out.println(username + " " + password);
        userService.addUser(username, passwordHash, salt);

        return new ResponseEntity<>("", headers, HttpStatus.OK);
    }

}
