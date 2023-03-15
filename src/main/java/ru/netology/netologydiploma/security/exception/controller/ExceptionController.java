package ru.netology.netologydiploma.security.exception.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.netology.netologydiploma.security.exception.*;


@RestControllerAdvice
public class ExceptionController {

    // /login:PUT 400: Bad credentials (Неверные учетные данные)
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> badCredentialExeption(BadCredentialsException e) {
        return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
    }


    // 400
    // /file:   POST, DELET, GET, PUT
    // /list:   GET
    // Error input data(Ошибка при вводе данных)
    @ExceptionHandler(ErrorInputDataException.class)
    public ResponseEntity<?> errorInputDataException(ErrorInputDataException e) {
        return new ResponseEntity(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    // 401
    // /file:   POST, DELETE, GET,
    // /list:   GET
    // Unauthorized error (Несанкционированная ошибка)
    @ExceptionHandler(UnauthorizedErrorException.class)
    public ResponseEntity<?> unauthorizedErrorException(UnauthorizedErrorException e) {
        return new ResponseEntity(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    // 500
    // /file:   DELL
    // Error delete file (Ошибка удаления файла)
    @ExceptionHandler(ErrorDeleteFileException.class)
    public ResponseEntity<?> errorDeleteFileException(ErrorDeleteFileException e) {
        return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // 500
    // /file:   GET, PUT
    // Error upload file (Ошибка загрузки файла)
    @ExceptionHandler(ErrorUploadFileException.class)
    public ResponseEntity<?> errorUploadFileException(ErrorUploadFileException e){
        return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // 500
    // /list:
    // Error getting file list (Ошибка при получении списка файлов)
    @ExceptionHandler(ErrorGettingFileListException.class)
    public ResponseEntity<?> errorGettingFileListException(ErrorGettingFileListException e){
        return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
