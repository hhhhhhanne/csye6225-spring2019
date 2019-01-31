package edu.neu.xswl.csye6225.dao;

import edu.neu.xswl.csye6225.pojo.Users;

public interface UserDao {

    Users selectByUsername(String username);

    Users selectByUserId(Integer userId);

    void addUser(String username, String password, String salt);

//    Users selectByUsernameAndPassword(String username,String password);
}
