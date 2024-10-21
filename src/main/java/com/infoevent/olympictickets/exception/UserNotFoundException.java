package com.infoevent.olympictickets.exception;

import java.io.Serial;

public class UserNotFoundException extends ResourceNotFoundException {

    @Serial
    private static final long serialVersionUID = 1L;

    public UserNotFoundException(String message) {
        super(message);
    }
}