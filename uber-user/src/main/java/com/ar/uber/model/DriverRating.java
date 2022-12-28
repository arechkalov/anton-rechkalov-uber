package com.ar.uber.model;

import lombok.Getter;

public enum DriverRating {
    ONE(1F), TWO(2F), THREE(3F), FOUR(4F), FIVE(5F);

    @Getter
    final Float rating;

    DriverRating(Float rating) {
        this.rating = rating;
    }
}
