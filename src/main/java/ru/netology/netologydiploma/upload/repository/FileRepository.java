package ru.netology.netologydiploma.upload.repository;

import liquibase.pro.packaged.A;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.netologydiploma.security.exception.ErrorDeleteFileException;
import ru.netology.netologydiploma.security.exception.ErrorUploadFileException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
@Repository
public class FileRepository {

    public void checkExistenceDirectory(String link) throws ErrorUploadFileException {
        File file = new File(link);
        if (!file.exists()) {
            if (!file.mkdir()){
                throw new ErrorUploadFileException("ОШИБКА СОЗДАНИИ ДИРЕКТОРИИ НА СЕРВЕРЕ");
            }
            log.info("CОХРАНЕНИЕ: Создание директории по адресу.");
        }
    }

    public void saveFileOnDisk(String link, MultipartFile file, String userName, String filename) throws ErrorUploadFileException {
        File fileToSave = new File(link);
        try {
            file.transferTo(fileToSave);
            log.info("{} - Сохранение файла {} по адресу {}", userName, filename, link);
        } catch (IOException e) {
            log.error("{} - ОШИБКА СОХРАНЕНИЯ: файл: {}  не может быть сохранен в директорию {}", userName, filename, link.toString());
            throw new ErrorUploadFileException("ОШИБКА СОХРАНЕНИЯ ЗАПИСИ ФАЙЛА НА СЕРВЕР");
        }
    }

    public void deleteFileFromDisk(String link, String userName, String fileName) throws ErrorDeleteFileException {
        try {
            Files.deleteIfExists(Paths.get(link));
        } catch (NullPointerException e) {
            log.error("{} - ОШИБКА УДАЛЕНИЯ: Файл {} не найден", userName, fileName);
            throw new ErrorDeleteFileException("ФАЙЛ НЕ НАЙДЕН НА СЕРВЕРЕ");
        } catch (IOException e) {
            log.error("{} - ОШИБКА УДАЛЕНИЯ: Файл {}", userName, fileName);
            throw new ErrorDeleteFileException("ОШИБКА УДАЛЕНИЯ ФАЙЛА С СЕРВЕРА");
        }
    }

    public Resource downloadFile(String link, String userName , String fileName) throws ErrorUploadFileException {
        Resource resource;
        try {
          resource = new UrlResource(Paths.get(link).toUri());
        }catch (IOException e){
            log.error("{} - Файл {} не найден", userName, fileName);
            throw new ErrorUploadFileException("ОШИБКА ПРОЧТЕНИЯ ФАЙЛА С СЕРВЕРА");
        }

        if (!resource.exists()) {
            log.error("{} - Файл {} не найден", userName, fileName);
            throw new ErrorUploadFileException("ОШИБКА ЧТЕНИЯ ФАЙЛА С СЕРВЕРА");
        }

        return resource;
    }

    public void editFileName(String oldLink, String newLink, String userName, String fileName) throws ErrorUploadFileException {
        //ПЕРЕИМЕНОВАНИЕ ФАЙЛА
        File newFile = new File(newLink);
        File oldFile = new File(oldLink);
//        System.out.println(oldFile.exists());
        if (!oldFile.renameTo(newFile)) {
            log.error("{} - ошибка переименования файла {}", userName, fileName);
            throw new ErrorUploadFileException("ОШИБКА ПЕРЕИМЕНОВАНИЯ ФАЙЛА");
        }
    }

}
