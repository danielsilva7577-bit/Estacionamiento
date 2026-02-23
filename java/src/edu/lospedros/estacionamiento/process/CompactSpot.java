package edu.lospedros.estacionamiento.process;

import edu.lospedros.estacionamiento.data.VehicleSize;

public class CompactSpot implements ParkingSpot {
    private boolean available = true;

    @Override
    public boolean isAvailable() { return available; }

    @Override
    public void reserve() { this.available = false; }

    @Override
    public void release() { this.available = true; }

    @Override
    public VehicleSize getSize() { return VehicleSize.SMALL; }
}