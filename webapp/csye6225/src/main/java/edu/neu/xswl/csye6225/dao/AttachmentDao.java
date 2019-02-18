package edu.neu.xswl.csye6225.dao;

import edu.neu.xswl.csye6225.pojo.Attachments;

import java.util.List;

public interface AttachmentDao {

    List<Attachments> selectByNoteId(String noteId);

    Attachments selectByAttachmentId(String attachmentId);

    void addAttachment(Attachments attachment);

    void updateByAttachment(Attachments attachment);

    void deleteByAttachmentId(String attachmentId);
}
