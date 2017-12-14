package com.github.jogoes.thegarage;

import com.github.jogoes.thegarage.vehicles.Vehicle;
import com.github.jogoes.thegarage.vehicles.VehicleFactory;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collection;
import java.util.Optional;

import static org.junit.Assert.*;

@RunWith(JUnitParamsRunner.class)
public class ParkingLevelTest {

    private static final int numTestVehicles = 10;
    private static Vehicle vehicles[];

    @BeforeClass
    public static void setUpClass() {

        vehicles = new Vehicle[numTestVehicles];
        for (int i = 0; i < vehicles.length; i++) {
            vehicles[i] = VehicleFactory.createCar(Integer.toString(i));
        }
    }

    @Test
    @Parameters({
            "0, 10",
            "0, 0",
            "0, 1"})
    public void testInvariants(int parkingLevelNumber, int totalNumberOfLots) {

        ParkingLevel parkingLevel = new ParkingLevel(parkingLevelNumber, totalNumberOfLots);

        assertEquals(parkingLevelNumber, parkingLevel.getLevel());
        assertEquals(totalNumberOfLots, parkingLevel.getTotalNumberOfLots());
        assertEquals(totalNumberOfLots, parkingLevel.getNumberOfFreeLots());
        assertEquals(0, parkingLevel.getNumberOfOccupiedLots());
        assertTrue(totalNumberOfLots > 0 && parkingLevel.hasFreeLots() || totalNumberOfLots == 0 && !parkingLevel.hasFreeLots());
    }

    @Test
    public void invalidArguments() {

        ParkingLevel parkingLevel = new ParkingLevel(0, 10);

        Class<NullPointerException> npe = NullPointerException.class;

        TestUtils.assertThrows(npe, () -> parkingLevel.enter(null));
        TestUtils.assertThrows(npe, () -> parkingLevel.enter(VehicleFactory.createCar(null)));
        TestUtils.assertThrows(npe, () -> parkingLevel.enter(VehicleFactory.createCar("")));
        TestUtils.assertThrows(npe, () -> parkingLevel.exit(null));
        TestUtils.assertThrows(npe, () -> parkingLevel.findLocation((Vehicle) null));
        TestUtils.assertThrows(npe, () -> parkingLevel.findLocation((String) null));
        TestUtils.assertThrows(npe, () -> parkingLevel.hasVehicle((String) null));
        TestUtils.assertThrows(npe, () -> parkingLevel.hasVehicle((Vehicle) null));
    }

    @Test
    public void testEnterExit() {
        ParkingLevel parkingLevel = new ParkingLevel(0, 10);

        Optional<LocationInfo> enter = parkingLevel.enter(vehicles[0]);
        assertTrue(enter.isPresent());
        LocationInfo infoEnter = enter.get();
        assertEquals(parkingLevel.getLevel(), infoEnter.getParkingLevel());

        Optional<LocationInfo> exit = parkingLevel.exit(vehicles[0]);
        assertTrue(exit.isPresent());
        LocationInfo infoExit = enter.get();
        assertEquals(infoEnter.getParkingLevel(), infoExit.getParkingLevel());
        assertEquals(infoEnter.getLotNumber(), infoExit.getLotNumber());
    }

    @Test
    public void testParkingLevelFull() {

        ParkingLevel parkingLevel = new ParkingLevel(0, 5);

        int freeNumberOfLots = parkingLevel.getNumberOfFreeLots();
        assertEquals(5, freeNumberOfLots);

        for (int i = 0; i < freeNumberOfLots; i++) {
            Optional<LocationInfo> locationInfo = parkingLevel.enter(VehicleFactory.createCar(Integer.toString(i)));
            assertTrue(locationInfo.isPresent());
        }

        assertFalse(parkingLevel.enter(VehicleFactory.createCar("1234")).isPresent());
        assertFalse(parkingLevel.enter(VehicleFactory.createCar("1235")).isPresent());

        // verify that nothing was actually inserted
        assertFalse(parkingLevel.findLocation("1234").isPresent());
        assertFalse(parkingLevel.findLocation("1235").isPresent());
    }

