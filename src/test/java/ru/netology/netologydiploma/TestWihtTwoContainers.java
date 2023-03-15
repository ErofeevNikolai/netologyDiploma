//package ru.netology.netologydiploma;
//
//import org.junit.Test;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.BeforeAll;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.web.client.TestRestTemplate;
//import org.springframework.test.context.DynamicPropertyRegistry;
//import org.springframework.test.context.DynamicPropertySource;
//import org.springframework.web.client.RestTemplate;
//import org.testcontainers.containers.GenericContainer;
//import org.testcontainers.containers.MySQLContainer;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.junit.jupiter.Testcontainers;
//
//import java.util.Map;
//
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@Testcontainers
//public class TestWihtTwoContainers {
//
//
//    public static MySQLContainer<?> mysql = new MySQLContainer("mysql")
//            .withDatabaseName("diplom")
//            .withUsername("root")
//            .withPassword("Password");
//
//    @DynamicPropertySource
//    static void configureTestProperties(DynamicPropertyRegistry registry) {
//        registry.add("spring.datasource.url", () -> mysql.getJdbcUrl());
//        registry.add("spring.datasource.username", () -> mysql.getUsername());
//        registry.add("spring.datasource.password", () -> mysql.getPassword());
//    }
//
//
//    public static GenericContainer<?> diplom = new GenericContainer<>("diplom")
//            .withExposedPorts(5050)
//            .dependsOn(mysql);
//
//    @Test
//    public void a_testLogin() throws InterruptedException {
//        mysql.start();
//        Integer port = mysql.getMappedPort(3306);
//        System.out.println(port);
//
//        diplom.withEnv(Map.of("SPRING_DATASOURCE_URL", "jdbc:mysql://database:" + port + "/diplom"));
//        diplom.start();
//        
//        Assertions.assertTrue(diplom.isRunning());
//    }
//}
