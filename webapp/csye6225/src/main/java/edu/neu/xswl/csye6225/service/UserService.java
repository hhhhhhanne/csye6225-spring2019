package edu.neu.xswl.csye6225.service;

import edu.neu.xswl.csye6225.pojo.Users;

public interface UserService {

    Users getUserById(int userId);

    Users getUserByUsername(String userName);

    void addUser(String userName, String password);

    Boolean checkUsername(String userName);

    Boolean checkUser(String username, String password);
}
