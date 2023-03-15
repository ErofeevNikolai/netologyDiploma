package ru.netology.netologydiploma.security.exception;

import liquibase.pro.packaged.E;

public class ErrorUploadFileException extends Exception {
    public ErrorUploadFileException(String msg){
        super(msg);
    }
}
