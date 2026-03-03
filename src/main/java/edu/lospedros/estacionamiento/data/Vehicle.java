package edu.lospedros.estacionamiento.data;

/**
 * Clase abstracta base para todos los tipos de vehículos.
 * <p>
 * Define las propiedades comunes como placa y marca, y obliga a las subclases
 * a definir su tarifa base.
 * </p>
 */
public abstract class Vehicle {
    private final String placa;
    private final String marca;

    /**
     * Constructor base para un vehículo.
     *
     * @param placa La placa del vehículo.
     * @param marca La marca del vehículo.
     */
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

    /**
     * Obtiene la tarifa base por hora para este tipo de vehículo.
     *
     * @return La tarifa en la moneda local.
     */
    public abstract double getTarifaBase();

    public String getTipoVehiculo() {
        return getClass().getSimpleName();
    }
}
