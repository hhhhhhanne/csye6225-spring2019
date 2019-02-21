package edu.neu.xswl.csye6225.service;

import edu.neu.xswl.csye6225.dao.AttachmentDao;
import edu.neu.xswl.csye6225.pojo.Attachments;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service("AttachmentService")
public class AttachmentServiceImpl implements  AttachmentService {

    @Resource
    AttachmentDao attachmentDao;

    @Override
    public List<Attachments> selectByNoteId(String noteId) {
        return attachmentDao.selectByNoteId(noteId);
    }

    @Override
    public Attachments selectByAttachmentId(String attachmentId) {
        return attachmentDao.selectByAttachmentId(attachmentId);
    }

    @Override
    public void addAttachment(Attachments attachment) {
        attachmentDao.addAttachment(attachment);
    }

    @Override
    public void updateByAttachment(Attachments attachment) {
        attachmentDao.updateByAttachment(attachment);
    }

    @Override
    public void deleteByAttachmentId(String attachmentId) {
        attachmentDao.deleteByAttachmentId(attachmentId);
    }
}
