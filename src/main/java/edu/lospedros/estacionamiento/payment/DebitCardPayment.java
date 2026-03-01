package edu.lospedros.estacionamiento.payment;

public class DebitCardPayment extends CardPayment {
    private final int nip;

    public DebitCardPayment(String numTarjeta, String titular, int nip) {
        super(numTarjeta, titular);
        this.nip = nip;
    }

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
