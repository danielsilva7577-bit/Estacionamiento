package edu.lospedros.estacionamiento.payment;

public abstract class PaymentProcessor {
    public abstract boolean ejecutarCobro(double monto);
    public abstract String nombreMetodo();
}
