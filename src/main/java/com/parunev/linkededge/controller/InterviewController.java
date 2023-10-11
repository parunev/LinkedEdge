package com.parunev.linkededge.controller;

import com.parunev.linkededge.model.payload.interview.*;
import com.parunev.linkededge.service.InterviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/edge-api/v1/interview")
public class InterviewController {

    private final InterviewService interviewService;

    @GetMapping("/generate")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_USER_EXTRA')")
    public ResponseEntity<List<QuestionResponse>> generateRandomInterviewQuestions(@RequestBody QuestionRequest request){
       return new ResponseEntity<>(interviewService.generateRandomInterviewQuestions(request), HttpStatus.OK);
    }

    @GetMapping("/answer-me")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_USER_EXTRA')")
    public ResponseEntity<AnswerResponse> answerUserQuestion(@RequestBody AnswerRequest request){
        return new ResponseEntity<>(interviewService.answerUserQuestion(request), HttpStatus.OK);
    }

    @GetMapping("/prepare-me")
    @PreAuthorize("hasAnyRole('ROLE_USER', 'ROLE_USER_EXTRA')")
    public ResponseEntity<JobResponse> prepareMeForAJob(@RequestBody JobRequest request){
        return new ResponseEntity<>(interviewService.prepareMeForAJob(request), HttpStatus.OK);
    }
}
