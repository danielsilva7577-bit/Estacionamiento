package edu.lospedros.estacionamiento.UI;

import edu.lospedros.estacionamiento.data.*;
import edu.lospedros.estacionamiento.payment.BaseTarifaProcess;
import edu.lospedros.estacionamiento.process.*;

import java.math.BigDecimal;
import java.util.Scanner;

public class ParkingConsole {

    private ParkingLot parkingLot;

    public ParkingConsole() {

        // Crear manager
        ParkingManager manager = new ParkingManager();

        // Agregar espacios manualmente (como no hay método addSpot usamos reflexión)
        try {
            var field = ParkingManager.class.getDeclaredField("spots");
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            java.util.List<ParkingSpot> list = (java.util.List<ParkingSpot>) field.get(manager);
            list.add(new CompactSpot()); // SMALL
            list.add(new CompactSpot()); // SMALL
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Crear calculador
        TarifaCalculator calculator = ticket -> {
            BaseTarifaProcess process = BaseTarifaProcess.withDefaultRates();
            return process.calculateFare(ticket, BigDecimal.ZERO).doubleValue();
        };

        this.parkingLot = new ParkingLot(manager, calculator);
    }
@SuppressWarnings("resource")
public void start() {

    Scanner scanner = new Scanner(System.in);

    boolean running = true;

    while (running) {

        System.out.println("\n===== SISTEMA DE ESTACIONAMIENTO =====");
        System.out.println("1. Ingresar vehículo");
        System.out.println("2. Salir");
        System.out.print("Seleccione una opción: ");

        String option = scanner.nextLine();

        switch (option) {

            case "1" -> {

                Vehicle vehicle = null;

                // 🔁 Loop hasta que el tipo sea válido
                while (vehicle == null) {

                    System.out.print("Ingrese tipo (1=Car, 2=Motorcycle, 3=Truck): ");
                    String typeInput = scanner.nextLine();

                    System.out.print("Ingrese placa: ");
                    String plate = scanner.nextLine();

                    switch (typeInput) {
                        case "1" -> vehicle = new Car(plate);
                        case "2" -> vehicle = new Motorcycle(plate);
                        case "3" -> vehicle = new Truck(plate);
                        default -> System.out.println(" Tipo inválido. Intente nuevamente.");
                    }
                }

                var ticket = parkingLot.enterVehicle(vehicle);

                if (ticket == null) {
                    System.out.println("No se pudo estacionar. Intente con otro vehículo.");
                } else {
                    System.out.println("Presione ENTER para registrar salida...");
                    scanner.nextLine();
                    parkingLot.leaveVehicle(ticket);
                }
            }

            case "2" -> {
                running = false;
                System.out.println("Saliendo del sistema...");
            }

            default -> System.out.println("Opción inválida. Intente nuevamente.");
        }
    }

    System.out.println("Sistema finalizado.");
    }
}

