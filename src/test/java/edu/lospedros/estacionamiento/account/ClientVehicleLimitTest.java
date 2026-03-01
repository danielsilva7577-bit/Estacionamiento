package edu.lospedros.estacionamiento.account;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ClienteVehicleLimitTest {

    @Test
    void clienteNoPuedeRegistrarMasDeTresVehiculos() {
        Client cliente = new Client("c1", "cliente@test.com", "hash");

        assertTrue(cliente.addVehicle("CAR", "Toyota", "Corolla", "Rojo", "ABC1234"));
        assertTrue(cliente.addVehicle("MOTORCYCLE", "Honda", "CBR", "Negro", "MOT1234"));
        assertTrue(cliente.addVehicle("TRUCK", "Nissan", "Frontier", "Blanco", "TRK1234"));
        assertFalse(cliente.addVehicle("CAR", "Mazda", "3", "Azul", "MAZ1234"));
        assertEquals(3, cliente.getVehicles().size());
    }

    @Test
    void clienteNoAceptaPlacasConCaracteresEspeciales() {
        Client cliente = new Client("c1", "cliente@test.com", "hash");
        assertFalse(cliente.addVehicle("CAR", "Toyota", "Corolla", "Rojo", "ABC-234"));
        assertFalse(cliente.addVehicle("CAR", "Toyota", "Corolla", "Rojo", "ABC12@4"));
        assertEquals(0, cliente.getVehicles().size());
    }
}
