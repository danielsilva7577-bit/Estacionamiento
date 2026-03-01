package edu.lospedros.estacionamiento.data;

public abstract class Vehicle {
    private final String placa;
    private final String marca;

    protected Vehicle(String placa, String marca) {
        this.placa = (placa == null) ? "" : placa.trim();
        this.marca = (marca == null) ? "GENERICA" : marca.trim();
    }

    public String getPlaca() {
        return placa;
    }

    public String getMarca() {
        return marca;
    }

    public abstract double getTarifaBase();

    public String getTipoVehiculo() {
        return getClass().getSimpleName();
    }
}
