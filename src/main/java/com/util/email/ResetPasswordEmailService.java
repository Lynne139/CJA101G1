package com.util.email;
 
import com.member.model.MemberService;
import com.member.model.MemberVO;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class ResetPasswordEmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private MemberService memberSvc;

    public boolean sendResetPasswordEmail(String toMemberEmail) {
        // 1. 取得會員資料
        MemberVO member = memberSvc.getByEmail(toMemberEmail);
        if (member == null) return false;

        // 2. 產生新密碼
        String newPassword = generateRandomPassword(8);

        // 3. 更新密碼
        member.setMemberPassword(newPassword); // 可加密
        memberSvc.updateMember(member);

        try {
            // 4. 準備 Email
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("maisonyukog1@gmail.com");
            helper.setTo(toMemberEmail);
            helper.setSubject("【嶼蔻渡假村】密碼變更通知信");

            // 5. HTML 內容（用 replace 插入密碼）
            String htmlContent = """
                <div style='font-family: Arial, sans-serif; color: #333333; max-width: 650px; margin: auto; font-size: 1.35em;'>
                    <img src='cid:headerImage' alt='飯店橫幅圖' style='width: 95%%; max-width: 675px; height: auto; display: block; border: none; margin: 0 auto;'>

                    <div style='padding: 25px;'>
                        <h2 style='color: #c06014; font-size: 1.8em; margin-top: 5px;'>密碼變更通知！</h2>
                        <p style='font-size: 1.05em;'>親愛的貴賓您好，</p>
                        <p style='font-size: 1.05em;'>這是您的新密碼：<strong>{{password}}</strong></p>
                        <p style='font-size: 1.05em;'>請儘快登入並變更您的密碼，以保障帳戶安全。</p>
                    </div>

                    <div style='background-color: #fff4e6; padding: 10px 15px;'>
                        <div style='display: table; width: 100%%;'>
                            <div style='display: table-cell; width: 165px; text-align: center; vertical-align: top; padding-right: 20px;'>
                                <img src='cid:footerImage' alt='Logo照片' style='width: 150px; height: 150px; object-fit: contain; border-radius: 12px;'>
                            </div>
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
                """.replace("{{password}}", newPassword);

            helper.setText(htmlContent, true);

            // 6. 加入圖片
            ClassPathResource headerImage = new ClassPathResource("static/images/email/emailHeader.png");
            helper.addInline("headerImage", headerImage);

            ClassPathResource footerImage = new ClassPathResource("static/images/email/emailLogo.png");
            helper.addInline("footerImage", footerImage);

            // 7. 發送
            mailSender.send(message);
            return true;

        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder();
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
