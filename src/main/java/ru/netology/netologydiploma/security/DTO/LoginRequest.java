package ru.netology.netologydiploma.security.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginRequest {
    private String login;
    private String password;
}
