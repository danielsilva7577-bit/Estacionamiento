package edu.lospedros.estacionamiento.persistence;

import java.io.IOException;

/**
 * Interfaz para la persistencia del estado del estacionamiento.
 * <p>
 * Permite guardar y cargar la configuración actual de espacios y el historial.
 * </p>
 */
public interface ParkingStateRepository {
    /**
     * Carga el estado persistido desde el almacenamiento.
     *
     * @return El objeto {@link ParkingState} con los datos cargados.
     * @throws IOException Si ocurre un error de lectura.
     */
    ParkingState load() throws IOException;

    /**
     * Guarda el estado actual en el almacenamiento.
     *
     * @param state El estado a persistir.
     * @throws IOException Si ocurre un error de escritura.
     */
    void save(ParkingState state) throws IOException;
}
