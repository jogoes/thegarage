package com.github.jogoes.thegarage;

/**
 * Container used to identify a specific location in com github.
 */
public class LocationInfo {

    private int parkingLevel;
    private int lotNumber;

    public int getParkingLevel() { return parkingLevel; }
    public int getLotNumber() { return lotNumber; }

    LocationInfo(int parkingLevel, int lotNumber) {
        assert(parkingLevel >= 0);
        assert(lotNumber >= 0);

        this.parkingLevel = parkingLevel;
        this.lotNumber = lotNumber;
    }

    @Override
    public String toString() {
        return "LocationInfo{" +
                "parkingLevel=" + parkingLevel +
                ", lotNumber=" + lotNumber +
                '}';
    }
}


