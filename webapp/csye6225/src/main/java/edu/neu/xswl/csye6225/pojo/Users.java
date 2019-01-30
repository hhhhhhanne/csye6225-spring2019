package edu.neu.xswl.csye6225.pojo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Users implements Serializable {

    private Integer userId;

    private String password;

    private String username;

    private String salt;
}
