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


@RunWith(SpringRunner.class)
@SpringBootTest
public class NoteTest {

    @Resource
    NoteDao noteDao;

    @Test
    public void testGetCreateNote() {
        Notes n = new Notes();
        n.setNote_id("id");
        n.setTitle("title");
        n.setContent("content");
        String current = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format( new java.util.Date());
        n.setLast_updated_on(current);
        n.setCreated_on(current);
        noteDao.addNote(n);
    }
}
