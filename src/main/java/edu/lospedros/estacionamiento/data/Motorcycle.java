package edu.lospedros.estacionamiento.data;

public class Motorcycle extends Vehicle {
    public Motorcycle(String placa) {
        super(placa, "MOTO");
    }

    @Override
    public double getTarifaBase() {
        return 15.0;
    }
}
