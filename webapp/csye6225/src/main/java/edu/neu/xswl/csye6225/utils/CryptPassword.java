package edu.neu.xswl.csye6225.utils;

import org.springframework.security.crypto.bcrypt.BCrypt;

public class CryptPassword {


    public BCrypt bct;

    public CryptPassword() {
        this.bct = new BCrypt();
    }

    public String hashPassword(String password, String salt) {

        String passwordHash = bct.hashpw(password, salt);

        return passwordHash;
    }

    public String generateSalt() {
        return bct.gensalt();
    }
}
