package com.ai.rag_mini_project.controller;

import com.ai.rag_mini_project.service.RagService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/chat")
public class RagController {

    private final RagService ragService;

    public RagController(RagService ragService) {
        this.ragService = ragService;
    }


    @GetMapping
    public ResponseEntity<String> getResponse(@RequestParam String query,
                                              @RequestHeader String userId) {
        String result = ragService.getResponse(query,userId);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
