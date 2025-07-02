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
        qaList.add(new QAItem(Arrays.asList("早餐", "早上吃飯", "早餐時間"), "我們的早餐時間是早上6點30分至10點，位於一樓餐廳。", 9));
        qaList.add(new QAItem(Arrays.asList("停車", "車位", "停車場"), "本渡假村設有免費停車場，住客可於入住期間自由進出，無需預約。", 8));
        qaList.add(new QAItem(Arrays.asList("入住", "報到", "退房"), "入住時間為下午3點，退房時間為上午11點。如需提前入住或延後退房，\n請提前與櫃台確認當日房況，將酌收費用並視可用房間安排。", 7));
        qaList.add(new QAItem(Arrays.asList("網路", "無線網路", "wifi"), "全館房型及公共區域皆提供免費高速 Wi-Fi，入住時可向櫃台索取密碼。", 8));
        qaList.add(new QAItem(Arrays.asList("洗衣", "烘衣", "洗脫烘"), "我們的自助洗衣間位於B1，全時段開放，提供住客免費使用。", 9));
        qaList.add(new QAItem(Arrays.asList("健身房", "運動", "健身設施"),"我們的健身房位於三樓，開放時間為早上6點至晚上10點，提供住客免費使用。", 8));
        qaList.add(new QAItem(Arrays.asList("游泳池", "泳池", "游泳"),"戶外游泳池於夏季開放，開放時間為早上8點至晚上8點，請著泳裝入場。", 8));
        qaList.add(new QAItem(Arrays.asList("行李", "寄放", "提早到"),"您可於入住前或退房後將行李寄放於櫃台，我們將妥善為您保管。", 7));
        qaList.add(new QAItem(Arrays.asList("寵物", "導盲犬", "貓狗"),"目前本渡假村僅限導盲犬等服務犬入住，一般寵物暫不開放，\n造成不便敬請見諒。", 7));
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
