package com.resto.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
public class SseController {
	
	// 儲存所有連線中的客戶端瀏覽器
    private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    // 前端來訂閱：GET /sse/order-status
    @GetMapping("/sse/order-status")
    public SseEmitter streamOrderStatus() { // 瀏覽器連上後，會一直等後端推送事件
    	
    	// Spring 的 SSE 工具，負責持續對瀏覽器送事件
        SseEmitter emitter = new SseEmitter(0L); // 0 = 永不逾時；若想 30 分鐘自動斷線可改 1_800_000L
        emitters.add(emitter);

        // 移除失效連線
        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError((ex) -> emitters.remove(emitter));

        // 連線建立後，瀏覽器會一直等待事件
        return emitter;
    }

    // 後端要推播時呼叫這支方法
    public void sendStatusUpdate(String message) {
    	for (SseEmitter emitter : emitters) {
            try {
                emitter.send(SseEmitter.event()
                                       .name("order-status-update")          // 事件名
                                       .data(message)             // 可JSON或字串
                                       .id("update-" + LocalDateTime.now()) 
                                       .reconnectTime(3000));        // 客戶端斷線後 3 秒再試
            } catch (IOException ex) {
                emitter.complete();
                emitters.remove(emitter);
            }
        }
    }

    

    
    
    
    
    
}
