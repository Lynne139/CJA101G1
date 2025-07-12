package com.resto.utils;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.resto.entity.RestoOrderVO;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class RestoEmailService {
	
	@Autowired
    private JavaMailSender mailSender;
    // Example改成自己的e.g.RoomBooking，參數可自訂(e.g.加入RoomOrder roomOrder)
    public void sendRestoRegiEmail(String toEmail, RestoOrderVO restoOrderVO) throws MessagingException, IOException {

        toEmail = "cja101g1@gmail.com"; // 收件者電郵，這行僅供測試，實際應是傳入的參數
        String subject = "【嶼蔻渡假村】餐廳訂位成功通知信"; // e.g.【嶼蔻渡假村】訂房成功通知信
        
        MimeMessage message = mailSender.createMimeMessage();
        
        // true = multipart message
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");   
        helper.setFrom("maisonyukog1@gmail.com"); // 這行不要動
        helper.setTo(toEmail);
        helper.setSubject(subject);

        String customerName = restoOrderVO.getOrderGuestName(); // 改 e.g. = roomOrder.getCustomerName()
        String orderId = restoOrderVO.getRestoOrderId().toString(); // 改...
        String regiDate = restoOrderVO.getRegiDate().format(DateTimeFormatter.ofPattern("yyyy年M月d日")); // 改...
        String regiPreiod = restoOrderVO.getSnapshotPeriodName(); // 改...
        String regiTimeslot = restoOrderVO.getSnapshotTimeslotName(); // 改...
        String resto = restoOrderVO.getSnapshotRestoName(); // 改...
        String guestCount = restoOrderVO.getRegiSeats().toString();// 改...
        String HighChairs = restoOrderVO.getHighChairs().toString();// 改...
        String Req = restoOrderVO.getRegiReq();// 改...

        // htmlContent改成自己要的內容
        String htmlContent = """
        	    <div style='font-family: Arial, sans-serif; color: #333333; max-width: 650px; margin: auto; font-size: 1.35em;'>
        	        <!-- 飯店橫幅圖片 -->
        	        <img src='cid:headerImage' alt='飯店橫幅圖' style='width: 95%; max-width: 675px; height: auto; display: block; border: none; margin: 0 auto;'>

        	        <!-- 正文內容 -->
        	        <div style='padding: 25px;'>
        	            <h2 style='color: #c06014; font-size: 1.8em; margin-top: 5px;'>您的訂位已確認！</h2>"""
        	            + "<p style='font-size: 1.05em;'>親愛的 <strong>" + customerName + "</strong> 貴賓您好，</p>"
        	            + """
        	            <p style='font-size: 1.05em;'>感謝您選擇 <strong>嶼蔻渡假村 Maison d’Yuko</strong>，我們已收到您的餐廳訂位需求。以下為您的訂位資訊：</p>

        	            <table style='width: 100%; border-collapse: collapse; border-spacing: 0; margin-top: 20px; font-size: 1.05em;'>
        	                """
        	                + "<tr><td style='padding: 0;'><strong>預約餐廳：</strong></td><td>" + resto + "</td></tr>"
        	                + "<tr><td style='padding: 0;'><strong>用餐日期：</strong></td><td>" + regiDate + "</td></tr>"
        	                + "<tr><td style='padding: 0;'><strong>用餐時段：</strong></td><td>" + regiPreiod + " " + regiTimeslot + "</td></tr>"
        	                + "<tr><td style='padding: 0;'><strong>用餐人數：</strong></td><td>" + guestCount + "位</td></tr>"
        	                + "<tr><td style='padding: 0;'><strong>兒童椅需求數：</strong></td><td>" + HighChairs + "</td></tr>"
        	                + "<tr><td style='padding: 0;'><strong>其他需求備註：</strong></td><td>" + Req + "</td></tr>"
        	                + "<tr><td style='padding: 0;'><strong>訂位編號：</strong></td><td>" + orderId + "</td></tr>"
        	            + """
        	            </table>

        	            <p style='margin-top: 20px; font-size: 1.05em;'><strong>訂位須知：</strong></p>
        	            <ul style='font-size: 1.05em; padding-left: 20px;'>
        	                <li style='margin-left: 70px;'>當日用餐，訂位時間將保留10分鐘</li>
        	                <li style='margin-left: 70px;'>若行程有延遲需晚入場，敬請主動聯繫餐廳</li>
        	                <li style='margin-left: 70px;'>為保有餐飲完美體驗，嶼蔻保留取消或調整預約的權利</li>
        	            </ul>

        	            <p style='font-size: 1.05em;'>如需修改或需其他資訊，請與我們聯繫。期待您的蒞臨！</p>
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
