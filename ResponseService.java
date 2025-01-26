package com.example.service;

import com.example.model.Response;
import com.example.repository.ResponseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ResponseService {

    private final ResponseRepository responseRepository;

    @Autowired
    public ResponseService(ResponseRepository responseRepository) {
        this.responseRepository = responseRepository;
    }

    public void saveAllResponses(List<Response> responses) {
        responseRepository.saveAll(responses);
    }
}
