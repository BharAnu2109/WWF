package com.wwf.application;

import com.wwf.application.config.TestKafkaConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestKafkaConfig.class)
class WwfApplicationTests {

	@Test
	void contextLoads() {
	}

}