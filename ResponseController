package com.example.controller;

import com.example.model.Response;
import com.example.service.ResponseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/responses")
public class ResponseController {

    private final ResponseService responseService;

    @Autowired
    public ResponseController(ResponseService responseService) {
        this.responseService = responseService;
    }

    @PostMapping("/submit")
    public ResponseEntity<String> submitResponses(@RequestBody List<Response> responses) {
        responseService.saveAllResponses(responses);
        return ResponseEntity.ok("Responses submitted successfully!");
    }
}

