package edu.lospedros.estacionamiento.data;

/**
 * Representa un vehículo de gran tamaño (e.g., camioneta, furgoneta).
 * <p>
 * Tiene una tarifa base más alta debido al espacio que ocupa.
 * </p>
 */
public class LargeVehicle extends Vehicle {
    /**
     * Crea un nuevo vehículo grande.
     *
     * @param placa La placa del vehículo.
     */
    public LargeVehicle(String placa) {
        super(placa, "GRANDE");
    }

    @Override
    public double getTarifaBase() {
        return 35.0;
    }
}
