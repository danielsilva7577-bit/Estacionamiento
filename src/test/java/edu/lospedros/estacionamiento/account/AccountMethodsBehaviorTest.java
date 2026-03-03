package edu.lospedros.estacionamiento.account;

import edu.lospedros.estacionamiento.data.StandardVehicle;
import edu.lospedros.estacionamiento.payment.CashPayment;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AccountMethodsBehaviorTest {

    @Test
    void clientePuedeRegistrarVehiculoDesdeMetodoUml() {
        Client cliente = new Client("c1", "cliente@test.com", "hash");
        cliente.registrarVehiculo(new StandardVehicle("ABC1234"));
        assertEquals(1, cliente.getVehicles().size());
        assertEquals("ABC1234", cliente.getVehiclePlate());
    }

    @Test
    void clientePuedeConfigurarPagoPreferente() {
        Client cliente = new Client("c1", "cliente@test.com", "hash");
        cliente.configurarPago(new CashPayment());
        assertEquals("EFECTIVO", cliente.getPreferredPaymentMethod());
    }

    @Test
    void guestGuardaDatosTemporalesDePago() {
        Guest guest = new Guest("g1", "guest@local");
        guest.proporcionarDatosTemporales(new CashPayment());
        assertEquals("EFECTIVO", guest.getTemporalPaymentMethod());
        assertNotNull(guest.getTemporalDataCapturedAt());
    }

    @Test
    void adminConfiguraTarifasSinRomperEstado() {
        Administrator admin = new Administrator("a1", "admin@test.com", "hash");
        admin.gestionarUsuarios();
        admin.setUltimoReporteIngresos(1250.75);
        admin.generarReporteIngresos();
        admin.configurarTarifas(List.of(15.0, 20.0, 35.0), 3);

        assertEquals(1, admin.getOperacionesUsuarios());
        assertEquals(1250.75, admin.getUltimoReporteIngresos(), 0.001);
        assertEquals(3, admin.getUltimaConfigNp());
        assertEquals(3, admin.getTarifasConfiguradas().size());
    }
}
