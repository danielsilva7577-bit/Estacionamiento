package edu.lospedros.estacionamiento.data;

/**
 * Representa un vehículo estándar (automóvil sedán, hatchback, etc.).
 * <p>
 * Tiene una tarifa base intermedia.
 * </p>
 */
public class StandardVehicle extends Vehicle {
    /**
     * Crea un nuevo vehículo estándar.
     *
     * @param placa La placa del vehículo.
     */
    public StandardVehicle(String placa) {
        super(placa, "PROMEDIO");
    }

    @Override
    public double getTarifaBase() {
        return 20.0;
    }
}
