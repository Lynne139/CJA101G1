package com.util.email;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import jakarta.mail.MessagingException;

@RestController
@RequestMapping("/testemail")
public class TestEmailController { 

    @Autowired
    private ExampleEmailService exampleEmailService;
    
    @GetMapping("/send-example-email")
    public String testSendEmail() throws MessagingException, IOException {
    	
        try {
        	exampleEmailService.sendExampleEmail("");
            System.out.println("✅ 測試信已寄出！");
            return "✅ 測試信已寄出！";
        } catch (MessagingException e) {
            System.err.println("❌ 發送信件失敗：" + e.getMessage());
            return "❌ 發送信件失敗：" + e.getMessage();
        }
    }
}

// 寄信測試用網址:
// http://localhost:8081/CJA101G1/testemail/send-example-email
//或http://localhost:8080/testemail/send-example-email