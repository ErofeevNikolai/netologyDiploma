package ru.netology.netologydiploma.security.exception;

import java.io.IOException;

public class ErrorDeleteFileException extends IOException {
    public ErrorDeleteFileException(String msg){
        super(msg);
    }
}
