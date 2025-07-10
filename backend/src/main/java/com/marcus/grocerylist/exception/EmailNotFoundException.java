package com.marcus.grocerylist.exception;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
public class EmailNotFoundException extends UsernameNotFoundException {

    public EmailNotFoundException(String msg) {
        super(msg);
    }

    public EmailNotFoundException(String msg, Throwable cause) {
        super(msg, cause);
    }
}