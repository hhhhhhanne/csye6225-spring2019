package edu.neu.xswl.csye6225.service;

import edu.neu.xswl.csye6225.dao.NoteDao;
import edu.neu.xswl.csye6225.pojo.Notes;
import edu.neu.xswl.csye6225.pojo.Users;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service("NoteService")
public class NoteServiceImpl implements NoteService {


    @Resource
    private NoteDao noteDao;


    @Override
    public List<Notes> selectByUser(Users user) {
        return noteDao.selectByUser(user);
    }

    @Override
    public void addNote(Notes note) {
        noteDao.addNote(note);
    }

    @Override
    public Notes selectByNoteId(String noteId) {
        return noteDao.selectByNoteId(noteId);
    }

    @Override
    public Notes updateByNoteId(String noteId, Notes note) {
        return noteDao.updateByNoteId(noteId, note);
    }

    @Override
    public void deleteByNoteId(String noteId) {
        noteDao.deleteByNoteId(noteId);
    }
}
