package com.parunev.linkededge.controller;

import com.parunev.linkededge.openai.OpenAi;
import com.parunev.linkededge.openai.model.OpenAiMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequiredArgsConstructor
@RequestMapping("/edge-api/v1/interview")
public class InterviewController {

    private final OpenAi openAi;

    @GetMapping
    public ResponseEntity<String> test(){
        List<OpenAiMessage> messages = new ArrayList<>();

        return new ResponseEntity<>(openAi.ask(messages), HttpStatus.OK);
    }
}
