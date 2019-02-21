package edu.neu.xswl.csye6225;

import edu.neu.xswl.csye6225.dao.AttachmentDao;
import edu.neu.xswl.csye6225.dao.NoteDao;
import edu.neu.xswl.csye6225.pojo.Attachments;
import edu.neu.xswl.csye6225.pojo.Notes;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.UUID;


@RunWith(SpringRunner.class)
@SpringBootTest
public class AttachmentTest {

    @Resource
    AttachmentDao attachmentDao;

    @Test
    public void testAddAttachment() {
        Attachments a = new Attachments();
        String attachmentId = UUID.randomUUID().toString();
        a.setAttachmentId(attachmentId);
        a.setUrl("avgle.com");
        a.setNoteId("2bae6d7a-35ae-48cc-baeb-bfe6d8df8ef3");
        attachmentDao.addAttachment(a);
    }

    @Test
    public void testSelectByNoteId() {
        List<Attachments> attachments = attachmentDao.selectByNoteId("2bae6d7a-35ae-48cc-baeb-bfe6d8df8ef3");
        System.out.println(attachments);
    }

    @Test
    public void testUpdateByAttachment() {
        Attachments n = new Attachments();
        n.setAttachmentId("fa7fa5b6-4904-4b19-a26b-526dba21c9df");
        n.setUrl("t66y.com");
        n.setNoteId("2bae6d7a-35ae-48cc-baeb-bfe6d8df8ef3");
        attachmentDao.updateByAttachment(n);
    }

    @Test
    public void testDeleteByAttachmentId() {
        attachmentDao.deleteByAttachmentId("c6ae0029-d6bf-43be-a281-11e280613f53");
    }

}
