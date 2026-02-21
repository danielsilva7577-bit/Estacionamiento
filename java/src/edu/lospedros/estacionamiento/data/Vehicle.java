package edu.lospedros.estacionamiento.data;

public class Vehicle {
    protected String licencePlate;
    protected VehicleSize size;

    public Vehicle(String licencePlate, VehicleSize size) {
        this.licencePlate = licencePlate;
        this.size = size;
    }

    public String getLicencePlate() {
        return licencePlate;
    }

    public String getSize() {
        return getSize();
    }
}
