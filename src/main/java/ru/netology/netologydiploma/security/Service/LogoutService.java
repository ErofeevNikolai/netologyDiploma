package ru.netology.netologydiploma.security.Service;

import lombok.AllArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;
import ru.netology.netologydiploma.security.entity.AuthorizationStatus;
import ru.netology.netologydiploma.security.repository.AuthorizationRepositoty;
import ru.netology.netologydiploma.security.repository.UserRepository;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
@AllArgsConstructor
public class LogoutService implements LogoutHandler {

    private AuthorizationRepositoty authorizationRepositoty;
    UserRepository userRepository;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String token = request.getHeader("auth-token");

        if (token == null || token.equals("")) {
            System.out.println("token is null");
            return;
        }
        AuthorizationStatus authorizationStatus = authorizationRepositoty.findByToken(token);
        authorizationRepositoty.delete(authorizationStatus);
    }
}
