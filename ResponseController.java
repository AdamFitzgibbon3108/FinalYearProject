package com.example.controller;

import com.example.model.Response;
import com.example.service.ResponseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ResponseController {

    private final ResponseService responseService;

    @Autowired
    public ResponseController(ResponseService responseService) {
        this.responseService = responseService;
    }

    @PostMapping("/responses/submit")
    public void submitResponses(@RequestBody List<Response> responses) {
        responses.forEach(response -> responseService.saveResponse(response));
    }
}

