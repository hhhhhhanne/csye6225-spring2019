package edu.neu.xswl.csye6225.service;

import edu.neu.xswl.csye6225.dao.AttachmentDao;
import edu.neu.xswl.csye6225.pojo.Attachments;

import javax.annotation.Resource;
import java.util.List;

public class AttachmentServiceImpl implements  AttachmentService {

    @Resource
    AttachmentDao attachmentDao;

    @Override
    public List<Attachments> selectByNoteId(String noteId) {
        return attachmentDao.selectByNoteId(noteId);
    }

    @Override
    public void addAttachment(Attachments attachment) {
        attachmentDao.addAttachment(attachment);
    }

    @Override
    public void updateByAttachmentId(Attachments attachment) {
        attachmentDao.updateByAttachmentId(attachment);
    }

    @Override
    public void deleteByAttachmentId(String attachmentId) {
        attachmentDao.deleteByAttachmentId(attachmentId);
    }
}
