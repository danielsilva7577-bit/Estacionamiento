package edu.lospedros.estacionamiento.data;

/**
 * Representa una motocicleta.
 * <p>
 * Tiene la tarifa base más baja del sistema.
 * </p>
 */
public class Motorcycle extends Vehicle {
    /**
     * Crea una nueva motocicleta.
     *
     * @param placa La placa de la moto.
     */
    public Motorcycle(String placa) {
        super(placa, "MOTO");
    }

    @Override
    public double getTarifaBase() {
        return 15.0;
    }
}
