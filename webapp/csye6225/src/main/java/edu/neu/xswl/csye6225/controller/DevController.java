package edu.neu.xswl.csye6225.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.timgroup.statsd.StatsDClient;
import edu.neu.xswl.csye6225.pojo.Attachments;
import edu.neu.xswl.csye6225.pojo.Notes;
import edu.neu.xswl.csye6225.pojo.Users;
import edu.neu.xswl.csye6225.service.AttachmentService;
import edu.neu.xswl.csye6225.service.NoteService;
import edu.neu.xswl.csye6225.service.UserService;
import edu.neu.xswl.csye6225.utils.S3uploadUtil;
import org.apache.http.entity.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jackson.JsonObjectDeserializer;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.Response;
import java.io.*;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@Profile("dev")
@RequestMapping(value = "/note")
public class DevController {

    @Autowired
    UserService userService;

    @Autowired
    NoteService noteService;

    @Autowired
    AttachmentService attachmentService;

    private static final Logger logger = LoggerFactory.getLogger(DevController.class);

    private static final StatsDClient statsd = IndexController.statsd;

    @GetMapping(produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> getAllNote(Principal principal) {
        statsd.incrementCounter("/note.http.get");
        Users user = userService.getUserByUsername(principal.getName());
        logger.info("[GET USER NOTES START] Username is: " + user.getUsername());

        List<JSONObject> jsonObjectList = new ArrayList<>();
        List<Notes> noteList = noteService.selectByUserId(user.getUserId());
        for (Notes note : noteList)
            jsonObjectList.add(getNoteResponseEntity(new JSONObject(), note));

        logger.info("[GET USER NOTES SUCCESS] Username is: " + user.getUsername());
        return new ResponseEntity<>(jsonObjectList, HttpStatus.OK);
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> getNote(@PathVariable("id") String id, Principal principal) {
        statsd.incrementCounter("/note/{noteid}.http.get");

        JSONObject jsonObject = new JSONObject();

        Users user = userService.getUserByUsername(principal.getName());
        logger.info("[GET NOTE START] Username is: " + user.getUsername());

        Notes note = noteService.selectByNoteId(id);

        //If note not exist, send BAD_REQUEST
        try {
            note.getNoteId();
            logger.info("note id is: " + note.getNoteId());
        } catch (Exception e) {
            jsonObject.put("message", "note does not exist");
            logger.error("note not found, note id is: " + note.getNoteId());
            return new ResponseEntity<>(jsonObject, HttpStatus.NOT_FOUND);
        }

        if (!user.getUserId().equals(note.getUserId())) {
            jsonObject.put("message", "user does not match");
            logger.error("user does not match, note id is: " + note.getNoteId());
            return new ResponseEntity<>(jsonObject, HttpStatus.UNAUTHORIZED);
        }

        logger.info("[GET NOTE SUCCESS] Username is: " + user.getUsername());
        return new ResponseEntity<>(getNoteResponseEntity(jsonObject, note), HttpStatus.OK);
    }


    @PostMapping(produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> createNote(@RequestBody(required = false) String jsonNote, Principal principal) {
        statsd.incrementCounter("/note.http.post");

        logger.info("[POST NOTE START] Username is: " + principal.getName());

        JSONObject jsonObject = new JSONObject();

        Notes note;
        try {
            note = JSON.parseObject(jsonNote, Notes.class);
            String noteId = UUID.randomUUID().toString();
            note.setNoteId(noteId);
            logger.info("note creating.... note id is: " + noteId);
        } catch (Exception e) {
            jsonObject.put("message", "Bad Request");
            logger.error("Bad Request, json format error");
            return new ResponseEntity<>(jsonObject, HttpStatus.BAD_REQUEST);
        }

        if (note.getTitle() == null || note.getTitle().equals("")) {
            jsonObject.put("message", "Title is null");
            logger.error("Bad Request, title can not be null");
            return new ResponseEntity<>(jsonObject, HttpStatus.BAD_REQUEST);
        }

        String current = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
        note.setCreatedOn(current);
        note.setLastUpdatedOn(current);

        note.setUserId(userService.getUserByUsername(principal.getName()).getUserId());
        noteService.addNote(note);
        logger.info("[POST NOTE SUCCESS] Username is: " + principal.getName());
        return new ResponseEntity<>(getNoteResponseEntity(jsonObject, note), HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}", produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> updateNote(@PathVariable("id") String id, Principal principal,
                                        @RequestBody(required = false) String jsonNote) {
        statsd.incrementCounter("/note/{id}.http.put");

        logger.info("[PUT NOTE START] Username is: " + principal.getName());

        JSONObject jsonObject = new JSONObject();

        Notes oldNote = noteService.selectByNoteId(id);
        try {
            oldNote.getNoteId();
            logger.info("note id is: " + oldNote.getNoteId());
        } catch (Exception e) {
            jsonObject.put("message", "Note Not Exist");
            logger.error("note not exist, note id is: " + oldNote.getNoteId());
            return new ResponseEntity<>(jsonObject, HttpStatus.BAD_REQUEST);
        }

        Users user = userService.getUserByUsername(principal.getName());

        if (!user.getUserId().equals(oldNote.getUserId())) {
            jsonObject.put("message", "user does not match");
            logger.error("user not match");
            return new ResponseEntity<>(jsonObject, HttpStatus.UNAUTHORIZED);
        }

        //If input JSON is invalid
        Notes newNote;
        try {
            newNote = JSON.parseObject(jsonNote, Notes.class);
            newNote.setNoteId(id);
            logger.info("new note is creating.....");
        } catch (Exception e) {
            jsonObject.put("message", "Bad Request");
            logger.error("Bad Request, Json format not correct");
            return new ResponseEntity<>(jsonObject, HttpStatus.BAD_REQUEST);
        }

        if (newNote.getTitle() == null || newNote.getTitle().equals("")) {
            jsonObject.put("message", "Title is null");
            logger.error("Bad Request, title can not null");
            return new ResponseEntity<>(jsonObject, HttpStatus.BAD_REQUEST);
        }

        String current = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date());
        newNote.setLastUpdatedOn(current);
        noteService.updateByNoteId(newNote);
        Notes returnNote = noteService.selectByNoteId(id);

        logger.info("[PUT NOTE SUCCESS] Username is: " + principal.getName());
        return new ResponseEntity<>(getNoteResponseEntity(jsonObject, returnNote), HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(value = "/{id}", produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> deleteNote(@PathVariable("id") String id, Principal principal) {
        statsd.incrementCounter("/note/{id}.http.delete");

        JSONObject jsonObject = new JSONObject();

        Users user = userService.getUserByUsername(principal.getName());
        logger.info("[DELETE NOTE START]");

        Notes note;
        List<Attachments> attachmentList;
        //If note not exist, send BAD_REQUEST
        try {
            note = noteService.selectByNoteId(id);
            noteService.selectByNoteId(id).getNoteId();
            attachmentList = attachmentService.selectByNoteId(id);
            logger.info("note is found.... note id is: " + id);
        } catch (Exception e) {
            jsonObject.put("message", "note does not exist");
            logger.error("note not exist, note id is: " + id);
            return new ResponseEntity<>(jsonObject, HttpStatus.BAD_REQUEST);
        }

        if (!user.getUserId().equals(note.getUserId())) {
            jsonObject.put("message", "user does not match");
            logger.error("user does not match");
            return new ResponseEntity<>(jsonObject, HttpStatus.UNAUTHORIZED);
        }

        //If exist, delete
        logger.info("deleting note.... note id is: " + id);
        noteService.deleteByNoteId(id);

        for (Attachments attachments : attachmentList) {
            String path = attachments.getUrl();
            String keyName = S3uploadUtil.getKeyname(path);
            S3uploadUtil.deleteObject(keyName, S3uploadUtil.bucketName);
        }

        //If deleted successfully, send NO_CONTENT
        logger.info("[DELETE NOTE SUCCESS]");
        return new ResponseEntity<>(jsonObject, HttpStatus.NO_CONTENT);
    }

    @GetMapping(value = "/{id}/attachments", produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> getAllAttachments(@PathVariable("id") String id, Principal principal) {
        statsd.incrementCounter("/note/{id}/attachments.http.get");
        logger.info("[GET ATTACHMENTS START]");

        List<JSONObject> jsonObjectList = new ArrayList<>();

        Users user = userService.getUserByUsername(principal.getName());

        Notes note = noteService.selectByNoteId(id);
        if (note == null) {
            logger.error("bad request, can not find a note");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (user.getUserId().equals(note.getUserId())) {
            List<Attachments> attachmentsList = attachmentService.selectByNoteId(id);
            for (Attachments attachments : attachmentsList)
                jsonObjectList.add(getAttachmentResponseEntity(new JSONObject(), attachments));
        } else {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("message", "user does not match");
            logger.error("user does not match");
            return new ResponseEntity<>(jsonObject, HttpStatus.UNAUTHORIZED);
        }

        logger.info("[GET ATTACHMENTS SUCCESS]");
        return new ResponseEntity<>(jsonObjectList, HttpStatus.OK);
    }

    @PostMapping(value = "/{id}/attachments", produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> postAttachment(@PathVariable("id") String id,
                                            @RequestParam(value = "file", required = false) MultipartFile file,
                                            Principal principal) {
        statsd.incrementCounter("/note/{id}/attachments.http.post");
        logger.info("[POST ATTACHMENTS START]");
        Users user = userService.getUserByUsername(principal.getName());

        Notes note = noteService.selectByNoteId(id);

        if (file == null || note == null) {
            logger.error("Bad Request, can not find a file or note");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Attachments attachment = new Attachments();
        String fileName = file.getOriginalFilename();
        String folder = "/src/main/resources/static";
        String relativePath = System.getProperty("user.dir");
        String filePath = relativePath + folder;

        if (!attachmentService.isURLUnique(fileName)) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("message", "url already exist, please rename it or choose a different file");
            logger.error("Bad Request, url already exist, please rename it or choose a different file");
            return new ResponseEntity<>(jsonObject, HttpStatus.BAD_REQUEST);
        }

        String keyName = fileName;
        File fileToUpload = null;
        try {
            fileToUpload = convertMultiToFile(file, filePath, fileName);
        } catch (IOException e) {
            logger.error("Unknown error, convert multipart file not success");
            e.printStackTrace();
        }

        attachment.setAttachmentId(UUID.randomUUID().toString());
        attachment.setNoteId(id);

        if (user.getUserId().equals(note.getUserId())) {
            try {
                AmazonS3 s3client = new AmazonS3Client(DefaultAWSCredentialsProviderChain.getInstance());
                s3client.putObject(new PutObjectRequest(S3uploadUtil.bucketName, keyName, fileToUpload));
                fileToUpload.delete();
            } catch (Exception e) {
                logger.error("error, check s3 status and file to upload path");
                e.printStackTrace();
            }
            String s3url = S3uploadUtil.getpublicurl(keyName, S3uploadUtil.bucketName);
            attachment.setUrl(s3url);
            attachmentService.addAttachment(attachment);
        } else {
            logger.error("Unauthorized, user can not attach file to this note");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        logger.info("[POST ATTACHMENTS SUCCESS]");
        return new ResponseEntity<>(getAttachmentResponseEntity(new JSONObject(), attachment), HttpStatus.OK);
    }

    @PutMapping(value = "/{id}/attachments/{idAttachments}", produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> updateAttachment(@PathVariable("id") String id,
                                              @PathVariable("idAttachments") String idAttachments,
                                              @RequestParam(value = "file", required = false) MultipartFile file,
                                              Principal principal) {
        statsd.incrementCounter("/note/{id}/attachments/{attachmentid}.http.put");
        logger.info("[PUT ATTACHMENTS START]");
        Users user = userService.getUserByUsername(principal.getName());

        Notes note = noteService.selectByNoteId(id);
        if (file == null || note == null) {
            logger.error("Bad Request, can not find a file or note");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Attachments attachment = attachmentService.selectByAttachmentId(idAttachments);
        if (attachment == null) {
            logger.error("error, can not find a attachment according to the attachment id");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        String oldPath = attachment.getUrl();
        String fileName = file.getOriginalFilename();
        String folder = "/src/main/resources/static";
        String relativePath = System.getProperty("user.dir");
        String filePath = relativePath + folder;

        if (!attachmentService.isURLUnique(fileName)) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("message", "url already exist, please rename it or choose a different file");
            logger.error("url already exist, please rename it or choose a different file");
            return new ResponseEntity<>(jsonObject, HttpStatus.BAD_REQUEST);
        }

        if (user.getUserId().equals(note.getUserId()) && attachment.getNoteId().equals(id)) {
            String keyName = S3uploadUtil.getKeyname(oldPath);
            S3uploadUtil.deleteObject(keyName, S3uploadUtil.bucketName);
            try {
                delete(oldPath);
            } catch (Exception e) {
                logger.error("Delete file from disk error");
                e.printStackTrace();
            }
        } else {
            logger.error("Unauthorized, user can not attach file to this note");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        String keyName = fileName;
        File fileToUpload = null;


        //convert multiFile to file
        try {
            fileToUpload = convertMultiToFile(file, filePath, fileName);
        } catch (IOException e) {
            logger.error("Unknown error, convert multipart file not success");
            e.printStackTrace();
        }

        attachment.setUrl(filePath);

        try {
            AmazonS3 s3client = new AmazonS3Client(DefaultAWSCredentialsProviderChain.getInstance());
            s3client.putObject(new PutObjectRequest(S3uploadUtil.bucketName, keyName, fileToUpload));
            fileToUpload.delete();
        } catch (Exception e) {
            logger.error("error, check s3 status and file to upload path");
            e.printStackTrace();
        }
        String s3url = S3uploadUtil.getpublicurl(keyName, S3uploadUtil.bucketName);
        attachment.setUrl(s3url);
        attachmentService.updateByAttachment(attachment);

        logger.info("[PUT ATTACHMENTS SUCCESS]");
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(value = "/{id}/attachments/{idAttachments}", produces = "application/json")
    @ResponseBody
    public ResponseEntity<?> deleteAttachment(@PathVariable("id") String id,
                                              @PathVariable("idAttachments") String idAttachments,
                                              Principal principal) {
        statsd.incrementCounter("/note/{id}/attachments/{attachmentid}.http.delete");
        logger.info("[DELETE ATTACHMENTS START]");
        Users user = userService.getUserByUsername(principal.getName());

        Notes note = noteService.selectByNoteId(id);
        if (note == null) {
            logger.error("error, note can not be null");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Attachments attachments = attachmentService.selectByAttachmentId(idAttachments);
        if (attachments == null) {
            logger.error("error, attachment can not be null");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        String oldPath = attachments.getUrl();
        if (user.getUserId().equals(note.getUserId()) && attachments.getNoteId().equals(note.getNoteId())) {
            attachmentService.deleteByAttachmentId(idAttachments);
            String keyName = S3uploadUtil.getKeyname(oldPath);
            S3uploadUtil.deleteObject(keyName, S3uploadUtil.bucketName);
        } else {
            logger.error("error, file delete can not success");
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }

        logger.info("[DELETE ATTACHMENTS SUCCESS]");
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

    private static void deleteFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            System.out.println("[log] Delete File failed:" + file.getName() + "not exist！");
        } else {
            if (file.isFile()) file.delete();
        }


    }

    //transfer file
//    private File transferFile(MultipartFile file, String path) throws IOException {
//        if (!file.isEmpty()) {
//            String filename = file.getOriginalFilename();
//            File convFile = new File(path+File.separator+filename);
//        } else {
//            throw new IOException("empty multipartfile");
//        }
//    }
    public static File convertMultiToFile(MultipartFile file, String filePath, String fileName) throws IOException {
//        File dir = new File(filePath);
//        if (!dir.exists()) {
//            dir.mkdirs();
//        }
        File convFile = new File(filePath + fileName);
        InputStream ins = file.getInputStream();// new File(file.getOriginalFilename());
        OutputStream os = new FileOutputStream(convFile);
        int bytesRead = 0;
        byte[] buffer = new byte[8192];
        while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
            os.write(buffer, 0, bytesRead);
        }
        os.close();
        ins.close();
        return convFile;
    }

}
