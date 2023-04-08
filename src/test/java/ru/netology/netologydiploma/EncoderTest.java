package ru.netology.netologydiploma;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

//ГЕНЕРАЦИЯ ПАРОЛЕЙ ДЛЯ ВНЕСЕНИЯ В БАЗУ ДАННЫХ
public class EncoderTest {
    @Test
    public void bcryptEncoder() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encoded = bCryptPasswordEncoder.encode("1234");
        System.out.println("Becrypt encoded "+encoded);
        String pass = "$2a$10$827rq1ZTWfT4bwfjM5wDNuSayLdRLm7vngiQMfq/VAfTHTyQTS5S6";
        System.out.println(bCryptPasswordEncoder.matches("1234", pass));
    }
}
