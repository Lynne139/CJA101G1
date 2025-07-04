package com;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.member.model.MemberImageUploader;

@SpringBootApplication
@EnableScheduling 
public class Cja101G1Application {

	public static void main(String[] args) {
		SpringApplication.run(Cja101G1Application.class, args);
	}
	
	@Bean
	public CommandLineRunner run(MemberImageUploader uploader) {
		return args -> {
			uploader.uploadAllMemberImages(); // 啟動時塞入會員照片 1~10.png
		};
	}
}
