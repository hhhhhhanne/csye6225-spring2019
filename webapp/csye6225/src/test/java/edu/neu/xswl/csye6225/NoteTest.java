package edu.neu.xswl.csye6225;

import edu.neu.xswl.csye6225.dao.NoteDao;
import edu.neu.xswl.csye6225.pojo.Notes;
import edu.neu.xswl.csye6225.service.NoteServiceImpl;
import edu.neu.xswl.csye6225.service.UserServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest
public class NoteTest {

    @Resource
    NoteDao noteDao;

    @Test
    public void testGetCreateNote() {
        Notes n = new Notes();
        n.setNoteId("idtest");
        n.setTitle("title");
        n.setContent("content");
        String current = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
        n.setCreatedOn(current);
        n.setLastUpdatedOn(current);
        noteDao.addNote(n);
    }

    @Test
    public void testGetNotes() {
        List<Notes> notes = noteDao.selectByUserId("9643f85e-67f6-4f91-965a-0d42f8ece005");
        System.out.println(notes);
    }

    @Test
    public void testSelectByNoteId() {
        System.out.println(noteDao.selectByNoteId("idtest"));
    }

    @Test
    public void testUpdateByNoteId() {
        Notes n = new Notes();
        n.setNoteId("idtest");
//        n.setTitle("123title");
        n.setContent("123content");
        String current = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
//        n.setCreatedOn(current);
        n.setLastUpdatedOn(current);
        noteDao.updateByNoteId(n);
    }

    @Test
    public void testDeleteByNoteId() {
        noteDao.deleteByNoteId("idtest");
    }

}
