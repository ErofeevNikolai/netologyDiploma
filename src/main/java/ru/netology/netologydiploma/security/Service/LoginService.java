package ru.netology.netologydiploma.security.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.netology.netologydiploma.security.exception.BadCredentialsException;
import ru.netology.netologydiploma.security.DTO.AuthToken;
import ru.netology.netologydiploma.security.DTO.LoginRequest;
import ru.netology.netologydiploma.security.customization.JWTProvider;
import ru.netology.netologydiploma.security.entity.AuthorizationStatus;
import ru.netology.netologydiploma.security.entity.User;
import ru.netology.netologydiploma.security.repository.AuthorizationRepositoty;
import ru.netology.netologydiploma.security.repository.UserRepository;

@Service
@AllArgsConstructor
@Slf4j
public class LoginService {
    private UserRepository userRepository;
    private JWTProvider jwtProvider;
    private AuthorizationRepositoty authorizationRepositoty;

    public AuthToken login(LoginRequest loginRequest) {
        String login = loginRequest.getLogin();
        User user = userRepository.findByUserName(login);

        //ПРОВЕРКА СУЩЕСТВОВАНИЧ ПОЛЬЗОВАТЕЛЯ
        if (user == null) {
            log.error("Пользователь не зарегистрирован");
            throw new BadCredentialsException("ПОЛЬЗОВАТЕЛЬ НЕ ЗАРЕГИСТРИРОВАН");
        }


        //ПРОВЕРКА КОРРЕКТНОСТИ ПАРОЛЯ
        if (!loginRequest.getPassword().equals(user.getPassword())) {
            log.error("АВТОРИЗАЦИЯ: Пользователь указал неверный пароль", login);
            throw new BadCredentialsException("Указан неверный пароль");
        }
        String token = jwtProvider.generateToken(login);

        //ЗАПИСЬ ТОКЕНА В БД
        AuthorizationStatus authorizationStatus = authorizationRepositoty.findByUser(user);
        if (authorizationStatus != null) {
            authorizationStatus.setToken(token);
            authorizationRepositoty.save(authorizationStatus);
        } else {
            authorizationRepositoty.save(new AuthorizationStatus(token, user));
        }
        log.info("AUTHORIZATION: User_{} - authorized",login);

        return new AuthToken(token);
    }
}
