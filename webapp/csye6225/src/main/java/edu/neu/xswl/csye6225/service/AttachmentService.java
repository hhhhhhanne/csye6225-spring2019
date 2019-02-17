package edu.neu.xswl.csye6225.service;

import edu.neu.xswl.csye6225.pojo.Attachments;

import java.util.List;

public interface AttachmentService {

    List<Attachments> selectByNoteId(String noteId);

    void addAttachment(Attachments attachment);

    void updateByAttachmentId(Attachments attachment);

    void deleteByAttachmentId(String attachmentId);
}
