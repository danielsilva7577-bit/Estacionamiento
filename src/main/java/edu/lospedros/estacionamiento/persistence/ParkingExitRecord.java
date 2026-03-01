package edu.lospedros.estacionamiento.persistence;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ParkingExitRecord {
    private String ticketId;
    private int spaceId;
    private String vehicleType;
    private String licensePlate;
    private LocalDateTime entryTime;
    private LocalDateTime exitTime;
    private BigDecimal totalPaid;
    private String paymentMethod;

    public ParkingExitRecord() {
    }

    public ParkingExitRecord(String ticketId, int spaceId, String vehicleType, String licensePlate,
                             LocalDateTime entryTime, LocalDateTime exitTime, BigDecimal totalPaid, String paymentMethod) {
        this.ticketId = ticketId;
        this.spaceId = spaceId;
        this.vehicleType = vehicleType;
        this.licensePlate = licensePlate;
        this.entryTime = entryTime;
        this.exitTime = exitTime;
        this.totalPaid = totalPaid;
        this.paymentMethod = paymentMethod;
    }

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public int getSpaceId() {
        return spaceId;
    }

    public void setSpaceId(int spaceId) {
        this.spaceId = spaceId;
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

    public LocalDateTime getExitTime() {
        return exitTime;
    }

    public void setExitTime(LocalDateTime exitTime) {
        this.exitTime = exitTime;
    }

    public BigDecimal getTotalPaid() {
        return totalPaid;
    }

    public void setTotalPaid(BigDecimal totalPaid) {
        this.totalPaid = totalPaid;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}
