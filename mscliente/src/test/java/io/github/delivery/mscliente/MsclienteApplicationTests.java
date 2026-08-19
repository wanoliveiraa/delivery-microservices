package io.github.delivery.mscliente;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.util.StreamUtils;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Testcontainers
public class MsclienteApplicationTests {

	@Container
	@ServiceConnection
	static PostgreSQLContainer postgres =
			new PostgreSQLContainer("postgres:16")
					.withDatabaseName("mscliente_test")
					.withUsername("postgres")
					.withPassword("postgres");

	@Autowired
	private MockMvc mockMvc;

	public static final ObjectMapper objectMapper = new ObjectMapper();

	public ResultActions doRequest(RequestBuilder requestBuilder) throws Exception {
		return mockMvc.perform(requestBuilder);
	}

	public static <T> T readJsonFileAndConvert(String filePath, Class<T> clazz) throws IOException {
		return objectMapper.readValue(readJsonFile(filePath), clazz);
	}

	private static String readJsonFile(String filePath) throws IOException {
		return StreamUtils.copyToString(new ClassPathResource(filePath).getInputStream(), StandardCharsets.UTF_8);
	}

}
