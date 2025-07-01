package com.chatbot.model;

import java.util.List;

//QA資料結構
public class QAItem {
    private List<String> keywords;
    private String answer;
    private int weight;

    public QAItem(List<String> keywords, String answer, int weight) {
        this.keywords = keywords;
        this.answer = answer;
        this.weight = weight;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public String getAnswer() {
        return answer;
    }

    public int getWeight() {
        return weight;
    }
}
