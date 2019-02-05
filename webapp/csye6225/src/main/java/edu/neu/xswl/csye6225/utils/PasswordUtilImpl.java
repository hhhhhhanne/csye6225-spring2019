package edu.neu.xswl.csye6225.utils;

import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class PasswordUtilImpl implements PasswordUtil {

    private static final int REQUIRED_PASSWORD_LENGTH = 8;

    @Override
    public boolean isStrongPassword(String password) {
        return checkPasswordLength(password) && checkAPasswordContainsNumber(password) && checkPasswordContainsChar(password);
    }


    /*
    Check if password length meet requirements
     */
    private boolean checkPasswordLength(String password) {
        return password.length() >= REQUIRED_PASSWORD_LENGTH;
    }


    /*
    Check if password contains number
     */
    private boolean checkAPasswordContainsNumber(String password) {
        Pattern pattern = Pattern.compile(".*\\d+.*");
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    /*
    Check if password contains non-number character
     */
    private boolean checkPasswordContainsChar(String password) {
        Pattern pattern = Pattern.compile(".*\\D+.*");
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }
}
