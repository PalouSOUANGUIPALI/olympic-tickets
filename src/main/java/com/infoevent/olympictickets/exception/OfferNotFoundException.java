package com.infoevent.olympictickets.exception;

import java.io.Serial;

public class OfferNotFoundException extends ResourceNotFoundException {

    @Serial
    private static final long serialVersionUID = 1L;

    public OfferNotFoundException(String message) {
        super(message);
    }
}

