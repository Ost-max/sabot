package dev.ostmax.sabot;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.TimeZone;

@SpringBootApplication()
@EnableScheduling
public class SabotApplication {

	public static void main(String[] args) {
		SpringApplication.run(SabotApplication.class, args);
	}

}
