package edu.lospedros.estacionamiento.data;

public class StandardVehicle extends Vehicle {
    public StandardVehicle(String placa) {
        super(placa, "PROMEDIO");
    }

    @Override
    public double getTarifaBase() {
        return 20.0;
    }
}
