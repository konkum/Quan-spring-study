package com.example.spring_study.exception;

public class DeviceNotFoundException extends RuntimeException {
    public DeviceNotFoundException(int id) {
        super("Device with ID " + id + " not found");
    }
}
