package io.github.delivery.mssecurity;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MssecurityApplication {

	public static void main(String[] args) {
		SpringApplication.run(MssecurityApplication.class, args);
	}

}
