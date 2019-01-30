package edu.neu.xswl.csye6225.controller;

import edu.neu.xswl.csye6225.pojo.Users;
import edu.neu.xswl.csye6225.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/users")
@RestController
public class UserController {
    @Autowired
    UserService userService;

    @RequestMapping(value = "/test/{id}", method = RequestMethod.GET)
    public Users getUser(@PathVariable("id") String id){
        Integer userId = new Integer(id);
        Users user = userService.getUserById(userId);
        return user;
    }
}
