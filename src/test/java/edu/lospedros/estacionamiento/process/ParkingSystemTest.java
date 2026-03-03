package edu.lospedros.estacionamiento.process;

import edu.lospedros.estacionamiento.account.Client;
import edu.lospedros.estacionamiento.data.Motorcycle;
import edu.lospedros.estacionamiento.data.Ticket;
import edu.lospedros.estacionamiento.data.StandardVehicle;
import edu.lospedros.estacionamiento.payment.CashPayment;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SistemaEstacionamientoTest {

    @Test
    void ingresoYSalidaActualizanEstadoYContadores() {
        ParkingSystem sistema = ParkingSystem.crearDefault();
        Client cliente = new Client("c1", "cliente@test.com", "hash");

        Space espacioPromedio = sistema.getEspacio(51);
        assertNotNull(espacioPromedio);
        assertFalse(espacioPromedio.isOcupado());

        Ticket ticket = sistema.registrarIngreso(cliente, espacioPromedio, new StandardVehicle("ABC1234"), new CashPayment());
        assertNotNull(ticket);
        assertTrue(espacioPromedio.isOcupado());
        assertEquals(1, sistema.getGestor().ocupadosPromedio);

        boolean salida = sistema.registrarSalida(ticket);
        assertTrue(salida);
        assertFalse(espacioPromedio.isOcupado());
        assertEquals(0, sistema.getGestor().ocupadosPromedio);
        assertTrue(ticket.getMontoACobrar() > 0);
    }

    @Test
    void ingresoEnEspacioIncompatibleFalla() {
        ParkingSystem sistema = ParkingSystem.crearDefault();
        Client cliente = new Client("c1", "cliente@test.com", "hash");

        Space espacioMoto = sistema.getEspacio(1);
        Ticket ticket = sistema.registrarIngreso(cliente, espacioMoto, new StandardVehicle("XYZ9876"), new CashPayment());

        assertNull(ticket);
        assertFalse(espacioMoto.isOcupado());
    }

    @Test
    void ingresoMotoEnEspacioMotoEsValido() {
        ParkingSystem sistema = ParkingSystem.crearDefault();
        Client cliente = new Client("c1", "cliente@test.com", "hash");

        Space espacioMoto = sistema.getEspacio(1);
        Ticket ticket = sistema.registrarIngreso(cliente, espacioMoto, new Motorcycle("MOT1234"), new CashPayment());

        assertNotNull(ticket);
        assertTrue(espacioMoto.isOcupado());
        assertEquals(1, sistema.getGestor().ocupadosMotos);
    }
}
