package edu.lospedros.estacionamiento.process;

import edu.lospedros.estacionamiento.data.Ticket;
import edu.lospedros.estacionamiento.data.Vehicle;
import edu.lospedros.estacionamiento.payment.FareCalculator;
import java.time.LocalDateTime;

public class ParkingLot {
    private ParkingManager parkingManager;
    private FareCalculator fareCalculator;


    public ParkingLot(ParkingManager parkingManager, FareCalculator fareCalculator) {
        this.parkingManager = parkingManager;
        this.fareCalculator = fareCalculator;
    }


    public Ticket enterVehicle(Vehicle vehicle) {

        ParkingSpot spot = parkingManager.findSpotForVehicle(vehicle);

        if (spot != null) {

            parkingManager.parkVehicle(vehicle);


            String idTicket = "TKT-" + System.currentTimeMillis();
            Ticket newTicket = new Ticket(idTicket, vehicle, LocalDateTime.now());

            System.out.println("¡Bienvenido! Vehículo " + vehicle.getLicensePlate() + " estacionado.");
            return newTicket;
        } else {

            System.out.println("No hay espacios disponibles para este vehículo.");
            return null;
        }
    }


    public void leaveVehicle(Ticket ticket) {
        if (ticket == null) return;


        ticket.setExitTime(LocalDateTime.now());


        parkingManager.unparkVehicle(ticket.getVehicle());


        double total = fareCalculator.calculateFare(ticket);

        System.out.println("--- Recibo de Salida ---");
        System.out.println("Placa: " + ticket.getVehicle().getLicensePlate());
        System.out.println("Total a pagar: $" + total);
        System.out.println("------------------------");
    }
}