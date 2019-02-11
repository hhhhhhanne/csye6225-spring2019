package edu.neu.xswl.csye6225.dao;

import edu.neu.xswl.csye6225.pojo.Notes;
import edu.neu.xswl.csye6225.pojo.Users;

import java.util.List;

public interface NoteDao {

    /**
     * Select By User
     *
     * @param user the user to be retrieved
     * @return note list created by the user
     */
    List<Notes> selectByUser(Users user);

    /**
     * Add New Note
     *
     * @param note note to be added
     * @return note added
     */
    void addNote(Notes note);

    /**
     * Select By User Id
     *
     * @param noteId to be selected
     * @return note selected
     */
    Notes selectByNoteId(String noteId);

    /**
     * Update By Note Id
     *
     * @param noteId to be deleted
     * @param note   to replaced old note
     * @return note updated
     */
    Notes updateByNoteId(String noteId, Notes note);

    /**
     * Delete By User Id
     *
     * @param noteId to be deleted
     * @return Nothing
     */
    void deleteByNoteId(String noteId);

}
