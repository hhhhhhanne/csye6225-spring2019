package edu.neu.xswl.csye6225.service;

import edu.neu.xswl.csye6225.pojo.Notes;
import edu.neu.xswl.csye6225.pojo.Users;

import java.util.List;

public interface NoteService {

    List<Notes> selectByUserId(String userId);

    void addNote(Notes note);

    Notes selectByNoteId(String noteId);

    Notes updateByNoteId(Notes note);

    void deleteByNoteId(String noteId);
}
