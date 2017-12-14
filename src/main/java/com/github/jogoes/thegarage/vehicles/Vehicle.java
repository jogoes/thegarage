package com.github.jogoes.thegarage.vehicles;

public abstract class Vehicle {

    private String identifier;

    public String getIdentifier() { return identifier; }

    Vehicle(String identifier) { this.identifier = identifier; }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "{" +
                "identifier='" + identifier + '\'' +
                '}';
    }
}
