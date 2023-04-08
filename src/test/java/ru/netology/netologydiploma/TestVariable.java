package ru.netology.netologydiploma;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.netologydiploma.security.dto.LoginRequest;
import ru.netology.netologydiploma.security.entity.Role;
import ru.netology.netologydiploma.security.entity.User;
import ru.netology.netologydiploma.upload.entity.UploadFile;

public class TestVariable {

    //USER:
    public static final User USER = new User(1L, "Ivan", "$2a$10$827rq1ZTWfT4bwfjM5wDNuSayLdRLm7vngiQMfq/VAfTHTyQTS5S6", new Role(1L, "USER"));

    // jwtProvider.getLoginFromToken(any()))
    public static final String LOGIN = "Ivan";

    public static final String FILENAME = "fileName";
    public static final String NEW_FILENAME = "newFileName";

    public static final String ORIGINAL_FILE = "FILENAME.jpg";
    public static final MultipartFile FILE = new MockMultipartFile("FILENAME.jpg", "FILENAME.jpg", null, FILENAME.getBytes());
    public static final String TOKEN = "Bearer TOKEN";

    public static final String LINK = "LINK";
    public static final Integer SIZE = Math.toIntExact(FILE.getSize());

    public static UploadFile uploadFile = new UploadFile(USER, FILENAME, SIZE, ORIGINAL_FILE.getBytes() );

    public static final LoginRequest LOGIN_REQUEST = new LoginRequest("ivan", "1234");
}
