package edu.lospedros.estacionamiento.payment;

import edu.lospedros.estacionamiento.data.Ticket;
import edu.lospedros.estacionamiento.data.Vehicle;
import edu.lospedros.estacionamiento.data.VehicleSize;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public class BaseTarifaProcess implements TarifaProcess {

    private final Map<VehicleSize, BigDecimal> hourlyRate;
    private final Map<VehicleSize, BigDecimal> minimumCharge;

    public BaseTarifaProcess(Map<VehicleSize, BigDecimal> hourlyRates,
                             Map<VehicleSize, BigDecimal> minimumCharges) {
        this.hourlyRate = new EnumMap<>(Objects.requireNonNull(hourlyRates, "hourlyRates"));
        this.hourlyRate.putAll(hourlyRates);
        this.minimumCharge = new EnumMap<>(Objects.requireNonNull(minimumCharges, "minimumCharges"));
        this.minimumCharge.putAll(minimumCharges);
    }

    /** default rates: small=20, medium=50, large=30 */
    public static BaseTarifaProcess withDefaultRates() {
        Map<VehicleSize, BigDecimal> hourly = new EnumMap<>(VehicleSize.class);
        hourly.put(VehicleSize.SMALL, new BigDecimal("20"));
        hourly.put(VehicleSize.MEDIUM, new BigDecimal("50"));
        hourly.put(VehicleSize.LARGE, new BigDecimal("30"));

        Map<VehicleSize, BigDecimal> min = new EnumMap<>(VehicleSize.class);
        min.put(VehicleSize.SMALL, new BigDecimal("20"));
        min.put(VehicleSize.MEDIUM, new BigDecimal("50"));
        min.put(VehicleSize.LARGE, new BigDecimal("30"));

        return new BaseTarifaProcess(hourly, min);
    }

    @Override
    public BigDecimal calculateFare(Ticket ticket, BigDecimal fare) {
        Objects.requireNonNull(ticket, "ticket");
        Objects.requireNonNull(fare, "fare");

        Vehicle vehicle = Objects.requireNonNull(ticket.getVehicle(), "ticket.vehicle");
        VehicleSize size = Objects.requireNonNull(vehicle.getSize(), "vehicle.size");

        BigDecimal ratePerHour = this.hourlyRate.get(size);
        if (ratePerHour == null) throw new IllegalStateException("No hourlyRate configured for size: " + size);

        Duration d = ticket.calculateParkingDuration();
        long minutes = d.toMinutes();

        // Always minimum 1 hour; fractions round up
        long billableHours = Math.max(1, (minutes + 59) / 60);

        BigDecimal base = ratePerHour.multiply(BigDecimal.valueOf(billableHours));

        BigDecimal min = minimumCharge.getOrDefault(size, BigDecimal.ZERO);
        if (base.compareTo(min) < 0) base = min;

        return fare.add(base).setScale(2, RoundingMode.HALF_UP);
    }
}