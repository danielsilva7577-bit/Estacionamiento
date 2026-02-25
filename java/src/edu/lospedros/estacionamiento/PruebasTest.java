package edu.lospedros.estacionamiento;

import edu.lospedros.estacionamiento.data.*;
import edu.lospedros.estacionamiento.payment.BaseTarifaProcess;
import edu.lospedros.estacionamiento.process.*;
// CORRECCIÓN: Usar solo JUPITER (JUnit 5)
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// CORRECCIÓN: Usar solo las aserciones de JUPITER
import static org.junit.jupiter.api.Assertions.*;

public class PruebasTest {

    @Test
    @DisplayName("Validar que el coche sea de tamaño mediano")
    void carShouldBeMedium() {
        Car car = new Car("ABC123");
        assertEquals(VehicleSize.MEDIUM, car.getSize());
        assertEquals("ABC123", car.getLicensePlate());
    }

    @Test
    void motorcycleShouldBeSmall() {
        Motorcycle moto = new Motorcycle("MOTO1");
        assertEquals(VehicleSize.SMALL, moto.getSize());
    }

    @Test
    void truckShouldBeLarge() {
        Truck truck = new Truck("TRK1");
        assertEquals(VehicleSize.LARGE, truck.getSize());
    }

    @Test
    void shouldCalculateDurationWithExitTime() {
        Car car = new Car("ABC");
        LocalDateTime entry = LocalDateTime.now().minusHours(2).minusMinutes(5);
        Ticket ticket = new Ticket("T1", car, entry);
        ticket.setExitTime(LocalDateTime.now());

        assertTrue(ticket.calculateParkingDuration().toHours() >= 2);
    }

    @Test
    void shouldUseNowIfExitTimeIsNull() {
        Car car = new Car("ABC");
        LocalDateTime entry = LocalDateTime.now().minusMinutes(35);
        Ticket ticket = new Ticket("T2", car, entry);

        assertTrue(ticket.calculateParkingDuration().toMinutes() >= 30);
    }

    @Test
    void shouldChargeMinimumOneHour() {
        Car car = new Car("ABC");
        Ticket ticket = new Ticket("T3", car, LocalDateTime.now().minusMinutes(10));
        ticket.setExitTime(LocalDateTime.now());

        BaseTarifaProcess process = BaseTarifaProcess.withDefaultRates();
        BigDecimal result = process.calculateFare(ticket, BigDecimal.ZERO);

        assertEquals(0, new BigDecimal("50.00").compareTo(result));
    }

    @Test
    void shouldRoundUpFractionalHours() {
        Car car = new Car("ABC");
        Ticket ticket = new Ticket("T4", car, LocalDateTime.now().minusMinutes(61));
        ticket.setExitTime(LocalDateTime.now());

        BaseTarifaProcess process = BaseTarifaProcess.withDefaultRates();
        BigDecimal result = process.calculateFare(ticket, BigDecimal.ZERO);

        assertEquals(0, new BigDecimal("100.00").compareTo(result));
    }

    @Test
    void shouldReserveAndReleaseSpot() throws Exception {
        ParkingManager manager = new ParkingManager();

        Field field = ParkingManager.class.getDeclaredField("spots");
        field.setAccessible(true);

        @SuppressWarnings("unchecked")
        List<ParkingSpot> spots = (List<ParkingSpot>) field.get(manager);
        spots.add(new CompactSpot());

        Motorcycle moto = new Motorcycle("M2");

        assertNotNull(manager.findSpotForVehicle(moto));
        manager.parkVehicle(moto);
        assertNull(manager.findSpotForVehicle(moto));
        manager.unparkVehicle(moto);
        assertNotNull(manager.findSpotForVehicle(moto));
    }

    @Test
    void fullParkingFlowShouldWork() throws Exception {
        ParkingManager manager = new ParkingManager();
        Field field = ParkingManager.class.getDeclaredField("spots");
        field.setAccessible(true);
        ((List<ParkingSpot>) field.get(manager)).add(new CompactSpot());

        TarifaCalculator calculator = ticket -> {
            BaseTarifaProcess process = BaseTarifaProcess.withDefaultRates();
            return process.calculateFare(ticket, BigDecimal.ZERO).doubleValue();
        };

        ParkingLot lot = new ParkingLot(manager, calculator);
        Motorcycle moto = new Motorcycle("FLOW1");

        Ticket ticket = lot.enterVehicle(moto);
        assertNotNull(ticket);

        ticket.setExitTime(LocalDateTime.now());
        manager.unparkVehicle(moto);
        assertNotNull(manager.findSpotForVehicle(moto));
    }
}