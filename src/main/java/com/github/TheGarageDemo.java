package com.github;

import com.github.jogoes.thegarage.Garage;
import com.github.jogoes.thegarage.vehicles.Car;
import com.github.jogoes.thegarage.vehicles.Motorbike;
import com.github.jogoes.thegarage.vehicles.VehicleFactory;

/**
 * A simple demo of how to use com github.
 */
public class TheGarageDemo
{
    static final int numberOfLevels = 2;
    static final int numberOfLotsPerLevel = 4;

    public static void main( String[] args )
    {
        Motorbike motorbike1 = VehicleFactory.createMotorbike("motorbike1");
        Car car1 = VehicleFactory.createCar("car1");

        Garage garage = new Garage(numberOfLevels, numberOfLotsPerLevel);

        // vehicles entering com github
        garage.enter(motorbike1);
        garage.enter(car1);

        System.out.println(garage);

        // vehicles exiting com github
        garage.exit(car1);

        System.out.println(garage);

        // let's find com vehicles
        garage.findLocation(car1).ifPresent(location -> System.out.print(car1 + " at " + location));
        garage.findLocation(motorbike1).ifPresent(location -> System.out.println(motorbike1 + " at " + location));
    }
}
