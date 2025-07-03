package com.util.email;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class ResetPasswordEmailService {

    @Autowired
    private JavaMailSender mailSender;
    // Example改成自己的e.g.RoomBooking，參數可自訂(e.g.加入RoomOrder roomOrder)
    public void sendResetPasswordEmail(String toMemberEmail) throws MessagingException, IOException {

    	toMemberEmail = "cja101g1@gmail.com"; // 收件者電郵，這行僅供測試，實際應是傳入的參數
        String subject = "【嶼蔻渡假村】密碼變更通知信"; 
        
        MimeMessage message = mailSender.createMimeMessage();
        
        // true = multipart message
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");   
        helper.setFrom("maisonyukog1@gmail.com"); // 這行不要動
        helper.setTo(toMemberEmail);
        helper.setSubject(subject);

        String newPassword = "Pw123456"; 
        

        // htmlContent改成自己要的內容
        String htmlContent = """
        	    <div style='font-family: Arial, sans-serif; color: #333333; max-width: 650px; margin: auto; font-size: 1.35em;'>
        	        <!-- 飯店橫幅圖片 -->
        	        <img src='cid:headerImage' alt='飯店橫幅圖' style='width: 95%; max-width: 675px; height: auto; display: block; border: none; margin: 0 auto;'>

        	        <!-- 正文內容 -->
        	        <div style='padding: 25px;'>
        	            <h2 style='color: #c06014; font-size: 1.8em; margin-top: 5px;'>密碼變更通知！</h2>"""
        	            + "<p style='font-size: 1.05em;'>親愛的貴賓您好，</p>"
        	            + """
        	            <p style='font-size: 1.05em;'>這是您的新密碼：+ newPassword</p>     	  
        	        </div>

        	        <!-- 底部區塊 -->
        	        <div style='background-color: #fff4e6; padding: 10px 15px;'>
        	            <div style='display: table; width: 100%;'>
        	                <!-- Logo -->
        	                <div style='display: table-cell; width: 165px; text-align: center; vertical-align: top; padding-right: 20px;'>
        	                    <img src='cid:footerImage' alt='Logo照片' style='width: 150px; height: 150px; object-fit: contain; border-radius: 12px;'>
        	                </div>

        	                <!-- 聯絡資訊 -->
        	                <div style='display: table-cell; vertical-align: top; font-size: 1.1em;'>
        	                    <p style='margin: 10px 0 0 0; line-height: 1.6;'>
        	                        <strong>嶼蔻渡假村 Maison d’Yuko</strong><br>
        	                        南海群島．映濤嶼 9 號，珊瑚灣 2025<br>
        	                        Tel：+886-7-123-4567<br>
        	                        Email：service@maisondyuko.com
        	                    </p>
        	                </div>
        	            </div>
        	        </div>
        	    </div>
        	""";

        helper.setText(htmlContent, true); // true代表html格式
        
        // 加入 header 圖片 (橫幅)
        ClassPathResource headerImage = new ClassPathResource("static/images/email/emailHeader.png");
        helper.addInline("headerImage", headerImage);

        // 加入 footer 圖片 (Logo)
        ClassPathResource footerImage = new ClassPathResource("static/images/email/emailLogo.png");
        helper.addInline("footerImage", footerImage);

        mailSender.send(message);
    }
}