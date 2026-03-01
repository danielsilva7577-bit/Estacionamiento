package edu.lospedros.estacionamiento.persistence;

import java.io.IOException;

public interface ParkingStateRepository {
    ParkingState load() throws IOException;
    void save(ParkingState state) throws IOException;
}
