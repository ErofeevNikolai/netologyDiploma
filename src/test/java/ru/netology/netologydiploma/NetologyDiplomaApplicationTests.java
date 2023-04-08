package ru.netology.netologydiploma;

import org.junit.Assert;
import org.junit.FixMethodOrder;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.netology.netologydiploma.security.dto.AuthToken;
import ru.netology.netologydiploma.security.dto.LoginRequest;

import java.io.File;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@Testcontainers
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class NetologyDiplomaApplicationTests {
    @Autowired
    private TestRestTemplate restTemplate;
    private static String url = "http://localhost:5050";
    private static String token;

    @Container
    public static DockerComposeContainer compose = new DockerComposeContainer(new File("docker-compose.yaml"));

    @BeforeAll
    static void start() throws InterruptedException {
        Thread.sleep(2_000);
    }

    @Test
    public void a_testLogin() {
        String urlLogin = url + "/login";
        LoginRequest loginRequest = new LoginRequest("kolia", "1234");

        ResponseEntity<AuthToken> responseLogin = restTemplate.postForEntity(urlLogin, loginRequest, AuthToken.class);
        token = "Bearer " + responseLogin.getBody().getAuthToken();

        Assert.assertEquals(HttpStatus.OK, responseLogin.getStatusCode());
    }

    @Test
    public void b_testChek() {

        HttpHeaders headers = new HttpHeaders();

        headers.set("auth-token", token);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        //ПРОВЕРКА ТОКЕНА
        ResponseEntity<String> responseCheckToken = restTemplate.exchange("http://localhost:5050/check", HttpMethod.POST, requestEntity, String.class);
        Assert.assertEquals(HttpStatus.OK, responseCheckToken.getStatusCode());
    }

    @Test
    public void c_testList() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("auth-token", token);
        HttpEntity<Void> request = new HttpEntity<>(headers);

        ResponseEntity<String> responseList = restTemplate.exchange("http://localhost:5050/list?limit=3", HttpMethod.GET, request, String.class);
        Assert.assertEquals(HttpStatus.OK, responseList.getStatusCode());
    }
}
