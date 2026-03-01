package edu.lospedros.estacionamiento.persistence;

import java.time.LocalDateTime;

public class ActiveTicketRecord {
    private String ticketId;
    private String vehicleType;
    private String licensePlate;
    private String reservedBy;
    private LocalDateTime entryTime;
    private double hourlyRate;

    public ActiveTicketRecord() {
    }

    public ActiveTicketRecord(String ticketId, String vehicleType, String licensePlate, String reservedBy, LocalDateTime entryTime, double hourlyRate) {
        this.ticketId = ticketId;
        this.vehicleType = vehicleType;
        this.licensePlate = licensePlate;
        this.reservedBy = reservedBy;
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

    public String getReservedBy() {
        return reservedBy;
    }

    public void setReservedBy(String reservedBy) {
        this.reservedBy = reservedBy;
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
