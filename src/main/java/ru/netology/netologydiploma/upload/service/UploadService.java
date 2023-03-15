package ru.netology.netologydiploma.upload.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.netology.netologydiploma.security.customization.JWTProvider;
import ru.netology.netologydiploma.security.entity.User;
import ru.netology.netologydiploma.security.exception.ErrorDeleteFileException;
import ru.netology.netologydiploma.security.exception.ErrorGettingFileListException;
import ru.netology.netologydiploma.security.exception.ErrorInputDataException;
import ru.netology.netologydiploma.security.exception.ErrorUploadFileException;
import ru.netology.netologydiploma.security.repository.UserRepository;
import ru.netology.netologydiploma.upload.DTO.FileSize;
import ru.netology.netologydiploma.upload.entity.UploadFile;
import ru.netology.netologydiploma.upload.repository.FileRepository;
import ru.netology.netologydiploma.upload.repository.UploadRepository;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.List;

@Service
@Slf4j
public class UploadService {
    @Value("${upload.path}")
    private String uploadPath;
    JWTProvider jwtProvider;
    UploadRepository uploadRepository;
    UserRepository userRepository;
    FileRepository fileRepository;

    public UploadService(JWTProvider jwtProvider, UploadRepository uploadRepository, UserRepository userRepository, FileRepository fileRepository) {
        this.jwtProvider = jwtProvider;
        this.uploadRepository = uploadRepository;
        this.userRepository = userRepository;
        this.fileRepository = fileRepository;
    }

    public void postUpload(MultipartFile file, String filename, String requestToken) throws ErrorUploadFileException {
        //ПОЛУЧЕНИЕ ИМЕНИ ПОЛЬЗОВОТЕЛЯ ПО ЛОГИНУ
        String userName = jwtProvider.getLoginFromToken(requestToken.substring(7));
        User user = userRepository.findByUserName(userName);

        //ПРОВЕРКА ЧТО ФАЙИЛ ПЕРЕДАН
        if (file == null) {
            log.error("{} - ОШИБКА СОХРАНЕНИЯ: Файл не передан для сохранения", userName);
            throw new ErrorInputDataException("ФАЙЛ НА ПЕРЕДАН НА СЕРВЕР");
        }

        //ПРОВЕРКА ЗАПРОСА
        checkFileNameNotNull(filename, userName);
        checkingNameUnique(filename, user, userName);

        //СОЗДАНИЕ ОБЩЕЙ ДИРЕКТОРИИ ДЛЯ ОБМЕНА(ЕСЛИ ОНА НЕ СОЗДАВАЛАСЬ РАНЕЕ)
        fileRepository.checkExistenceDirectory(uploadPath);

        //СОЗАДЕМ StringBuilder ДЛЯ ФОРМИРОВАНИЯ ПУТИ ХРАНЕНИЯ ФАЙЛА
        StringBuilder link = new StringBuilder().append(uploadPath);
        link.append("\\");

        //ДОБАВЛЕНИЕ ИМЕНИ ПОЛЬЗОВАТЕЛЯ В ПУТЬ
        link.append(userName);

        //ПРОВЕКА НАЛИЧИЯ ДИРЕКТОРИЯ ПОЛЬЗОВАТЕЛЯ,
        //В СЛУЧАЕ ОТСУТВИЯ - СОЗДАНИЕ ДИРЕКТОРИИ
        fileRepository.checkExistenceDirectory(link.toString());

        //ДОБАВЛЕНИЕ В ССЫЛКУ ИМЕНИ ФАЙЛА
        link.append("\\").append(filename);

        //ЗАПИСЬ ФАЙЛА НА ДИСК
        fileRepository.saveFileOnDisk(link.toString(), file, userName, filename);

        //ЗАПИСЬ ПУТИ ФАЙЛА В БД
        uploadRepository.save(new UploadFile(userRepository.findByUserName(userName), filename, link.toString(), Math.toIntExact(file.getSize())));
        log.info("{} - Внесение данных в БД:  файла {},  ссылка {}:", userName, filename, link);
    }

    public void deleteFile(String requestToken, String fileName) throws IOException {
        //ПОЛУЧЕНИЕ ИМЕНИ ПОЛЬЗОВОТЕЛЯ ПО ЛОГИНУ
        String userName = jwtProvider.getLoginFromToken(requestToken.substring(7));
        //ПОЛУЧЕНИЕ ОБЪЕКТА USER
        User user = userRepository.findByUserName(userName);

        //ПРОВЕРКА ЗАПРОСА
        checkFileNameNotNull(fileName, userName);
        checkDatabaseHasFile(fileName, user, userName);

        //ПОЛУЧЕНИЕ ССЫЛКИ НА ФАЙЛ
        String link = uploadRepository.findLinkByFileNameAndUser(fileName, user);

        //УДАЛЕНИЕ ФАЙЛА С ДИСКА
        fileRepository.deleteFileFromDisk(link, userName, fileName);

        //УДАЛЕНИЕ ЗАПИСИ ИЗ БД
        uploadRepository.delete(uploadRepository.findUploadFileByUserAndFileName(user, fileName));
    }

    public Resource downloadFile(String requestToken, String fileName) throws ErrorUploadFileException, ErrorDeleteFileException {
        //ПОЛУЧЕНИЕ ИМЕНИ ПОЛЬЗОВОТЕЛЯ ПО ЛОГИНУ
        String userName = jwtProvider.getLoginFromToken(requestToken.substring(7));
        //ПОЛУЧЕНИЕ ОБЪЕКТА USER
        User user = userRepository.findByUserName(userName);

        //ПРОВЕРКА ЗАПРОСА
        checkFileNameNotNull(fileName, userName);
        checkDatabaseHasFile(fileName, user, userName);

        String link = uploadRepository.findLinkByFileNameAndUser(fileName, user);

        //ЧТЕНИЕ ФАЙЛА С СЕРВЕРА
        return fileRepository.downloadFile(link, userName, fileName);
    }

    @Transactional
    public void editFileName(String requestToken, String fileName, String newFileName) throws ErrorUploadFileException {
        String userName = jwtProvider.getLoginFromToken(requestToken.substring(7));
        User user = userRepository.findByUserName(userName);

        //ПРОВЕРКА ЗАПРОСА
        checkFileNameNotNull(fileName, userName);
        checkDatabaseHasFile(fileName, user, userName);
        checkingNameUnique(newFileName, user, userName);

        //ПОЛУЧАЕМ ЭЛЕМЕНТ БД
        UploadFile uploadFile = uploadRepository.findUploadFileByUserAndFileName(user, fileName);

        //СОЗАДЕМ StringBuilder ДЛЯ ФОРМИРОВАНИЯ ПУТИ ХРАНЕНИЯ ФАЙЛА
        StringBuilder newLink = new StringBuilder().append(uploadPath);
        newLink.append("\\")
                .append(userName)
                .append("\\")
                .append(newFileName);

        //ПЕРЕИМЕНОВЫВАЕМ ФАЙЛ НА ДИСКЕ
        fileRepository.editFileName(uploadFile.getLink(), newLink.toString(), userName, fileName);

        //ИЗМЕНЕНИЕ ЭКЗЕМПЛЧРА БД
        uploadFile.setFileName(newFileName);
        uploadFile.setLink(newLink.toString());

        uploadRepository.save(uploadFile);
    }

    public List<FileSize> getAllFiles(String requestToken, Integer limit) throws ErrorGettingFileListException {

        //ПОЛУЧЕНИЕ ИМЯ ПО ТОКЕНУ
        User user = userRepository.findByUserName(jwtProvider.getLoginFromToken(requestToken.substring(7)));

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
}
