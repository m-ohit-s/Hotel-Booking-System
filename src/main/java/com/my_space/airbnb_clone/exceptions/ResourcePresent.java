package com.my_space.airbnb_clone.exceptions;

public class ResourcePresent extends RuntimeException {
    public ResourcePresent(String message) {
        super(message);
    }
}
