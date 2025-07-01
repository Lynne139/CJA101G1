package com.chatbot.service;

import com.chatbot.model.QAItem;
import org.ansj.domain.Result;
import org.ansj.domain.Term;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ChatbotService {

    private final List<QAItem> qaList = new ArrayList<>();
    private final LevenshteinDistance levenshtein = new LevenshteinDistance();

    public ChatbotService() {
        qaList.add(new QAItem(Arrays.asList("早餐", "早上吃飯", "早餐時間"), "我們的早餐時間是早上6點半至10點，位於一樓餐廳。", 10));
        qaList.add(new QAItem(Arrays.asList("停車", "車位", "停車場"), "我們提供免費地下停車場，並有全時攝影保護您的愛車。", 7));
        qaList.add(new QAItem(Arrays.asList("入住", "check-in", "報到"), "本飯店的入住時間為下午3點以後，退房時間為中午12點前。", 9));
        qaList.add(new QAItem(Arrays.asList("wifi", "無線網路", "網路"), "本飯店全館提供免費無線網路，歡迎貴賓多加利用。", 7));
        qaList.add(new QAItem(Arrays.asList("洗衣", "烘衣", "洗脫烘"), "我們的自助洗衣間位於B1，全時段開放，提供住客免費使用。", 8));
        qaList.add(new QAItem(Arrays.asList("健身房", "運動", "健身設施"),"我們的健身房位於三樓，開放時間為早上6點至晚上10點，提供住客免費使用。", 8));
        qaList.add(new QAItem(Arrays.asList("游泳池", "泳池", "游泳"),"戶外游泳池於夏季開放，開放時間為早上8點至晚上8點，請著泳裝入場。", 8));
        qaList.add(new QAItem(Arrays.asList("行李", "寄放", "提早到", "退房後行李"),"您可於入住前或退房後將行李寄放於櫃台，我們將妥善為您保管。", 7));
        qaList.add(new QAItem(Arrays.asList("寵物", "毛小孩", "貓狗"),"目前尚未開放攜帶寵物入住，造成不便敬請見諒。", 6));
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
    
    // 以逐詞模糊比對QA表，決定回復內容
    public String generateReply(String userMessage) {
        List<String> userWords = segmentWords(userMessage);

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
            return "抱歉，我不明白您的問題，請重新提問，或電話聯絡我們。";
        }
        return bestAnswer;
    }
}
