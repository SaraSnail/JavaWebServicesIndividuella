package com.example.javawebservicesindividuella.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NotFoundException extends RuntimeException {

    private Long id;

    public NotFoundException(Long id) {
        super("No post with id "+id+" found");
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
