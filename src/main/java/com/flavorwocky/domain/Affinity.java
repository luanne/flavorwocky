package com.flavorwocky.domain;

public enum Affinity {
    TRIED_TESTED(0.6),
    EXCELLENT(0.45),
    GOOD(0.35);


    private double weight;

    Affinity(double weight) {
        this.weight = weight;
    }

    public double getWeight() {
        return weight;
    }
}
