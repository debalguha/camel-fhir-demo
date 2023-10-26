package com.example.demo.fhir;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.MockEndpoints;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.util.concurrent.TimeUnit;

@CamelSpringBootTest
@SpringBootTest(classes = CamelFhirDemoApplication.class)
@ActiveProfiles("test")
@MockEndpoints("direct:result")
class CamelFhirDemoApplicationTests {

	@Autowired
	private CamelContext camelContext;

	@Autowired
	private ProducerTemplate producerTemplate;

	@EndpointInject("mock:direct:result")
	private MockEndpoint mock;

	@Test
	void shouldCreateAPatientRecordInHAPI() throws Exception {
		mock.expectedMessageCount(1);

		FileUtils.copyDirectory(new File("src/test/resources/work"), new File("target/work"));
		Thread.sleep(TimeUnit.SECONDS.toMillis(5));

		mock.assertIsSatisfied();
	}

}
