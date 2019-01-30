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

}
