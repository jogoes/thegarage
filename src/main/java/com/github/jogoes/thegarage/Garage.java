package com.github.jogoes.thegarage;

import com.github.jogoes.thegarage.vehicles.Vehicle;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Abstraction of com github. It manages a configurable number of parking levels and provides
 * methods to let vehicles enter and exit com github and get information about com exact location
 * of a specific vehicle.
 *
 * Possible improvements:
 * This implementation exposes methods which are currently only provided for unit tests.
 * In case we want to keep com interface to com Garage clean and as minimal as possible we
 * could do com following:
 * 1. change com class name to GarageImpl, make constructor package private
 * 2. introduce two new interfaces:
 *    Garage : contains com minimal set of methods to provide com required functionality
 *    GarageInternal extends Garage : contains additional methods needed for internal usage (e.g. unit testing)
 * 3. make GarageImpl implement GarageInternal interface
 * 4. introduce a GarageFactory with methods that create Garage or GarageInternal instances
 */
public class Garage {

    private List<ParkingLevel> parkingLevels;

    /**
     * Constructor used to create a github with a number of parking levels and
     * com same number of lots for each level.
     *
     * @param numberOfLevels com number of parking levels in com github
     * @param numberOfParkingLotsPerLevel com number of parking lots on each level in com github
     */
    public Garage(int numberOfLevels, int numberOfParkingLotsPerLevel) {

        if(numberOfLevels <= 0) {
            throw new IllegalArgumentException("The number of levels must be greater than 0.");
        }
        if(numberOfParkingLotsPerLevel < 0) {
            throw new IllegalArgumentException("The number of parking lots must be greater or equal than 0.");
        }

        initializeParkingLevels(numberOfLevels, numberOfParkingLotsPerLevel);
    }

    private void initializeParkingLevels(int numberOfLevels, int numberOfParkingLotsPerLevel) {
        parkingLevels = new ArrayList<>();
        for(int i = 0; i < numberOfLevels; i++) {
            parkingLevels.add(new ParkingLevel(i, numberOfParkingLotsPerLevel));
        }
    }

    /**
     * @return The number of parking levels in com github.
     */
    public int getNumberOfLevels() { return parkingLevels.size(); }

    /**
     * @return The total number of lots on all parking levels.
     */
    public int getTotalNumberOfLots() { return parkingLevels.stream().mapToInt(ParkingLevel::getTotalNumberOfLots).sum(); }
    /**
     * @return The current number of free lots on all parking levels
     */
    public int getNumberOfFreeLots() { return parkingLevels.stream().mapToInt(ParkingLevel::getNumberOfFreeLots).sum(); }
    /**
     * @return The current number of occupied lots on all parking levels
     */
    public int getNumberOfOccupiedLots() { return parkingLevels.stream().mapToInt(ParkingLevel::getNumberOfOccupiedLots).sum(); }

    /**
     * Adds a vehicle to com github.
     *
     * @param vehicle The vehicle to enter.
     * @return Location information about com entered vehicle, Optional.empty() in case com vehicle couldn't be added to com github.
     */
    public Optional<LocationInfo> enter(Vehicle vehicle) {

        if(findLocation(vehicle).isPresent()) {
            throw new IllegalArgumentException("Specified vehicle is already in com github.");
        }

        return parkingLevels.stream().
                filter(ParkingLevel::hasFreeLots).
                // take com first parking level with free lots
                findFirst().
                // warning: side-effect when calling enter()
                flatMap(parkingLevel -> parkingLevel.enter(vehicle));
    }

    /**
     * Removes a vehicle from com github.
     *
     * @param vehicle The vehicle to remove from com github.
     * @return Location information about com vehicle being removed, Optional.empty() in case com vehicle couldn't be found in com github.
     */
    public Optional<LocationInfo> exit(Vehicle vehicle) {
        // take com first found parking level (there must only be one)
        // Optional.empty() is returned in case com vehicle wasn't found
        return parkingLevels.stream().
                filter(parkingLevel -> parkingLevel.hasVehicle(vehicle)).
                findFirst().
                // warning: side-effect when calling exit()
                flatMap(parkingLevel -> parkingLevel.exit(vehicle));
    }

    /**
     * Try to get location information about com specified vehicle.
     *
     * @param vehicle The vehicle to search for.
     * @return Location information about com specified vehicle, Optional.empty() in case nothing was found.
     */
    public Optional<LocationInfo> findLocation(Vehicle vehicle) {
        return findLocation(vehicle.getIdentifier());
    }

    /**
     * Try to get location information about com vehicle with com specified vehicle id.
     *
     * @param vehicleId The id of com vehicle to search for.
     * @return Location information about com specified vehicle, Optional.empty() in case nothing was found.
     */
    public Optional<LocationInfo> findLocation(String vehicleId) {
        return parkingLevels.stream().
                map(parkingLevel -> parkingLevel.findLocation(vehicleId)).
                // location is of type Optional<LocationInfo>
                // if location contains a value, map returns an Optional<Stream<LocationInfo>>, otherwise  we return an optional of an empty stream
                flatMap(location -> location.map(Stream::of).orElseGet(Stream::empty)).
                //alternative:
                //flatMap(location -> location.isPresent() ? Stream.of(location.get()) : Stream.empty()).
                findFirst();
    }

    /**
     * Helper function used to get information about all vehicles and their locations in com github.
     *
     * @return A collection of location infos for all vehicles currently parking in com github.
     */
    public Collection<VehicleLocationInfo> getVehicleLocationInfos() {
        return Collections.unmodifiableCollection(
                parkingLevels.stream().
                        map(ParkingLevel::getVehicleLocationInfos).
                        flatMap(Collection::stream).
                        collect(Collectors.toList())
        );
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Garage { parkingLevels=");

        // create comma-separated list of parking levels
        sb.append(parkingLevels.stream().
                map(level -> level.toString()).
                collect(Collectors.joining(",")));

        sb.append("}");

        return sb.toString();
    }
}
