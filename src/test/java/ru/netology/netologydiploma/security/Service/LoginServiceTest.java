package ru.netology.netologydiploma.security.Service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.netology.netologydiploma.security.customization.JWTProvider;
import ru.netology.netologydiploma.security.repository.AuthorizationRepositoty;
import ru.netology.netologydiploma.security.repository.UserRepository;
import ru.netology.netologydiploma.security.dto.LoginRequest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static ru.netology.netologydiploma.TestVariable.USER;


@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
class LoginServiceTest {

    @InjectMocks
    LoginService loginService;
    @Mock
    UserRepository userRepository;
    @Mock
    JWTProvider jwtProvider;
    @Mock
    AuthorizationRepositoty authorizationRepositoty;

    LoginRequest loginRequest;


    @BeforeEach
    void beforeStart() {
        loginRequest = new LoginRequest("ivan", "1234");
    }

    @Test
    void login() {
        when(userRepository.findByUserName(anyString())).thenReturn(USER);
        when(jwtProvider.generateToken(anyString())).thenReturn("123456789");
        when(authorizationRepositoty.findByUser(any())).thenReturn(null);

        assertEquals(loginService.login(loginRequest).getAuthToken(), "123456789");
    }
}