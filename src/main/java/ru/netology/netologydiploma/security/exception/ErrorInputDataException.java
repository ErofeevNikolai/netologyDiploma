package ru.netology.netologydiploma.security.exception;

public class ErrorInputDataException extends IllegalArgumentException{
    public ErrorInputDataException(String msg){
        super(msg);
    }
}
