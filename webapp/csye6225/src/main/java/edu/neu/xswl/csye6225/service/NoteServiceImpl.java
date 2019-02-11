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
    public List<Notes> selectByUserId(String userId) {
        return noteDao.selectByUserId(userId);
    }

    @Override
    public Notes addNote(Notes note) {
        noteDao.addNote(note);
        return note;
    }

    @Override
    public Notes selectByNoteId(String noteId) {
        return noteDao.selectByNoteId(noteId);
    }

    @Override
    public Notes updateByNoteId(Notes note) {
        noteDao.updateByNoteId(note);
        return note;
    }

    @Override
    public void deleteByNoteId(String noteId) {
        noteDao.deleteByNoteId(noteId);
    }
}
