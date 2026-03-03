package edu.lospedros.estacionamiento.process;

import edu.lospedros.estacionamiento.account.Client;
import edu.lospedros.estacionamiento.account.Guest;
import edu.lospedros.estacionamiento.data.Ticket;
import edu.lospedros.estacionamiento.data.StandardVehicle;
import edu.lospedros.estacionamiento.payment.CashPayment;
import edu.lospedros.estacionamiento.process.Space;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TarifaSimulationTest {

    @Test
    void simuladorHorasClienteCarroCalculaTarifaPorHora() {
        ParkingSystem sistema = ParkingSystem.crearDefault();
        Client cliente = new Client("c1", "cliente@test.com", "hash");
        Space espacio = sistema.getEspacio(51);

        Ticket ticket = sistema.registrarIngreso(cliente, espacio, new StandardVehicle("ABC1234"), new CashPayment());
        assertNotNull(ticket);

        LocalDateTime entry = ticket.getEntryTime();
        ticket.setExitTime(entry.plusHours(3).plusMinutes(5)); // 4 horas facturables

        assertTrue(sistema.registrarSalida(ticket));
        assertEquals(80.0, ticket.getMontoACobrar(), 0.001); // 4 * 20
    }

    @Test
    void simuladorHorasGuestUsaTarifaFijaDeTreinta() {
        ParkingSystem sistema = ParkingSystem.crearDefault();
        Guest guest = new Guest("g1", "guest@local");
        Space espacio = sistema.getEspacio(51);

        Ticket ticket = sistema.registrarIngreso(guest, espacio, new StandardVehicle("XYZ1234"), new CashPayment());
        assertNotNull(ticket);

        LocalDateTime entry = ticket.getEntryTime();
        ticket.setExitTime(entry.plusHours(2)); // 2 horas exactas

        assertTrue(sistema.registrarSalida(ticket));
        assertEquals(60.0, ticket.getMontoACobrar(), 0.001); // 2 * 30
    }
}
