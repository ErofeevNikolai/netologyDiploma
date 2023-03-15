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
import ru.netology.netologydiploma.security.customization.JWTProvider;
import ru.netology.netologydiploma.security.exception.ErrorDeleteFileException;
import ru.netology.netologydiploma.security.exception.ErrorUploadFileException;
import ru.netology.netologydiploma.security.repository.UserRepository;
import ru.netology.netologydiploma.upload.repository.FileRepository;
import ru.netology.netologydiploma.upload.repository.UploadRepository;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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

    @Mock
    FileRepository fileRepository;


    @BeforeEach
    void befo() {
        when(jwtProvider.getLoginFromToken(any())).thenReturn(LOGIN);
        when(userRepository.findByUserName(any())).thenReturn(USER);

    }

    @Test
    void postUpload() throws ErrorUploadFileException {
        when(uploadRepository.existsByFileNameAndUser(any(), any())).thenReturn(false);
        uploadService.postUpload(FILE, FILENAME, TOKEN);

        Mockito.verify(fileRepository, Mockito.times(1)).saveFileOnDisk(any(), any(), any(), any());
        Mockito.verify(uploadRepository, Mockito.times(1)).save(any());
    }

    @Test
    void deleteFile() throws IOException {
        when(uploadRepository.existsByFileNameAndUser(any(), any())).thenReturn(true);
        uploadService.deleteFile(TOKEN, FILENAME);

        Mockito.verify(fileRepository, Mockito.times(1)).deleteFileFromDisk(any(), any(), any());
        Mockito.verify(uploadRepository, Mockito.times(1)).delete(any());
    }

    @Test
    void downloadFile() throws ErrorUploadFileException, ErrorDeleteFileException {
        when(uploadRepository.existsByFileNameAndUser(any(), any())).thenReturn(true);

        uploadService.downloadFile(TOKEN, FILENAME);

        Mockito.verify(fileRepository, Mockito.times(1)).downloadFile(any(), any(), any());
        Mockito.verify(uploadRepository, Mockito.times(1)).findLinkByFileNameAndUser(any(), any());
    }

    @Test
    void editFileName() throws ErrorUploadFileException {
        when(uploadRepository.existsByFileNameAndUser(FILENAME, USER)).thenReturn(true);
        when(uploadRepository.existsByFileNameAndUser(NEW_FILENAME, USER)).thenReturn(false);

        when(uploadRepository.findUploadFileByUserAndFileName(USER, FILENAME)).thenReturn(uploadFile);


        uploadService.editFileName(TOKEN, FILENAME, NEW_FILENAME);

        Mockito.verify(fileRepository, Mockito.times(1)).editFileName(any(), any(), any(), any());
        assertEquals(uploadFile.getFileName(), NEW_FILENAME);
        Mockito.verify(uploadRepository, Mockito.times(1)).save(uploadFile);
    }

}