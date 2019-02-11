package edu.neu.xswl.csye6225.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonAlias;
import edu.neu.xswl.csye6225.pojo.Notes;
import edu.neu.xswl.csye6225.pojo.Users;
import edu.neu.xswl.csye6225.service.NoteService;
import edu.neu.xswl.csye6225.service.UserService;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
public class NoteController {

    @Autowired
    UserService userService;

    @Autowired
    NoteService noteService;

    @RequestMapping(value = "/note", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> getAllNote(Principal principal) {

        JSONObject jsonObject = new JSONObject();

        Users user;

        //If user not authorized, send UNAUTHORIZED
        try {
            user = userService.getUserByUsername(principal.getName());
        } catch (Exception e) {
            jsonObject.put("message", "user does not exist");
            return new ResponseEntity<>(jsonObject, HttpStatus.UNAUTHORIZED);
        }

        List<JSONObject> jsonObjectList = new ArrayList<>();
        List<Notes> noteList = noteService.selectByUserId(user.getUserId());
        for(Notes note: noteList)
            jsonObjectList.add(getResponseEntity(new JSONObject(), note));

        return new ResponseEntity<>(jsonObjectList, HttpStatus.OK);
    }

    @RequestMapping(value = "/note/{id}", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> getNote(@PathVariable("id") String id, Principal principal) {

        JSONObject jsonObject = new JSONObject();

        //If user not authorized, send UNAUTHORIZED
        try {
            userService.getUserByUsername(principal.getName());
        } catch (Exception e) {
            jsonObject.put("message", "user does not exist");
            return new ResponseEntity<>(jsonObject, HttpStatus.UNAUTHORIZED);
        }

        Notes note;

        //If note not exist, send BAD_REQUEST
        try{
            note = noteService.selectByNoteId(id);
        }catch (Exception e){
            jsonObject.put("message", "task does not exist");
            return new ResponseEntity<>(jsonObject,HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(getResponseEntity(jsonObject, note), HttpStatus.OK);
    }


    @RequestMapping(value = "/note", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> createNote(@RequestBody String jsonNote, Principal principal, HttpServletResponse response) {
        //我不知道这句话干啥的，学姐写了，你们看看要不要
        response.setContentType(ContentType.APPLICATION_JSON.getMimeType());
        JSONObject jsonObject = new JSONObject();
        //学姐写的判断json长度，我也不知道我们这个多少会爆
//        JsonObject jsonObject = new JsonObject();
//        Gson gson = new Gson();
//        if (gson.fromJson(sTask, Task.class).getDescription().length() >= 4096) {
//            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
//            jsonObject.addProperty("message", "task: bad request : size too large");
//            return jsonObject.toString();
//        }
        Notes note = JSON.parseObject(jsonNote, Notes.class);
        String noteId = UUID.randomUUID().toString();
        note.setNoteId(noteId);
        String current = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
        note.setCreatedOn(current);
        note.setLastUpdatedOn(current);
        Users user;
        try {
            user = userService.getUserByUsername(principal.getName());
        } catch (Exception e) {
            jsonObject.put("message", "user does not exist");
            return new ResponseEntity<>(jsonObject, HttpStatus.UNAUTHORIZED);
        }
        note.setUserId(user.getUserId());
        noteService.addNote(note);
        return new ResponseEntity<>(getResponseEntity(jsonObject, note), HttpStatus.CREATED);
    }

    private JSONObject getResponseEntity(JSONObject jsonObject, Notes note) {
        jsonObject.put("id", note.getNoteId());
        jsonObject.put("content", note.getContent());
        jsonObject.put("title", note.getTitle());
        jsonObject.put("created_on", note.getCreatedOn());
        jsonObject.put("last_updated_on", note.getLastUpdatedOn());
        return jsonObject;
    }

    @RequestMapping(value = "/note/{id}", method = RequestMethod.PUT, produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> updateNote(@PathVariable("id") String id, Principal principal,@RequestBody String jsonNote, HttpServletResponse response) {
        JSONObject jsonObject = new JSONObject();

        Notes oldNote;
        try{
            oldNote = noteService.selectByNoteId(id);
        }catch (Exception e){
            jsonObject.put("message", "task does not exist");
//            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return new ResponseEntity<>(jsonObject,HttpStatus.BAD_REQUEST);
        }
        Users user;
        try{
            user = userService.getUserByUsername(principal.getName());
        }catch (Exception e){
            jsonObject.put("message", "user does not exist");
//            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return new ResponseEntity<>(jsonObject,HttpStatus.BAD_REQUEST);
        }
        if(!user.getUserId().equals(oldNote.getUserId())){
            jsonObject.put("message", "user does not match");
//            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return new ResponseEntity<>(jsonObject,HttpStatus.UNAUTHORIZED);
        }
        Notes newNote = JSON.parseObject(jsonNote, Notes.class);
        newNote.setNoteId(id);
        String current = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
        newNote.setLastUpdatedOn(current);
        noteService.updateByNoteId(newNote);
        Notes returnNote = noteService.selectByNoteId(id);
        return new ResponseEntity<>(getResponseEntity(jsonObject, returnNote), HttpStatus.NO_CONTENT);
    }

    @RequestMapping(value = "/note/{id}", method = RequestMethod.DELETE, produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> deleteNote(@PathVariable("id") String id, Principal principal) {

        JSONObject jsonObject = new JSONObject();

        //If user not authorized, send UNAUTHORIZED
        try {
            userService.getUserByUsername(principal.getName());
        } catch (Exception e) {
            jsonObject.put("message", "user does not exist");
            return new ResponseEntity<>(jsonObject, HttpStatus.UNAUTHORIZED);
        }

        //If note not exist, send BAD_REQUEST
        try{
            noteService.deleteByNoteId(id);
        }catch (Exception e){
            jsonObject.put("message", "task does not exist");
            return new ResponseEntity<>(jsonObject,HttpStatus.BAD_REQUEST);
        }

        //If deleted successfully, send NO_CONTENT
        return new ResponseEntity<>(jsonObject, HttpStatus.NO_CONTENT);
    }
}