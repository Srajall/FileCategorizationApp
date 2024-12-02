package com.example.documentmanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DocumentmanagementApplication {

	public static void main(String[] args) {
		// Set the Google Cloud credentials path
        System.setProperty("GOOGLE_APPLICATION_CREDENTIALS", "C:/Users/2153045/Downloads/constant-disk-350520-4191821ded2c.json");

	
		SpringApplication.run(DocumentmanagementApplication.class, args);
	}

}
