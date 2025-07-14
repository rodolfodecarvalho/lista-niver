package com.rodolfo.listaniver.exception;

import java.io.Serial;

public class RecordNotFoundException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public RecordNotFoundException(String resourceType, Long resourceId) {
        super(String.format("%s not found with id: %s", resourceType, resourceId));
    }
}