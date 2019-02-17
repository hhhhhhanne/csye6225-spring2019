package edu.neu.xswl.csye6225.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class Attachments implements Serializable {

    private String attachmentId;

    private String url;

    private String noteId;
}
