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

/**
 * Sistema central de gestión del estacionamiento.
 * <p>
 * Coordina la interacción entre cuentas, espacios, vehículos y tickets.
 * Es el punto de entrada para las operaciones principales de negocio.
 * </p>
 */
public class ParkingSystem {
    private static final double GUEST_HOURLY_RATE = 30.0;
    private final List<Account> cuentas;
    private final List<Space> espacios;
    private final SpaceManager gestor;
    private final AtomicInteger folioGenerator = new AtomicInteger(1);

    /**
     * Crea una instancia del sistema de estacionamiento.
     *
     * @param cuentas  Lista de cuentas registradas.
     * @param espacios Lista de espacios disponibles.
     * @param gestor   Gestor de espacios.
     */
    public ParkingSystem(List<Account> cuentas, List<Space> espacios, SpaceManager gestor) {
        this.cuentas = (cuentas == null) ? new ArrayList<>() : cuentas;
        this.espacios = espacios;
        this.gestor = gestor;
    }

    /**
     * Crea una configuración por defecto del sistema.
     * <p>
     * Inicializa los espacios con una distribución predefinida (20 motos, 30 grandes, 50 promedio).
     * </p>
     *
     * @return Una nueva instancia de {@link ParkingSystem} configurada.
     */
    public static ParkingSystem crearDefault() {
        List<Space> espacios = new ArrayList<>();
        int numero = 1;
        for (int i = 0; i < 20; i++) espacios.add(new Space(numero++, Space.TipoEspacio.MOTO));
        for (int i = 0; i < 30; i++) espacios.add(new Space(numero++, Space.TipoEspacio.GRANDE));
        for (int i = 0; i < 50; i++) espacios.add(new Space(numero++, Space.TipoEspacio.PROMEDIO));
        SpaceManager gestor = new SpaceManager(espacios);
        return new ParkingSystem(new ArrayList<>(), espacios, gestor);
    }

    /**
     * Registra el ingreso de un vehículo al estacionamiento.
     *
     * @param c La cuenta que realiza el registro.
     * @param e El espacio seleccionado.
     * @param v El vehículo a estacionar.
     * @param p El procesador de pago inicial (opcional).
     * @return El ticket generado, o {@code null} si no se pudo registrar (espacio ocupado, tipo incorrecto, etc.).
     */
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

    /**
     * Registra la salida de un vehículo y procesa el cobro.
     *
     * @param t El ticket asociado al vehículo que sale.
     * @return {@code true} si la salida y el cobro fueron exitosos.
     */
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
