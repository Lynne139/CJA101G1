package com.chatbot.controller;

import com.chatbot.model.ChatRequest;
import com.chatbot.model.ChatResponse;
import com.chatbot.service.ChatbotService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {

    private final ChatbotService chatbotService;

    @Autowired
    public ChatbotController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @PostMapping
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        String userMessage = Optional.ofNullable(request.getMessage()).orElse("").toLowerCase();

        if (userMessage.isEmpty()) {
            return ResponseEntity.ok(new ChatResponse("您好，有甚麼能為您服務的嗎？"));
        }

        String reply = chatbotService.generateReply(userMessage);
        return ResponseEntity.ok(new ChatResponse(reply));
    }
}