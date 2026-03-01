package edu.lospedros.estacionamiento.data;

public class LargeVehicle extends Vehicle {
    public LargeVehicle(String placa) {
        super(placa, "GRANDE");
    }

    @Override
    public double getTarifaBase() {
        return 35.0;
    }
}
