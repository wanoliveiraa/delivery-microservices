package io.github.delivery.mscliente;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MsclienteApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsclienteApplication.class, args);
	}

}
