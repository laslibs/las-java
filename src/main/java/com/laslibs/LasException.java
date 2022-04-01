package com.laslibs;

public class LasException extends RuntimeException {
    public LasException(String message){
        super(message);
    }
    public LasException(String message, Throwable err){
        super(message, err);
    }
}
