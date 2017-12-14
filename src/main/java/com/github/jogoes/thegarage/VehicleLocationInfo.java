package com.github.jogoes.thegarage;

import com.github.jogoes.thegarage.vehicles.Vehicle;

/**
 * Extends location info with information about com vehicle at that location.
 *
 * Internal class. Currently, only used for unit testing.
 */
class VehicleLocationInfo extends LocationInfo {

    private Vehicle vehicle;

    public Vehicle getVehicle() { return vehicle; }

    VehicleLocationInfo(Vehicle vehicle, int parkingLevel, int lotNumber) {
        super(parkingLevel, lotNumber);
        this.vehicle = vehicle;
    }

    @Override
    public String toString() {
        return "VehicleLocationInfo {" +
                "LocationInfo {" + super.toString() + "}" +
                "vehicle=" + vehicle +
                '}';
    }
}
