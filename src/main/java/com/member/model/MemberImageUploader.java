package com.member.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;

@Service
public class MemberImageUploader {

    @Autowired
    private DataSource dataSource;

    public void uploadAllMemberImages() {
        for (int i = 1; i <= 10; i++) {
            String fileName = i + ".png";
            try {
                ClassPathResource imgFile = new ClassPathResource("static/images/member/" + fileName);
                InputStream inputStream = imgFile.getInputStream();
                byte[] imageBytes = inputStream.readAllBytes();

                try (Connection conn = dataSource.getConnection();
                     PreparedStatement ps = conn.prepareStatement("UPDATE member SET member_pic = ? WHERE member_id = ?")) {
                    ps.setBytes(1, imageBytes);
                    ps.setInt(2, i);
                    ps.executeUpdate();
                    
                }
            } catch (Exception e) {
                System.err.println(" 圖片 " + fileName + " 上傳錯誤：" + e.getMessage());
            }
        }
    }
}