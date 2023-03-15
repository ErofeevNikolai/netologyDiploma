package ru.netology.netologydiploma.security.exception;

public class UnauthorizedErrorException extends IllegalArgumentException{
    public UnauthorizedErrorException(String msg){
        super(msg);
    }
}
