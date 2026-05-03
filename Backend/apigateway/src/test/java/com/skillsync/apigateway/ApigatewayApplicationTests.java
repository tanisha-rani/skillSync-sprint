package com.skillsync.apigateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
		"jwt.secret=mysecretkeymysecretleymysecretkey32",
		"spring.cloud.config.enabled=false",
		"eureka.client.enabled=false",
		"management.tracing.enabled=false"
})
class ApigatewayApplicationTests {

	@Test
	void contextLoads() {
	}

}
