package edu.neu.xswl.csye6225.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notes implements Serializable {

    private String noteId;

    private String title;

    private String content;

    private String createdOn;

    private String lastUpdatedOn;

    private String userId;
}
