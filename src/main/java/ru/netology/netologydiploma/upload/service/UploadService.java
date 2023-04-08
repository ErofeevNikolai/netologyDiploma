package ru.netology.netologydiploma.upload.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.netologydiploma.security.entity.User;
import ru.netology.netologydiploma.security.exception.ErrorDeleteFileException;
import ru.netology.netologydiploma.security.exception.ErrorGettingFileListException;
import ru.netology.netologydiploma.security.exception.ErrorInputDataException;
import ru.netology.netologydiploma.security.exception.ErrorUploadFileException;
import ru.netology.netologydiploma.security.repository.UserRepository;
import ru.netology.netologydiploma.upload.dto.FileSize;
import ru.netology.netologydiploma.upload.entity.UploadFile;
import ru.netology.netologydiploma.upload.repository.UploadRepository;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class UploadService {
    private final UploadRepository uploadRepository;
    public UploadService(UploadRepository uploadRepository) {
        this.uploadRepository = uploadRepository;
    }

    public void postUpload(MultipartFile file, String fileName) throws ErrorUploadFileException {
        //ПОЛУЧЕНИЕ ПОЛЬЗОВАТЕЛЯ И ЕГО ИМЕНИ
        User user = getUserFromAuthentication();
        String userName = user.getUsername();

        //ПРОВЕРКА ЧТО ФАЙИЛ ПЕРЕДАН
        if (file == null) {
            log.error("{} - ОШИБКА СОХРАНЕНИЯ: Файл не передан для сохранения", userName);
            throw new ErrorInputDataException("ФАЙЛ НА ПЕРЕДАН НА СЕРВЕР");
        }

        //ПРОВЕРКА ЗАПРОСА
        checkFileNameNotNull(fileName, userName);
        checkingNameUnique(fileName, user, userName);

        //ЗАПИСЬ ПУТИ ФАЙЛА В БД
        try {
            uploadRepository.save(new UploadFile(user, fileName, Math.toIntExact(file.getSize()), file.getBytes()));
            log.info("{} - Внесение данных в БД:  файла {}:", userName, fileName);
        } catch (Exception e) {
            throw new ErrorUploadFileException("ОШИБКА ЗАПИСИ ФАЙЛА В СЕРВЕР");
        }
    }

    public void deleteFile(String fileName) throws ErrorUploadFileException {
        //ПОЛУЧЕНИЕ ПОЛЬЗОВАТЕЛЯ И ЕГО ИМЕНИ
        User user = getUserFromAuthentication();
        String userName = user.getUsername();

        //ПРОВЕРКА ЗАПРОСА
        checkFileNameNotNull(fileName, userName);
        checkDatabaseHasFile(fileName, user, userName);

        //УДАЛЕНИЕ ЗАПИСИ ИЗ БД
        try {
            uploadRepository.delete(uploadRepository.findUploadFileByUserAndFileName(user, fileName));
            log.info("{} - Сохранение файла {}", userName, fileName);
        } catch (Exception e) {
            log.error("{} - ОШИБКА СОХРАНЕНИЯ: файл: {}", userName, fileName);
            throw new ErrorUploadFileException("ОШИБКА СОХРАНЕНИЯ ЗАПИСИ ФАЙЛА НА СЕРВЕР");
        }
    }

    public byte[] downloadFile(String fileName) throws ErrorUploadFileException {
        //ПОЛУЧЕНИЕ ПОЛЬЗОВАТЕЛЯ И ЕГО ИМЕНИ
        User user = getUserFromAuthentication();
        String userName = user.getUsername();

        //ПРОВЕРКА ЗАПРОСА
        checkFileNameNotNull(fileName, userName);
        checkDatabaseHasFile(fileName, user, userName);

        //ПОЛУЧЕНИЕ ФАЙЛА
        try {
            return uploadRepository.findFileByFileNameAndUser(fileName, user);
        } catch (Exception e) {
            log.error("{} - Ошибка получения файла {}", userName, fileName);
            throw new ErrorUploadFileException("ОШИБКА СКАЧИВАНИЯ ФАЙЛА");
        }
    }

    @Transactional
    public void editFileName(String fileName, String newFileName) throws ErrorUploadFileException {
        //ПОЛУЧЕНИЕ ПОЛЬЗОВАТЕЛЯ И ЕГО ИМЕНИ
        User user = getUserFromAuthentication();
        String userName = user.getUsername();

        //ПРОВЕРКА ЗАПРОСА
        checkFileNameNotNull(fileName, userName);
        checkDatabaseHasFile(fileName, user, userName);
        checkingNameUnique(newFileName, user, userName);

        //ПОЛУЧАЕМ ЭЛЕМЕНТ БД
        UploadFile uploadFile = uploadRepository.findUploadFileByUserAndFileName(user, fileName);

        //ИЗМЕНЕНИЕ ЭКЗЕМПЛЧРА БД
        uploadFile.setFileName(newFileName);
        try {
            uploadRepository.save(uploadFile);
            log.info("{} - Изменение имени фала {} на: файла {}:", userName, fileName, newFileName);
        } catch (Exception e) {
            log.error("{} - ошибка переименования файла {}", userName, fileName);
            throw new ErrorUploadFileException("ОШИБКА ПЕРЕИМЕНОВАНИЯ ФАЙЛА");
        }
    }

    public List<FileSize> getAllFiles(Integer limit) throws ErrorGettingFileListException {
        //ПОЛУЧЕНИЕ ЮЗЕРА
        User user = getUserFromAuthentication();

        //ДЛЯ ЗАДАНИЯ ЛИМИТА ВЫБОРКИ ИСПОЛЬЗУЕМ Pageable
        Pageable pageable = PageRequest.of(0, limit);
        List<FileSize> allFiles;
        try {
            allFiles = uploadRepository.findFileSize(user, pageable);
        } catch (Exception e) {
            log.error("{}: Ошибка чтения бд", user.getUsername());
            throw new ErrorGettingFileListException("ОШИБКА ЧТЕНИЯ ФАЙЛА ИЗ БД");
        }
        //ЗАПРОС В БД
        return allFiles;
    }

    //ПРОВЕРКА ЧТО УКАЗАННЫЙ ФАЙЛ ЕСТЬ В БД
    private void checkDatabaseHasFile(String fileName, User user, String userName) {
        if (!uploadRepository.existsByFileNameAndUser(fileName, user)) {
            log.error("{}: Файл {} не найден", userName, fileName);
            throw new ErrorInputDataException("УКАЗАННЫЙ ФАЙЛ НЕ НАЙДЕН");
        }
    }

    //ПРОВЕРКА ЧТО ИМЯ НЕ ПУСТОЕ
    private void checkFileNameNotNull(String fileName, String userName) {
        if (fileName == null || fileName.equals("")) {
            log.error("{}: Не передано имя файла для удаления", userName);
            throw new ErrorInputDataException("НЕ УКАЗАНО ИМЕНЯ ФАЙЛА");
        }
    }

    //ПРОВЕРКА УНИКАЛЬНОСТИ ИМЕНИ НОВГО ФАЙЛА
    private void checkingNameUnique(String newFileName, User user, String userName) {
        if (uploadRepository.existsByFileNameAndUser(newFileName, user)) {
            log.error("{} - ОШИБКА СОХРАНЕНИЯ: Попытка дублировании имения файла", userName);
            throw new ErrorInputDataException("ФАИЛ С УКАЗАННЫМ ИМЕНЕМ УЖЕ СУЩЕСТВУЕТ");
        }
    }

    protected User getUserFromAuthentication() {
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}

