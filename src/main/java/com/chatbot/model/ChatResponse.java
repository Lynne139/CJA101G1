package com.chatbot.model;

// 回應DTO
public class ChatResponse {
    private String reply;

    public ChatResponse() {}

    public ChatResponse(String reply) {
        this.reply = reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }
    
    public String getReply() {
        return reply;
    }
}
