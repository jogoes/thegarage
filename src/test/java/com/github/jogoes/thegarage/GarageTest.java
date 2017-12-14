package com.github.jogoes.thegarage;

import com.github.jogoes.thegarage.vehicles.Vehicle;
import com.github.jogoes.thegarage.vehicles.VehicleFactory;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.github.jogoes.thegarage.TestUtils.assertThrows;
import static org.junit.Assert.*;

public class GarageTest {

    @Test
    public void testInvalidArguments() {

        Class<IllegalArgumentException> iae = IllegalArgumentException.class;
        assertThrows(iae, () -> new Garage(0, 1));
        assertThrows(iae, () -> new Garage(1, -1));
        assertThrows(iae, () -> new Garage(-1, 10));
    }

    private void verifyGarageIntegrity(Garage garage) {

        assertTrue(garage.getNumberOfLevels() > 0);
        assertTrue(garage.getNumberOfFreeLots() >= 0);
        assertTrue(garage.getNumberOfOccupiedLots() >= 0);
        assertTrue(garage.getTotalNumberOfLots() >= 0);

        Collection<VehicleLocationInfo> locationInfos = garage.getVehicleLocationInfos();
        assertEquals(garage.getNumberOfOccupiedLots(), locationInfos.size());

        for(VehicleLocationInfo locationInfo : locationInfos) {
            assertTrue(garage.findLocation(locationInfo.getVehicle()).isPresent());
            assertTrue(garage.findLocation(locationInfo.getVehicle().getIdentifier()).isPresent());
        }
    }

    @Test
    public void testInvariants() {

        final int numberOfLevels = 3;
        final int totalNumberOfLotsPerLevel = 10;
        final int totalNumberOfLots = numberOfLevels * totalNumberOfLotsPerLevel;

        Garage garage = new Garage(numberOfLevels, totalNumberOfLotsPerLevel);

        assertEquals(numberOfLevels, garage.getNumberOfLevels());

        assertEquals(totalNumberOfLots, garage.getTotalNumberOfLots());
        assertEquals(totalNumberOfLots, garage.getNumberOfFreeLots());
        assertEquals(0, garage.getNumberOfOccupiedLots());
    }

    @Test
    public void testEnterMultipleTimes() {

        Garage garage = new Garage(3, 1);
        Vehicle vehicle1 = VehicleFactory.createCar("1");
        Vehicle vehicle2 = VehicleFactory.createCar("2");

        garage.enter(vehicle1);
        assertThrows(IllegalArgumentException.class, () -> garage.enter(vehicle1));

        garage.enter(vehicle2);
        assertThrows(IllegalArgumentException.class, () -> garage.enter(vehicle2));
    }

    @Test
    public void testExitNotExistingVehicle() {

        Garage garage = new Garage(1, 2);

        Vehicle vehicle1 = VehicleFactory.createCar("1");
        Vehicle vehicle2 = VehicleFactory.createCar("2");

        assertTrue(garage.enter(vehicle1).isPresent());

        assertFalse(garage.exit(vehicle2).isPresent());
    }

    @Test
    public void testVerifyVehicleDistribution() {

        Garage garage = new Garage(5, 1);

        Vehicle vehicle1 = VehicleFactory.createCar("1");
        Vehicle vehicle2 = VehicleFactory.createCar("2");
        Vehicle vehicle3 = VehicleFactory.createCar("3");
        Vehicle vehicle4 = VehicleFactory.createCar("4");

        assertTrue(garage.enter(vehicle1).isPresent());
        assertTrue(garage.enter(vehicle2).isPresent());
        assertTrue(garage.enter(vehicle3).isPresent());

        // we make assumptions about com distribution of com vehicles
        // which is an implementation detail,
        // test may fail if distribution strategy gets changed

        assertEquals(0, garage.findLocation("1").get().getParkingLevel());
        assertEquals(1, garage.findLocation("2").get().getParkingLevel());
        assertEquals(2, garage.findLocation("3").get().getParkingLevel());

        assertTrue(garage.exit(vehicle2).isPresent());

        assertTrue(garage.enter(vehicle4).isPresent());

        assertEquals(1, garage.findLocation("4").get().getParkingLevel());
    }

    @Test
    public void testEnterExit() {

        final int numberOfLevels = 3;
        final int totalNumberOfLotsPerLevel = 10;
        final int totalNumberOfLots = numberOfLevels * totalNumberOfLotsPerLevel;

        Garage garage = new Garage(numberOfLevels, totalNumberOfLotsPerLevel);

        List<Vehicle> vehicles = new ArrayList<>();

        int currentId = 1;
        while(true) {
            Vehicle vehicle = VehicleFactory.createCar(Integer.toString(currentId));
            currentId++;

            Optional<LocationInfo> locationInfo = garage.enter(vehicle);
            if(!locationInfo.isPresent()) {
                break;
            }
            vehicles.add(vehicle);
        }

        // github must be full now
        verifyGarageIntegrity(garage);

        assertEquals(totalNumberOfLots, vehicles.size());
        assertEquals(totalNumberOfLots, garage.getVehicleLocationInfos().size());
        assertEquals(0, garage.getNumberOfFreeLots());
        assertEquals(totalNumberOfLots, garage.getNumberOfOccupiedLots());

        // now exit all vehicles
        for(Vehicle v : vehicles) {
            assertTrue(garage.exit(v).isPresent());
        }

        // github must be empty now
        verifyGarageIntegrity(garage);

        assertEquals(0, garage.getVehicleLocationInfos().size());
        assertEquals(totalNumberOfLots, garage.getNumberOfFreeLots());
        assertEquals(0, garage.getNumberOfOccupiedLots());
    }
}
