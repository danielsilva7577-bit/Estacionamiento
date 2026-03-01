package edu.lospedros.estacionamiento.process;

import edu.lospedros.estacionamiento.account.Account;
import edu.lospedros.estacionamiento.account.Guest;
import edu.lospedros.estacionamiento.data.Ticket;
import edu.lospedros.estacionamiento.data.Vehicle;
import edu.lospedros.estacionamiento.payment.PaymentProcessor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ParkingSystem {
    private static final double GUEST_HOURLY_RATE = 30.0;
    private final List<Account> cuentas;
    private final List<Space> espacios;
    private final SpaceManager gestor;
    private final AtomicInteger folioGenerator = new AtomicInteger(1);

    public ParkingSystem(List<Account> cuentas, List<Space> espacios, SpaceManager gestor) {
        this.cuentas = (cuentas == null) ? new ArrayList<>() : cuentas;
        this.espacios = espacios;
        this.gestor = gestor;
    }

    public static ParkingSystem crearDefault() {
        List<Space> espacios = new ArrayList<>();
        int numero = 1;
        for (int i = 0; i < 20; i++) espacios.add(new Space(numero++, Space.TipoEspacio.MOTO));
        for (int i = 0; i < 30; i++) espacios.add(new Space(numero++, Space.TipoEspacio.GRANDE));
        for (int i = 0; i < 50; i++) espacios.add(new Space(numero++, Space.TipoEspacio.PROMEDIO));
        SpaceManager gestor = new SpaceManager(espacios);
        return new ParkingSystem(new ArrayList<>(), espacios, gestor);
    }

    public Ticket registrarIngreso(Account c, Space e, Vehicle v, PaymentProcessor p) {
        if (c == null || e == null || v == null) return null;
        if (e.isOcupado()) return null;
        if (SpaceManager.tipoParaVehiculo(v) != e.getTipo()) return null;
        if (!gestor.verificarDisponibilidad(v)) return null;

        e.setOcupado(true);
        gestor.contabilizarIngreso(v);

        Ticket t = new Ticket(folioGenerator.getAndIncrement(), LocalDateTime.now());
        t.vincularDatos(v, p);
        t.setEspacioNumero(e.getNumero());
        t.setTarifaPorHoraAplicada(c instanceof Guest ? GUEST_HOURLY_RATE : v.getTarifaBase());
        return t;
    }

    public boolean registrarSalida(Ticket t) {
        if (t == null) return false;
        Space e = gestor.obtenerEspacio(t.getEspacioNumero());
        if (e == null || !e.isOcupado()) return false;

        t.procesarFinalizacion();
        if (!t.cobrar()) return false;

        e.setOcupado(false);
        if (t.getVehiculo() != null) {
            gestor.contabilizarSalida(t.getVehiculo());
        }
        return true;
    }

    public Space getEspacio(int numero) {
        return gestor.obtenerEspacio(numero);
    }

    public SpaceManager getGestor() {
        return gestor;
    }

    public List<Space> getEspacios() {
        return espacios;
    }

    public List<Account> getCuentas() {
        return cuentas;
    }
}
