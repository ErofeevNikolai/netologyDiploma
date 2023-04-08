package ru.netology.netologydiploma.upload.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import ru.netology.netologydiploma.security.customization.JWTProvider;
import ru.netology.netologydiploma.security.exception.ErrorDeleteFileException;
import ru.netology.netologydiploma.security.exception.ErrorUploadFileException;
import ru.netology.netologydiploma.security.repository.UserRepository;
import ru.netology.netologydiploma.upload.repository.UploadRepository;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static ru.netology.netologydiploma.TestVariable.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.STRICT_STUBS)
class UploadServiceTest {
    @InjectMocks
    UploadService uploadService;
    @Mock
    JWTProvider jwtProvider;
    @Mock
    UploadRepository uploadRepository;
    @Mock
    UserRepository userRepository;


    @BeforeEach
    void befo() {
        //Заглушка для SecurityContextHolder.getContext().getAuthentication().getPrincipal():
        Authentication auth = mock(Authentication.class);
        // устанавливаем заглушку Principal для заглушки Authentication
        when(auth.getPrincipal()).thenReturn(USER);
        // создаем заглушку для объекта SecurityContextHolder
        SecurityContext securityContext = mock(SecurityContext.class);
        // устанавливаем заглушку Authentication для заглушки SecurityContextHolder
        when(securityContext.getAuthentication()).thenReturn(auth);

        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void postUpload() throws ErrorUploadFileException, IOException {
        when(uploadRepository.existsByFileNameAndUser(any(), any())).thenReturn(false);
        uploadService.postUpload(FILE, FILENAME);

        Mockito.verify(uploadRepository, Mockito.times(1)).save(any());
    }

    @Test
    void deleteFile() throws ErrorUploadFileException, ErrorDeleteFileException {
        when(uploadRepository.existsByFileNameAndUser(any(), any())).thenReturn(true);
        uploadService.deleteFile(FILENAME);

        Mockito.verify(uploadRepository, Mockito.times(1)).delete(any());
    }

    @Test
    void downloadFile() throws ErrorUploadFileException, ErrorDeleteFileException {
        when(uploadRepository.existsByFileNameAndUser(any(), any())).thenReturn(true);

        uploadService.downloadFile(FILENAME);

        Mockito.verify(uploadRepository, Mockito.times(1)).findFileByFileNameAndUser(any(), any());
    }

    @Test
    void editFileName() throws ErrorUploadFileException {
        when(uploadRepository.existsByFileNameAndUser(FILENAME, USER)).thenReturn(true);
        when(uploadRepository.existsByFileNameAndUser(NEW_FILENAME, USER)).thenReturn(false);
        when(uploadRepository.findUploadFileByUserAndFileName(USER, FILENAME)).thenReturn(uploadFile);

        uploadService.editFileName(FILENAME, NEW_FILENAME);

        Mockito.verify(uploadRepository, Mockito.times(1)).save(uploadFile);
    }

}