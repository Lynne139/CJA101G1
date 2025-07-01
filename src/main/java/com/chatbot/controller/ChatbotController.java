package com.chatbot.controller;

import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/chatbot")
public class ChatbotController {

    private final List<QAItem> qaList;
    private final LevenshteinDistance levenshtein = new LevenshteinDistance();

    public ChatbotController() {
        qaList = new ArrayList<>();
        qaList.add(new QAItem(Arrays.asList("早餐", "早上吃飯", "早餐時間"), "我們的早餐時間是早上6:30到10:00，位於一樓餐廳。", 10));
        qaList.add(new QAItem(Arrays.asList("停車", "車位", "停車場"), "飯店提供免費停車場，位於地下室。", 8));
        qaList.add(new QAItem(Arrays.asList("入住時間", "check-in", "入住"), "入住時間為下午3點以後，退房時間中午12點前。", 9));
        qaList.add(new QAItem(Arrays.asList("wifi", "無線網路", "網路"), "飯店全館提供免費無線網路。", 7));
        qaList.add(new QAItem(Arrays.asList("房價", "價格", "費用"), "請問您想查詢哪個房型的價格呢？", 6));
        // 你可以在這繼續加更多問題和答案
    }

    @PostMapping
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        String userMessage = Optional.ofNullable(request.getMessage()).orElse("").toLowerCase();

        if (userMessage.isEmpty()) {
            return ResponseEntity.ok(new ChatResponse("您好，請輸入您的問題，我們會盡快回覆您。"));
        }

        List<String> segmentedWords = segmentWords(userMessage);
        String reply = generateReply(segmentedWords);

        return ResponseEntity.ok(new ChatResponse(reply));
    }

    // 使用 Ansj 斷詞
    private List<String> segmentWords(String text) {
        Result result = ToAnalysis.parse(text);
        List<String> words = new ArrayList<>();
        for (Term term : result.getTerms()) {
            String word = term.getName().toLowerCase().trim();
            if (!word.isEmpty()) {
                words.add(word);
            }
        }
        return words;
    }

    // 逐詞模糊比對
    private String generateReply(List<String> userWords) {
        int maxScore = -1;
        String bestAnswer = null;

        for (QAItem item : qaList) {
            int matchCount = 0;
            for (String keyword : item.getKeywords()) {
                for (String userWord : userWords) {
                	int distance = levenshtein.apply(userWord, keyword.toLowerCase());
                	// e.g.如果關鍵字長度為 6，則容忍距離為 6 / 3 = 2 → 可接受差 2 個字。最少也會容忍 1 個字錯誤。
                    int tolerance = Math.max(keyword.length() / 3, 1);
                    if (distance <= tolerance) {
                        matchCount++;
                        break; // 該 keyword 找到一個 userWord 匹配即跳下一 keyword
                    }
                }
            }
            if (matchCount > 0) {
                int score = matchCount * item.getWeight();
                if (score > maxScore) {
                    maxScore = score;
                    bestAnswer = item.getAnswer();
                }
            }
        }
        if (bestAnswer == null) {
            return "抱歉，我不明白您的問題，請重新提問，或以電話聯絡我們。";
        }
        return bestAnswer;
    }

    // QA資料結構
    public static class QAItem {
        private List<String> keywords;
        private String answer;
        private int weight;

        public QAItem(List<String> keywords, String answer, int weight) {
            this.keywords = keywords;
            this.answer = answer;
            this.weight = weight;
        }

        public List<String> getKeywords() { return keywords; }
        public String getAnswer() { return answer; }
        public int getWeight() { return weight; }
    }

    // 請求DTO
    public static class ChatRequest {
        private String message;
        public void setMessage(String message) { this.message = message; }
        public String getMessage() { return message; }
    }

    // 回應DTO
    public static class ChatResponse {
        private String reply;
        public ChatResponse(String reply) { this.reply = reply; }
        public void setReply(String reply) { this.reply = reply; }
        public String getReply() { return reply; }
    }
}
