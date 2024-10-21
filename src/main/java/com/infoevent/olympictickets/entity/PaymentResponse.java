package com.infoevent.olympictickets.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentResponse {
    private final boolean success;
    private final String message;

    public PaymentResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}

