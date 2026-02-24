package edu.lospedros.estacionamiento.process;

import edu.lospedros.estacionamiento.data.Ticket;

public interface TarifaCalculator {
    double calculateFare(Ticket ticket);
}