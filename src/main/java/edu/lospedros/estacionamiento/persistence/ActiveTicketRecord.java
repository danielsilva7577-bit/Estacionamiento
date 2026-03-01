package edu.lospedros.estacionamiento.persistence;

import java.time.LocalDateTime;

/**
 * DTO (Data Transfer Object) para persistir tickets activos.
 * <p>
 * Se utiliza para serializar el estado de los vehículos actualmente estacionados.
 * </p>
 */
public class ActiveTicketRecord {
    private String ticketId;
    private String vehicleType;
    private String licensePlate;
    private LocalDateTime entryTime;
    private double hourlyRate;

    public ActiveTicketRecord() {
    }

    public ActiveTicketRecord(String ticketId, String vehicleType, String licensePlate, LocalDateTime entryTime, double hourlyRate) {
        this.ticketId = ticketId;
        this.vehicleType = vehicleType;
        this.licensePlate = licensePlate;
        this.entryTime = entryTime;
        this.hourlyRate = hourlyRate;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public LocalDateTime getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(LocalDateTime entryTime) {
        this.entryTime = entryTime;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }
}
