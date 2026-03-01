package edu.lospedros.estacionamiento.payment;

/**
 * Clase base para procesadores de pago con tarjeta.
 * <p>
 * Valida la estructura básica de los datos de la tarjeta.
 * </p>
 */
public abstract class CardPayment extends PaymentProcessor {
    protected final String numTarjeta;
    protected final String titular;

    /**
     * Constructor para pagos con tarjeta.
     *
     * @param numTarjeta Número de la tarjeta (16 dígitos).
     * @param titular    Nombre del titular.
     */
    protected CardPayment(String numTarjeta, String titular) {
        this.numTarjeta = (numTarjeta == null) ? "" : numTarjeta.replaceAll("\\s+", "");
        this.titular = (titular == null) ? "" : titular.trim();
    }

    /**
     * Valida que los datos de la tarjeta sean sintácticamente correctos.
     *
     * @return {@code true} si el número tiene 16 dígitos y hay un titular.
     */
    protected boolean validarTransaccion() {
        return !titular.isBlank() && numTarjeta.matches("\\d{16}");
    }
}
