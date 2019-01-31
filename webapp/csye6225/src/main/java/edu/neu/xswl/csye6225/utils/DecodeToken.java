package edu.neu.xswl.csye6225.utils;

import java.util.Base64;

public class DecodeToken {

    public String[] decodedInfo;

    public DecodeToken(String token) {
        //Use base64 decoder to decode token and get the username and password
        Base64.Decoder decoder = Base64.getDecoder();
        String decodedString = new String(decoder.decode(token));
        this.decodedInfo = decodedString.split(":");
    }

    public String getUsername() {
        return decodedInfo[0];
    }

    public String getPassword() {
        return decodedInfo[1];
    }

}
