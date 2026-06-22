package com.jobingestion.jobingestionplatform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class JobIngestionPlatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(JobIngestionPlatformApplication.class, args);
	}

}
