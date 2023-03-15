package ru.netology.netologydiploma.security.exception;

public class BadCredentialsException extends IllegalArgumentException{
    public BadCredentialsException(String msg){
        super(msg);
    }
}
