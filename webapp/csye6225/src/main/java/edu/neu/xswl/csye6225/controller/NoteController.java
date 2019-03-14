package edu.neu.xswl.csye6225.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.annotation.JsonAlias;
import edu.neu.xswl.csye6225.pojo.Attachments;
import edu.neu.xswl.csye6225.pojo.Notes;
import edu.neu.xswl.csye6225.pojo.Users;
import edu.neu.xswl.csye6225.service.AttachmentService;
import edu.neu.xswl.csye6225.service.NoteService;
import edu.neu.xswl.csye6225.service.UserService;
import org.apache.http.entity.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JsonObjectDeserializer;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.Response;
import java.io.File;
import java.io.IOException;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@Profile("default")
@RequestMapping(value = "/note")
public class NoteController {

    @Autowired
    UserService userService;

    @Autowired
    NoteService noteService;

    @Autowired
    AttachmentService attachmentService;

    @GetMapping(produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> getAllNote(Principal principal) {

        Users user = userService.getUserByUsername(principal.getName());

        List<JSONObject> jsonObjectList = new ArrayList<>();
        List<Notes> noteList = noteService.selectByUserId(user.getUserId());
        for (Notes note : noteList)
            jsonObjectList.add(getNoteResponseEntity(new JSONObject(), note));

        return new ResponseEntity<>(jsonObjectList, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> getNote(@PathVariable("id") String id, Principal principal) {

        JSONObject jsonObject = new JSONObject();

        Users user = userService.getUserByUsername(principal.getName());

        Notes note = noteService.selectByNoteId(id);

        //If note not exist, send BAD_REQUEST
        try {
            note.getNoteId();
        } catch (Exception e) {
            jsonObject.put("message", "note does not exist");
            return new ResponseEntity<>(jsonObject, HttpStatus.NOT_FOUND);
        }

        if (!user.getUserId().equals(note.getUserId())) {
            jsonObject.put("message", "user does not match");
            return new ResponseEntity<>(jsonObject, HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(getNoteResponseEntity(jsonObject, note), HttpStatus.OK);
    }


    @PostMapping(produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> createNote(@RequestBody(required = false) String jsonNote, Principal principal) {

        JSONObject jsonObject = new JSONObject();

        Notes note;
        try {
            note = JSON.parseObject(jsonNote, Notes.class);
            String noteId = UUID.randomUUID().toString();
            note.setNoteId(noteId);
        } catch (Exception e) {
            jsonObject.put("message", "Bad Request");
            return new ResponseEntity<>(jsonObject, HttpStatus.BAD_REQUEST);
        }

        if(note.getTitle()==null || note.getTitle().equals("")){
            jsonObject.put("message", "Title is null");
            return new ResponseEntity<>(jsonObject, HttpStatus.BAD_REQUEST);
        }

        String current = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
        note.setCreatedOn(current);
        note.setLastUpdatedOn(current);

        note.setUserId(userService.getUserByUsername(principal.getName()).getUserId());
        noteService.addNote(note);
        return new ResponseEntity<>(getNoteResponseEntity(jsonObject, note), HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}", produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> updateNote(@PathVariable("id") String id, Principal principal,
                                        @RequestBody(required = false) String jsonNote) {

        JSONObject jsonObject = new JSONObject();

        Notes oldNote = noteService.selectByNoteId(id);
        try {
            oldNote.getNoteId();
        } catch (Exception e) {
            jsonObject.put("message", "Note Not Exist");
            return new ResponseEntity<>(jsonObject, HttpStatus.BAD_REQUEST);
        }

        Users user = userService.getUserByUsername(principal.getName());

        if (!user.getUserId().equals(oldNote.getUserId())) {
            jsonObject.put("message", "user does not match");
            return new ResponseEntity<>(jsonObject, HttpStatus.UNAUTHORIZED);
        }

        //If input JSON is invalid
        Notes newNote;
        try {
            newNote = JSON.parseObject(jsonNote, Notes.class);
            newNote.setNoteId(id);
        } catch (Exception e) {
            jsonObject.put("message", "Bad Request");
            return new ResponseEntity<>(jsonObject, HttpStatus.BAD_REQUEST);
        }

        if(newNote.getTitle()==null || newNote.getTitle().equals("")){
            jsonObject.put("message", "Title is null");
            return new ResponseEntity<>(jsonObject, HttpStatus.BAD_REQUEST);
        }

        String current = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
        newNote.setLastUpdatedOn(current);
        noteService.updateByNoteId(newNote);
        Notes returnNote = noteService.selectByNoteId(id);
        return new ResponseEntity<>(getNoteResponseEntity(jsonObject, returnNote), HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(value = "/{id}", produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> deleteNote(@PathVariable("id") String id, Principal principal) {

        JSONObject jsonObject = new JSONObject();

        Users user = userService.getUserByUsername(principal.getName());

        Notes note;
        List<Attachments> attachmentsList;
        //If note not exist, send BAD_REQUEST
        try {
            note = noteService.selectByNoteId(id);
            noteService.selectByNoteId(id).getNoteId();
            attachmentsList = attachmentService.selectByNoteId(id);
        } catch (Exception e) {
            jsonObject.put("message", "note does not exist");
            return new ResponseEntity<>(jsonObject, HttpStatus.BAD_REQUEST);
        }

        if (!user.getUserId().equals(note.getUserId())) {
            jsonObject.put("message", "user does not match");
            return new ResponseEntity<>(jsonObject, HttpStatus.UNAUTHORIZED);
        }

        //If exist, delete
        noteService.deleteByNoteId(id);

        for(Attachments attachments: attachmentsList)
            delete(attachments.getUrl());

        //If deleted successfully, send NO_CONTENT
        return new ResponseEntity<>(jsonObject, HttpStatus.NO_CONTENT);
    }

    @GetMapping(value = "/{id}/attachments", produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> getAllAttachments(@PathVariable("id") String id, Principal principal) {

        List<JSONObject> jsonObjectList = new ArrayList<>();

        Users user = userService.getUserByUsername(principal.getName());

        Notes note = noteService.selectByNoteId(id);
        if (note == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (user.getUserId().equals(note.getUserId())) {
            List<Attachments> attachmentsList = attachmentService.selectByNoteId(id);
            for (Attachments attachments : attachmentsList)
                jsonObjectList.add(getAttachmentResponseEntity(new JSONObject(), attachments));
        } else {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("message", "user does not match");
            return new ResponseEntity<>(jsonObject, HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(jsonObjectList, HttpStatus.OK);
    }

    @PostMapping(value = "/{id}/attachments", produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> postAttachment(@PathVariable("id") String id, @RequestParam(value = "file", required = false) MultipartFile file, Principal principal) {
        Users user = userService.getUserByUsername(principal.getName());

        Notes note = noteService.selectByNoteId(id);

        if (file == null || note == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Attachments attachment = new Attachments();
        String fileName = file.getOriginalFilename();
        String folder = "/src/main/resources/static";
        String relativePath = System.getProperty("user.dir");
        String filePath = null;

        if(!attachmentService.isURLUnique(fileName)){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("message", "url already exist, please rename it or choose a different file");
            return new ResponseEntity<>(jsonObject, HttpStatus.BAD_REQUEST);
        }

        try {
            filePath = saveFile(file, relativePath + folder);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            attachment.setAttachmentId(UUID.randomUUID().toString());
            attachment.setUrl(filePath);
            attachment.setNoteId(id);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (user.getUserId().equals(note.getUserId())) {
            attachmentService.addAttachment(attachment);
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(getAttachmentResponseEntity(new JSONObject(), attachment), HttpStatus.OK);
    }

    @PutMapping(value = "/{id}/attachments/{idAttachments}", produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> updateAttachment(@PathVariable("id") String id, @PathVariable("idAttachments") String idAttachments, @RequestParam(value = "file", required = false) MultipartFile file, Principal principal) {
        Users user = userService.getUserByUsername(principal.getName());

        Notes note = noteService.selectByNoteId(id);
        if (file == null || note == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Attachments attachment = attachmentService.selectByAttachmentId(idAttachments);
        if (attachment == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        String oldPath = attachment.getUrl();
        String fileName = file.getOriginalFilename();
        String folder = "/src/main/resources/static";
        String relativePath = System.getProperty("user.dir");
        String filePath = null;

        for(Attachments attachments : attachmentService.selectAllAttachments())
            System.out.println(attachments.getUrl());

        System.out.println(fileName);

        if(!attachmentService.isURLUnique(fileName)){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("message", "url already exist, please rename it or choose a different file");
            return new ResponseEntity<>(jsonObject, HttpStatus.BAD_REQUEST);
        }

        try {
            filePath = saveFile(file, relativePath + folder);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            attachment.setUrl(filePath);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (user.getUserId().equals(note.getUserId()) && attachment.getNoteId().equals(id)) {
            attachmentService.updateByAttachment(attachment);
            try {
                delete(oldPath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(value = "/{id}/attachments/{idAttachments}", produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> deleteAttachment(@PathVariable("id") String id, @PathVariable("idAttachments") String idAttachments, Principal principal) {
        Users user = userService.getUserByUsername(principal.getName());

        Notes note = noteService.selectByNoteId(id);
        if (note == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Attachments attachments = attachmentService.selectByAttachmentId(idAttachments);
        if (attachments == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        String oldPath = attachments.getUrl();
        if (user.getUserId().equals(note.getUserId()) && attachments.getNoteId().equals(note.getNoteId())) {
            attachmentService.deleteByAttachmentId(idAttachments);
            try {
                delete(oldPath);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private JSONObject getNoteResponseEntity(JSONObject jsonObject, Notes note) {
        jsonObject.put("id", note.getNoteId());
        jsonObject.put("content", note.getContent());
        jsonObject.put("title", note.getTitle());
        jsonObject.put("created_on", note.getCreatedOn());
        jsonObject.put("last_updated_on", note.getLastUpdatedOn());
        return jsonObject;
    }

    private JSONObject getAttachmentResponseEntity(JSONObject jsonObject, Attachments attachment) {
        jsonObject.put("id", attachment.getAttachmentId());
        jsonObject.put("url", attachment.getUrl());
        return jsonObject;
    }

    //save file
    private String saveFile(MultipartFile file, String path) throws IOException {

        if (!file.isEmpty()) {
            String filename = file.getOriginalFilename();
            File filepath = new File(path, filename);

            //if path not exist, create the folder
            if (!filepath.getParentFile().exists()) {
                filepath.getParentFile().mkdirs();
            }
            String finalPath = path + File.separator + filename;

            //transfer the files into the target folder
            file.transferTo(new File(finalPath));
            return finalPath;
        } else {
            return "file not exist";
        }
    }

    //delete file on file system
    private void delete(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("[log] Delete File failed:" + file.getName() + "not exist！");
        } else {
            if (file.isFile()) file.delete();
        }


    }

}
