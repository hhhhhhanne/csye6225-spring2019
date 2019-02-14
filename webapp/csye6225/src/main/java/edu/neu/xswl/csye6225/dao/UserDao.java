package edu.neu.xswl.csye6225.dao;

import edu.neu.xswl.csye6225.pojo.Users;

public interface UserDao {

    Users selectByUsername(String username);

    Users selectByUserId(String userId);

    void addUser(String uuid,String username, String password);

//    Users selectByUsernameAndPassword(String username,String password);
}
