package edu.lospedros.estacionamiento.account;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ClienteTest {

    @Test
    void permiteRegistrarHastaTresVehiculos() {
        Client cliente = new Client("c-1", "cliente@test.local", "hash");

        assertTrue(cliente.addVehicle("CAR", "Toyota", "Corolla", "Rojo", "ABC1234"));
        assertTrue(cliente.addVehicle("MOTORCYCLE", "Honda", "CBR", "Negro", "MOT1234"));
        assertTrue(cliente.addVehicle("TRUCK", "Ford", "Ranger", "Blanco", "TRK1234"));
        assertEquals(3, cliente.getVehicles().size());
    }

    @Test
    void bloqueaCuartoVehiculo() {
        Client cliente = new Client("c-2", "cliente2@test.local", "hash");

        assertTrue(cliente.addVehicle("CAR", "Toyota", "Corolla", "Rojo", "AAA1111"));
        assertTrue(cliente.addVehicle("MOTORCYCLE", "Honda", "CBR", "Negro", "BBB2222"));
        assertTrue(cliente.addVehicle("TRUCK", "Ford", "Ranger", "Blanco", "CCC3333"));
        assertFalse(cliente.addVehicle("CAR", "Nissan", "Versa", "Gris", "DDD4444"));
        assertEquals(3, cliente.getVehicles().size());
    }

    @Test
    void bloqueaPlacaDuplicada() {
        Client cliente = new Client("c-3", "cliente3@test.local", "hash");

        assertTrue(cliente.addVehicle("CAR", "Toyota", "Corolla", "Rojo", "XYZ1234"));
        assertFalse(cliente.addVehicle("TRUCK", "Ford", "Ranger", "Negro", "XYZ1234"));
        assertEquals(1, cliente.getVehicles().size());
    }
}
