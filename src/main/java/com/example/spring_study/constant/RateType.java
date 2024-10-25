package com.example.spring_study.constant;

public enum RateType {
    NEW(1), LIKENEW(0.8), SECONDHAND(0.5), ERROR(0.1);

    private final double value;

    RateType(double value) {
        this.value = value;
    }

    public double getValue() {
        return this.value;
    }
}
