package edu.neu.xswl.csye6225.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notes {

    private String noteId;

    private String title;

    private String content;

    private String createdOn;

    private String lastUpdatedOn;

    private String userId;
}
