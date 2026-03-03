package edu.lospedros.estacionamiento.data;

import edu.lospedros.estacionamiento.payment.PaymentProcessor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Representa un ticket de estacionamiento emitido a un vehículo.
 * <p>
 * Contiene información sobre la entrada, salida, vehículo, espacio asignado
 * y el cálculo del costo total.
 * </p>
 */
public class Ticket {
    private int folio;
    private String id;
    private LocalDateTime entrada;
    private LocalDateTime salida;
    private long minutosTotales;
    private double montoACobrar;
    private Vehicle vehiculo;
    private PaymentProcessor procesadorPago;
    private int espacioNumero;
    private double tarifaPorHoraAplicada;

    /**
     * Crea un nuevo ticket.
     *
     * @param folio   Número de folio secuencial.
     * @param entrada Fecha y hora de entrada.
     */
    public Ticket(int folio, LocalDateTime entrada) {
        this.folio = folio;
        this.id = "TKT-" + folio;
        this.entrada = entrada;
    }

    /**
     * Asocia un vehículo y un método de pago (opcional) al ticket.
     *
     * @param v  El vehículo estacionado.
     * @param pi El procesador de pago inicial (puede ser null).
     */
    public void vincularDatos(Vehicle v, PaymentProcessor pi) {
        this.vehiculo = v;
        this.procesadorPago = pi;
    }

    /**
     * Calcula el tiempo total y el monto a cobrar al momento de la salida.
     * <p>
     * Si la salida no se ha registrado, usa la hora actual.
     * El cobro se realiza por horas completas o fracción.
     * </p>
     */
    public void procesarFinalizacion() {
        if (salida == null) {
            salida = LocalDateTime.now();
        }
        Duration duration = Duration.between(entrada, salida);
        minutosTotales = Math.max(1, duration.toMinutes());
        long horasFacturables = Math.max(1, (minutosTotales + 59) / 60);
        double tarifaBase = tarifaPorHoraAplicada > 0
                ? tarifaPorHoraAplicada
                : ((vehiculo == null) ? 0.0 : vehiculo.getTarifaBase());
        montoACobrar = horasFacturables * tarifaBase;
    }

    /**
     * Intenta realizar el cobro utilizando el procesador de pago asignado.
     *
     * @return {@code true} si el cobro fue exitoso.
     */
    public boolean cobrar() {
        if (procesadorPago == null) return false;
        return procesadorPago.ejecutarCobro(montoACobrar);
    }

    /**
     * Calcula la duración actual del estacionamiento.
     *
     * @return La duración entre la entrada y la salida (o ahora).
     */
    public Duration calculateParkingDuration() {
        Objects.requireNonNull(entrada, "entryTime");
        LocalDateTime end = (salida != null) ? salida : LocalDateTime.now();
        return Duration.between(entrada, end);
    }

    public String getId() {
        return (id != null && !id.isBlank()) ? id : "TKT-" + folio;
    }

    public int getFolio() {
        return folio;
    }

    public Vehicle getVehiculo() {
        return vehiculo;
    }

    public LocalDateTime getEntryTime() {
        return entrada;
    }

    public LocalDateTime getExitTime() {
        return salida;
    }

    public void setExitTime(LocalDateTime exitTime) {
        this.salida = exitTime;
    }

    public long getMinutosTotales() {
        return minutosTotales;
    }

    public double getMontoACobrar() {
        return montoACobrar;
    }

    public int getEspacioNumero() {
        return espacioNumero;
    }

    public void setEspacioNumero(int espacioNumero) {
        this.espacioNumero = espacioNumero;
    }

    public void setProcesadorPago(PaymentProcessor procesadorPago) {
        this.procesadorPago = procesadorPago;
    }

    public PaymentProcessor getProcesadorPago() {
        return procesadorPago;
    }

    public double getTarifaPorHoraAplicada() {
        if (tarifaPorHoraAplicada > 0) return tarifaPorHoraAplicada;
        return (vehiculo == null) ? 0.0 : vehiculo.getTarifaBase();
    }

    public void setTarifaPorHoraAplicada(double tarifaPorHoraAplicada) {
        this.tarifaPorHoraAplicada = Math.max(0.0, tarifaPorHoraAplicada);
    }

}
