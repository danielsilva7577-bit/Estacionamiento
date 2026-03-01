package edu.lospedros.estacionamiento.payment;

/**
 * Procesador para pagos con tarjeta de crédito.
 * <p>
 * Incluye validación de NIP y simulación de lectura de chip.
 * </p>
 */
public class CreditCardPayment extends CardPayment {
    private final int nip;

    /**
     * Crea un procesador de tarjeta de crédito.
     *
     * @param numTarjeta Número de la tarjeta.
     * @param titular    Nombre del titular.
     * @param nip        Número de Identificación Personal (4 dígitos).
     */
    public CreditCardPayment(String numTarjeta, String titular, int nip) {
        super(numTarjeta, titular);
        this.nip = nip;
    }

    /**
     * Simula la lectura física del chip de la tarjeta.
     */
    public void leerChip() {
        // Simulacion de lectura de chip.
    }

    @Override
    public boolean ejecutarCobro(double monto) {
        leerChip();
        return monto >= 0 && validarTransaccion() && nip >= 1000 && nip <= 9999;
    }

    @Override
    public String nombreMetodo() {
        return "TARJETA_CREDITO";
    }
}
