package edu.neu.xswl.csye6225.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailValidationUtilImpl implements  EmailValidationUtil {
    @Override
    public boolean isEmail(String str) {
        if (str == null)
            return false;
        String regEx1 = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
        Pattern p;
        Matcher m;
        p = Pattern.compile(regEx1);
        m = p.matcher(str);
        return m.matches();
    }
}