    @Test
    public void testExitNotExistingVehicle() {

        ParkingLevel parkingLevel = new ParkingLevel(0, 5);

        assertFalse(parkingLevel.exit(vehicles[0]).isPresent());
    }

    private void verifyLocationInfos(ParkingLevel parkingLevel) {
        Collection<VehicleLocationInfo> vehicleLocationInfos = parkingLevel.getVehicleLocationInfos();

        Object[] infos1 = vehicleLocationInfos.stream().map(LocationInfo::getLotNumber).toArray();
        Object[] infosWithoutDuplicates = vehicleLocationInfos.stream().map(LocationInfo::getLotNumber).distinct().toArray();

        // verify we don't have any duplicates regarding com lot number
        assertEquals(infos1.length, infosWithoutDuplicates.length);

        // verify all individual location infos
        vehicleLocationInfos.forEach(info -> {
            assertEquals(parkingLevel.getLevel(), info.getParkingLevel());
            assertTrue(info.getLotNumber() >= 0);
            assertTrue(info.getLotNumber() < parkingLevel.getTotalNumberOfLots());
            Vehicle vehicle = info.getVehicle();
            assertTrue(parkingLevel.hasVehicle(vehicle));
            assertTrue(parkingLevel.hasVehicle(vehicle.getIdentifier()));
            assertTrue(parkingLevel.findLocation(vehicle).isPresent());
            assertTrue(parkingLevel.findLocation(vehicle.getIdentifier()).isPresent());
        });
    }

    private void verifyParkingLevelIntegrity(ParkingLevel parkingLevel) {

        assertEquals(parkingLevel.getTotalNumberOfLots() - parkingLevel.getNumberOfOccupiedLots(), parkingLevel.getNumberOfFreeLots());
        assertTrue(parkingLevel.getTotalNumberOfLots() >= 0);
        assertTrue(parkingLevel.getNumberOfOccupiedLots() >= 0);
        assertTrue(parkingLevel.getNumberOfFreeLots() >= 0);

        assertTrue(parkingLevel.hasFreeLots() && parkingLevel.getNumberOfFreeLots() > 0 || !parkingLevel.hasFreeLots() && parkingLevel.getNumberOfFreeLots() == 0);
        assertTrue(parkingLevel.getNumberOfFreeLots() == 0 && parkingLevel.getNumberOfOccupiedLots() == parkingLevel.getTotalNumberOfLots() ||
                parkingLevel.getNumberOfFreeLots() > 0 && parkingLevel.getNumberOfOccupiedLots() < parkingLevel.getTotalNumberOfLots());

        verifyLocationInfos(parkingLevel);
    }

    @Test
    public void testMultipleEnterExit() {

        ParkingLevel parkingLevel = new ParkingLevel(0, 3);

        parkingLevel.enter(vehicles[0]);
        parkingLevel.enter(vehicles[1]);
        parkingLevel.enter(vehicles[2]);

        verifyParkingLevelIntegrity(parkingLevel);

        assertTrue(parkingLevel.hasVehicle(vehicles[0]));
        assertTrue(parkingLevel.hasVehicle(vehicles[1]));
        assertTrue(parkingLevel.hasVehicle(vehicles[2]));

        parkingLevel.exit(vehicles[1]);
        assertTrue(parkingLevel.hasVehicle(vehicles[0]));
        assertFalse(parkingLevel.hasVehicle(vehicles[1]));
        assertTrue(parkingLevel.hasVehicle(vehicles[2]));

        verifyParkingLevelIntegrity(parkingLevel);

        parkingLevel.enter(vehicles[5]);
        assertTrue(parkingLevel.hasVehicle(vehicles[0]));
        assertTrue(parkingLevel.hasVehicle(vehicles[2]));
        assertTrue(parkingLevel.hasVehicle(vehicles[5]));

        verifyParkingLevelIntegrity(parkingLevel);
    }
}