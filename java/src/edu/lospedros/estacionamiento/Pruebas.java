package edu.lospedros.estacionamiento;

import edu.lospedros.estacionamiento.data.*;
import edu.lospedros.estacionamiento.payment.BaseTarifaProcess;
import edu.lospedros.estacionamiento.process.*;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class Pruebas {

    // VEHICLES
    @Test
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

    // TICKET

    @Test
    void shouldCalculateDurationWithExitTime() {
        Car car = new Car("ABC");
        LocalDateTime entry = LocalDateTime.now().minusHours(2);

        Ticket ticket = new Ticket("T1", car, entry);
        ticket.setExitTime(LocalDateTime.now());

        assertTrue(ticket.calculateParkingDuration().toHours() >= 2);
    }

    @Test
    void shouldUseNowIfExitTimeIsNull() {
        Car car = new Car("ABC");
        LocalDateTime entry = LocalDateTime.now().minusMinutes(30);

        Ticket ticket = new Ticket("T2", car, entry);

        assertTrue(ticket.calculateParkingDuration().toMinutes() >= 30);
    }

    // TARIFA PROCESS

    @Test
    void shouldChargeMinimumOneHour() {
        Car car = new Car("ABC");

        Ticket ticket = new Ticket(
                "T3",
                car,
                LocalDateTime.now().minusMinutes(10)
        );

        ticket.setExitTime(LocalDateTime.now());

        BaseTarifaProcess process = BaseTarifaProcess.withDefaultRates();

        BigDecimal result = process.calculateFare(ticket, BigDecimal.ZERO);

        assertEquals(new BigDecimal("50.00"), result);
    }

    @Test
    void shouldRoundUpFractionalHours() {
        Car car = new Car("ABC");

        Ticket ticket = new Ticket(
                "T4",
                car,
                LocalDateTime.now().minusMinutes(61)
        );

        ticket.setExitTime(LocalDateTime.now());

        BaseTarifaProcess process = BaseTarifaProcess.withDefaultRates();

        BigDecimal result = process.calculateFare(ticket, BigDecimal.ZERO);

        assertEquals(new BigDecimal("100.00"), result);
    }

    @Test
    void motorcycleShouldCost20PerHour() {
        Motorcycle moto = new Motorcycle("M1");

        Ticket ticket = new Ticket(
                "T5",
                moto,
                LocalDateTime.now().minusHours(1)
        );

        ticket.setExitTime(LocalDateTime.now());

        BaseTarifaProcess process = BaseTarifaProcess.withDefaultRates();

        BigDecimal result = process.calculateFare(ticket, BigDecimal.ZERO);

        assertEquals(new BigDecimal("20.00"), result);
    }

    // PARKING MANAGER
    @Test
    void shouldReserveAndReleaseSpot() throws Exception {

        ParkingManager manager = new ParkingManager();

        // Usamos reflexión porque no existe addSpot()
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

    // INTEGRATION TEST

    @Test
    void fullParkingFlowShouldWork() throws Exception {

        ParkingManager manager = new ParkingManager();

        Field field = ParkingManager.class.getDeclaredField("spots");
        field.setAccessible(true);

        @SuppressWarnings("unchecked")
        List<ParkingSpot> spots = (List<ParkingSpot>) field.get(manager);

        spots.add(new CompactSpot());

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