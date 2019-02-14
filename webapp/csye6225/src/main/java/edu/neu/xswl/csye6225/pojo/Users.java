package edu.neu.xswl.csye6225.pojo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Users implements Serializable {

    private String userId;

    private String password;

    private String username;

    private String salt;
}
