package com.example.demo;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.specification.RequestSpecification;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;

import static io.restassured.RestAssured.given;
/* refer to:
https://github.com/testcontainers/workshop
* */

// this is for normal h2db test cases.
// @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

//this is for testcontainers for postgresql
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "spring.datasource.url=jdbc:tc:postgresql:14-alpine://testcontainers/workshop"
})
public class AbstractIntegrationTest {

    @Test
    public void contextLoads() {
        //
    }

    protected RequestSpecification requestSpecification;

    @LocalServerPort
    protected int localServerPort;

    static final GenericContainer redis = new GenericContainer("redis:6-alpine")
            .withExposedPorts(6379);

    static final GenericContainer kafka = new GenericContainer("confluentinc/cp-kafka:6.2.1")
            .withExposedPorts(9092);/**/

    static final GenericContainer mongo = new GenericContainer("mongo:4.4.2")
            .withExposedPorts(27017);/**/

    /**
     * @param registry
     */
    @DynamicPropertySource
    public static void configureProperty(DynamicPropertyRegistry registry) {
        redis.start();
        registry.add("spring.redis.host", redis::getHost);
        registry.add("spring.redis.port", redis::getFirstMappedPort);
        //
        kafka.start();
//        registry.add("spring.kafka.bootstrap-servers", kafka::getBootstrapServers);
        // registry.add("spring.kafka.consumer.bootstrap-servers", kafka::getBootstrapServers);
        // registry.add("spring.kafka.producer.bootstrap-servers", kafka::getBootstrapServers);

        mongo.start();
//        registry.add("spring.data.mongodb.uri", mongo::getReplicaSetUrl);
    }


    @BeforeEach
    public void setUpAbstractIntegrationTest() {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        requestSpecification = new RequestSpecBuilder()
                .setPort(localServerPort)
                .addHeader(
                        HttpHeaders.CONTENT_TYPE,
                        MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @AfterAll
    static void tearDown() {
        redis.stop();
        kafka.stop();
        mongo.stop();
    }

    @Test
    public void healthy() {
        given(requestSpecification)
                .when()
                .get("/actuator/health")
                .then()
                .statusCode(200)
                .log().ifValidationFails(LogDetail.ALL);
    }
}
