package edu.lospedros.estacionamiento.process;

import edu.lospedros.estacionamiento.data.VehicleSize;

public interface ParkingSpot {
    boolean isAvailable();
    void reserve();
    void release();
    VehicleSize getSize();
}