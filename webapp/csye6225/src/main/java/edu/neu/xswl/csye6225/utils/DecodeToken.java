package edu.neu.xswl.csye6225.utils;

import java.util.Base64;

public class DecodeToken {

    public String[] decodedInfo = {"", ""};
    public String decodedString;

    public DecodeToken(String token) throws IllegalArgumentException {
        //Use base64 decoder to decode token and get the username and password
        Base64.Decoder decoder = Base64.getDecoder();
        this.decodedString = new String(decoder.decode(token));
    }

    public boolean isValid() {
        if(!decodedString.contains(":")) return false;
        else {
            decodedInfo = decodedString.split(":");
            return true;
        }
    }

    public String getUsername() {
        return decodedInfo[0];
    }

    public String getPassword() {
        return decodedInfo[1];
    }

}
