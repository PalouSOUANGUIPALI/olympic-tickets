package com.infoevent.olympictickets.exception;

import java.io.Serial;

public class TicketNotFoundException extends ResourceNotFoundException {

    @Serial
    private static final long serialVersionUID = 1L;

    public TicketNotFoundException(String message) {
        super(message);
    }
}

