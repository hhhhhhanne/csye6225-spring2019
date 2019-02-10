package edu.neu.xswl.csye6225.controller;

import edu.neu.xswl.csye6225.service.NoteService;
import edu.neu.xswl.csye6225.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NoteController {

    @Autowired
    UserService userService;

    @Autowired
    NoteService noteService;


}
