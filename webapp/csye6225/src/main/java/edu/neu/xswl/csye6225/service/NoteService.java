package edu.neu.xswl.csye6225.service;

import edu.neu.xswl.csye6225.pojo.Notes;
import edu.neu.xswl.csye6225.pojo.Users;

import java.util.List;

public interface NoteService {

    List<Notes> selectByUser(Users user);

    Notes addNote(Notes note);

    Notes selectByNoteId(String noteId);

    Notes updateByNoteId(String noteId, Notes note);

    void deleteByNoteId(String noteId);
}
