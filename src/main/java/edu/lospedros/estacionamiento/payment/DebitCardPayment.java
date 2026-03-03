package edu.lospedros.estacionamiento.payment;

/**
 * Procesador para pagos con tarjeta de débito.
 * <p>
 * Similar a la tarjeta de crédito, valida NIP y simula lectura de chip.
 * </p>
 */
public class DebitCardPayment extends CardPayment {
    private final int nip;

    /**
     * Crea un procesador de tarjeta de débito.
     *
     * @param numTarjeta Número de la tarjeta.
     * @param titular    Nombre del titular.
     * @param nip        Número de Identificación Personal (4 dígitos).
     */
    public DebitCardPayment(String numTarjeta, String titular, int nip) {
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
        return "TARJETA_DEBITO";
    }
}
