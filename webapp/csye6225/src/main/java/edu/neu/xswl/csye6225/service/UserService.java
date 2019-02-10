package edu.neu.xswl.csye6225.service;

import edu.neu.xswl.csye6225.pojo.Users;

public interface UserService {

    public Users getUserById(int userId);

    public Users getUserByUsername(String userName);

    public void addUser(String userName, String password);

    public Boolean checkUsername(String userName);

    public Boolean checkUser(String username,String password);
}
