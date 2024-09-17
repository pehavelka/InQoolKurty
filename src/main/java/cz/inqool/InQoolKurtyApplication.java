package cz.inqool;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class InQoolKurtyApplication {

	public static void main(String[] args) {
		log.info("http://localhost:8080/h2-console");
		SpringApplication.run(InQoolKurtyApplication.class, args);
	}

}
