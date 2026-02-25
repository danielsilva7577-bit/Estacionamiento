package edu.lospedros.estacionamiento.process;

import edu.lospedros.estacionamiento.data.Vehicle;
import java.util.ArrayList;
import java.util.List;

public class ParkingManager {
    private List<ParkingSpot> spots;

    public ParkingManager() {
        this.spots = new ArrayList<>();
    }

    public ParkingSpot findSpotForVehicle(Vehicle vehicle) {
        for (ParkingSpot spot : spots) {
            if (spot.isAvailable() && spot.getSize() == vehicle.getSize()) {
                return spot;
            }
        }
        return null;
    }

    public void parkVehicle(Vehicle vehicle) {
        ParkingSpot spot = findSpotForVehicle(vehicle);
        if (spot != null) spot.reserve();
    }

    public void unparkVehicle(Vehicle vehicle) {
        for (ParkingSpot spot : spots) {
            if (!spot.isAvailable() && spot.getSize() == vehicle.getSize()) {
                spot.release();
                break;
            }
        }
    }
}
