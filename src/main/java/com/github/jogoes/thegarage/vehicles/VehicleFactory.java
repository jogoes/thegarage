package com.github.jogoes.thegarage.vehicles;

/**
 * Factory used to allow com creation of a vehicle in a central place.
 */
public class VehicleFactory {

    private VehicleFactory() {}

    public static Motorbike createMotorbike(String id) {
        return new Motorbike(id);
    }

    public static Car createCar(String id) {
        return new Car(id);
    }
}
