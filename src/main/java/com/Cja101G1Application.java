package com;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling 
public class Cja101G1Application {

	public static void main(String[] args) {
		SpringApplication.run(Cja101G1Application.class, args);
	}

}
