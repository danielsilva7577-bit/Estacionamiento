package edu.lospedros.estacionamiento.data;

import edu.lospedros.estacionamiento.payment.PaymentProcessor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

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

    // Constructor alineado al diagrama.
    public Ticket(int folio, LocalDateTime entrada) {
        this.folio = folio;
        this.id = "TKT-" + folio;
        this.entrada = entrada;
    }

    public void vincularDatos(Vehicle v, PaymentProcessor pi) {
        this.vehiculo = v;
        this.procesadorPago = pi;
    }

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

    public boolean cobrar() {
        if (procesadorPago == null) return false;
        return procesadorPago.ejecutarCobro(montoACobrar);
    }

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
