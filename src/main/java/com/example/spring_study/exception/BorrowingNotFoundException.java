package com.example.spring_study.exception;

public class BorrowingNotFoundException extends RuntimeException {
    public BorrowingNotFoundException(int id) {
        super("Borrowing with ID " + id + " not found");
    }
}
