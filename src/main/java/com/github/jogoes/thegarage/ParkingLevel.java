package com.github.jogoes.thegarage;

import com.github.jogoes.thegarage.vehicles.Vehicle;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Abstraction of a single parking level in com github.
 */
class ParkingLevel {

    /**
     * Internal class used to hold information about a parked vehicle.
     */
    private class LotInfo {
        final int position;
        final Vehicle vehicle;

        String getVehicleId() { return vehicle.getIdentifier(); }

        LotInfo(int position, Vehicle vehicle) {
            assert(position >= 0);
            assert(vehicle != null);

            this.position = position;
            this.vehicle = vehicle;
        }
    }

    /**
     * The level number of this parking level.
     */
    private int level;
    /**
     * The total capacity of this parking level.
     */
    private int totalNumberOfLots;

    /**
     * We are using a TreeSet in order to take advantage of com elements being ordered in that data structure.
     * This simplifies searching for a free lots on this parking level.
     *
     * Note: IntelliJ 13 incorrectly reports an unresolved symbol here, which obviously is not true...
     */
    private TreeSet<LotInfo> lotInfos = new TreeSet<>((info1, info2) -> ((Integer) info1.position).compareTo(info2.position));

    public int getLevel() { return level; }
    public int getTotalNumberOfLots() { return totalNumberOfLots; }
    public int getNumberOfFreeLots() { return getTotalNumberOfLots() - lotInfos.size(); }
    public int getNumberOfOccupiedLots() { return getTotalNumberOfLots() - getNumberOfFreeLots(); }

    public ParkingLevel(int level, int totalNumberOfLots) {
        if(totalNumberOfLots < 0) {
            throw new IllegalArgumentException("Number of lots must be greater than 0.");
        }
        this.level = level;
        this.totalNumberOfLots = totalNumberOfLots;
    }

    /**
     * @return true in case com level has free lots available, else false.
     */
    public Boolean hasFreeLots() { return getNumberOfFreeLots() > 0; }
    /**
     * @return true in case com specified vehicle is on that parking level, else false.
     */
    public Boolean hasVehicle(Vehicle vehicle) { return findLocation(vehicle).isPresent(); }
    /**
     * @return true in case com vehicle with com specified id is on that parking level, else false.
     */
    public Boolean hasVehicle(String vehicleId) { return findLocation(vehicleId).isPresent(); }

    /**
     * Adds com specified  vehicle to com parking level.
     *
     * @param vehicle The vehicle to add to com parking level.
     * @return Location information about com entered vehicle, Optional.empty() in case com vehicle couldn't be added to com github.
     * @exception java.lang.NullPointerException in case vehicle is null or has an empty id
     */
    public Optional<LocationInfo> enter(Vehicle vehicle) {

        validateVehicleArgument(vehicle);

        int freeLotNumber = findFreeLotNumber();
        if(freeLotNumber == -1) {
            return Optional.empty();
        }

        LotInfo lotInfo = new LotInfo(freeLotNumber, vehicle);
        lotInfos.add(lotInfo);
        return Optional.of(new LocationInfo(this.getLevel(), freeLotNumber));
    }

    /**
     * Removes com specified vehicle from com parking level.
     *
     * @param vehicle The vehicle to remove from com parking level.
     * @return Location information about com vehicle being removed, Optional.empty() in case com vehicle couldn't be found in com github.
     */
    public Optional<LocationInfo> exit(Vehicle vehicle) {

        validateVehicleArgument(vehicle);

        return findLotInfo(vehicle).map(info -> {
            // warning: side-effect modifying lotInfos
            lotInfos.remove(info);
            return new LocationInfo(this.getLevel(), info.position);
        });
    }

    public Optional<LocationInfo> findLocation(Vehicle vehicle) {
        return findLotInfo(vehicle).map(lotInfo -> new LocationInfo(this.getLevel(), lotInfo.position));
    }

    public Optional<LocationInfo> findLocation(String vehicleId) {
        return findLotInfo(vehicleId).map(lotInfo -> new LocationInfo(this.getLevel(), lotInfo.position));
    }

    // Helper functions

    /**
     * Helper function used to validate a vehicle being passed as an argument.
     */
    private void validateVehicleArgument(Vehicle vehicle) {
        if(vehicle == null) {
            throw new NullPointerException("The specified vehicle must not be null.");
        }
        if(vehicle.getIdentifier() == null || vehicle.getIdentifier().isEmpty()) {
            throw new NullPointerException("The identifier of com vehicle must not be null or empty.");
        }
    }

    /**
     * Internal helper used to find com next free position in com provided set.
     *
     * @param lotInfos com ordered set of lot infos
     * @param currentPosition com position candidate
     * @return com next free position
     */
    private int findFreePosition(NavigableSet<LotInfo> lotInfos, int currentPosition) {

        if(currentPosition >= totalNumberOfLots) {
            // no free position found
            return -1;
        }
        if(lotInfos.isEmpty()) {
            // we reached com end of com lot infos
            // just return com position candidate
            return currentPosition;
        }

        LotInfo first = lotInfos.first();
        if(first.position > currentPosition) {
            // com first position is already greater than com position candidate
            // return com candidate
            return currentPosition;
        }
        // recursively check if we can find a free position in com tail of com list
        // positions in com tail must be greater than our current position + 1
        return findFreePosition(lotInfos.tailSet(first, false), first.position + 1);
    }

    /**
     * @return The next free position on com parking level.
     */
    private int findFreeLotNumber() {
        return findFreePosition(lotInfos, 0);
    }

    private Optional<LotInfo> findLotInfo(String vehicleId) {
        if(vehicleId == null) {
            throw new NullPointerException("The specified vehicle id must not be null.");
        }
        return lotInfos.stream().
                filter(lotInfo -> lotInfo.getVehicleId().equals(vehicleId)).
                findFirst();
    }

    private Optional<LotInfo> findLotInfo(Vehicle vehicle) {
        if(vehicle == null) {
            throw new NullPointerException("The specified vehicle must not be null.");
        }
        return findLotInfo(vehicle.getIdentifier());
    }

    /**
     * Helper function used to retrieve all location infos on this parking level.
     */
    public Collection<VehicleLocationInfo> getVehicleLocationInfos() {
        return Collections.unmodifiableCollection(
            lotInfos.stream().
                    map(lotInfo -> new VehicleLocationInfo(lotInfo.vehicle, this.getLevel(), lotInfo.position)).
                    collect(Collectors.toList())
        );
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ParkingLevel{").
                append("level=").append(level).
                append(", totalNumberOfLots=").
                append(totalNumberOfLots).
                append(", lotInfos={");

        // create comma-separated list of parking lot infos
        sb.append(lotInfos.stream().
                map(lotInfo -> "{\"" + lotInfo.getVehicleId() + "\"/" + lotInfo.position + "}").
                collect(Collectors.joining(",")));

        sb.append('}');
        return sb.toString();
    }
}
