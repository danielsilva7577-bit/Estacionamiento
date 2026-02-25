package edu.lospedros.estacionamiento.payment;

import edu.lospedros.estacionamiento.data.Ticket;

import java.math.BigDecimal;

public interface TarifaProcess {
    BigDecimal calculateFare(Ticket ticket, BigDecimal inputFare);
}