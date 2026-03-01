package edu.lospedros.estacionamiento.payment;

public abstract class CardPayment extends PaymentProcessor {
    protected final String numTarjeta;
    protected final String titular;

    protected CardPayment(String numTarjeta, String titular) {
        this.numTarjeta = (numTarjeta == null) ? "" : numTarjeta.replaceAll("\\s+", "");
        this.titular = (titular == null) ? "" : titular.trim();
    }

    protected boolean validarTransaccion() {
        return !titular.isBlank() && numTarjeta.matches("\\d{16}");
    }
}
