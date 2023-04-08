package ru.netology.netologydiploma.security.Service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.netology.netologydiploma.security.exception.BadCredentialsException;
import ru.netology.netologydiploma.security.dto.AuthToken;
import ru.netology.netologydiploma.security.dto.LoginRequest;
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

    private static BCryptPasswordEncoder passwordEcorder = new BCryptPasswordEncoder();

    public AuthToken login(LoginRequest loginRequest) {
        String login = loginRequest.getLogin();
        User user = userRepository.findByUserName(login);

        //ПРОВЕРКА СУЩЕСТВОВАНИЧ ПОЛЬЗОВАТЕЛЯ
        if (user == null) {
            log.error("The user is not registered");
            throw new BadCredentialsException("THE USER IS NOT REGISTERED");
        }

        //ПРОВЕРКА КОРРЕКТНОСТИ ПАРОЛЯ
        if (!passwordEcorder.matches(loginRequest.getPassword(),user.getPassword())) {
            log.error("{}: The user entered an incorrect password", login);
            throw new BadCredentialsException("Invalid password specified");
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
