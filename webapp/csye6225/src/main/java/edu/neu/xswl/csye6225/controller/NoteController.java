package edu.neu.xswl.csye6225.controller;

import edu.neu.xswl.csye6225.service.NoteService;
import edu.neu.xswl.csye6225.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/note")
public class NoteController {

    @Autowired
    UserService userService;

    @Autowired
    NoteService noteService;

    @RequestMapping(method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Map<String, String>> getAllNotes(){
        Map<String, String> messageMap = new HashMap<>();
        messageMap.put("goushi", "dagoushi");
        return new ResponseEntity<>(messageMap, HttpStatus.OK);
    }


}
