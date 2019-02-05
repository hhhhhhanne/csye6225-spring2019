package edu.neu.xswl.csye6225.service;

import edu.neu.xswl.csye6225.dao.UserDao;
import edu.neu.xswl.csye6225.pojo.Users;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service("userService")
public class UserServiceImpl implements UserService {

    @Resource
    private UserDao userDao;

    @Override
    public Users getUserById(int userId) {
        return userDao.selectByUserId(userId);
    }

    @Override
    public Users getUserByUsername(String userName) {
        return userDao.selectByUsername(userName);
    }

    @Override
    public void addUser(String userName, String password) {
        if (!checkUsername(userName)) {
            userDao.addUser(userName, password);
        }
    }

    @Override
    public Boolean checkUsername(String userName) {
        return getUserByUsername(userName) != null;
    }

    @Override
    public Boolean checkUser(String username, String password) {
        Users user = getUserByUsername(username);
        if (user == null) return null;
        String salt = user.getSalt();
        //todo

//        if (password + salt == user.getPassword()) {
//            return true;
//        } else return false;
        return false;
    }


}
