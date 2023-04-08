package ru.netology.netologydiploma.security.customization;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.netology.netologydiploma.security.entity.User;
import ru.netology.netologydiploma.security.repository.AuthorizationRepositoty;
import ru.netology.netologydiploma.security.repository.UserRepository;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

import static io.jsonwebtoken.lang.Strings.hasText;

@Component
@AllArgsConstructor
@Slf4j
public class JWTFilter extends GenericFilter {
    private final JWTProvider jwtProvider;
    private final CustomUserDetailsService customUserDetailsService;
    private final AuthorizationRepositoty authorizationRepositoty;


    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws ServletException, IOException {
        String token = getTokenFormRequest((HttpServletRequest) servletRequest);
        if (checkToken(token)) {
            String userLoin = jwtProvider.getLoginFromToken(token);
            User user = customUserDetailsService.loadUserByUsername(userLoin);
            UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);
            log.info("AUTHENTICATION: User_{} authenticated", userLoin);
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }

    public String getTokenFormRequest(HttpServletRequest request) {
        String requestTokenHeader = request.getHeader("auth-token");
        String token;
        if (hasText(requestTokenHeader) && requestTokenHeader.startsWith("Bearer ")) {
            token = requestTokenHeader.substring(7);
            return token;
        }
        return null;
    }

    public boolean checkToken(String token) {
        //ПРОВEРКА НАЛИЧИЯ ТОКЕНА
        if (token == null) {
            return false;
        }
        //ПРОВЕРКА ВАЛИДНОСТИ ТОКЕНА
        if (!jwtProvider.validateToken(token)) {
            log.error("AUTHENTICATION: Invalid token provided");
            return false;
        }
        //ПРОВЕРКА НА ОТСУТСТВИЕ logout
        if (authorizationRepositoty.findByToken(token) == null) {
            log.error("AUTHENTICATION: The user has logout.");
            return false;
        }
        log.info("AUTHENTICATION: Token validity check passed");
        return true;
    }
}
