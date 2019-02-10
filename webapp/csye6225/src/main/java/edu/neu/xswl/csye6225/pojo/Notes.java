package edu.neu.xswl.csye6225.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notes {

    private String note_id;

    private String content;

    private String created_on;

    private String last_updated_on;

    private Integer userId;
}
