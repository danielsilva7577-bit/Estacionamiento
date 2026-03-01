package edu.lospedros.estacionamiento.payment;

public class CashPayment extends PaymentProcessor {
    @Override
    public boolean ejecutarCobro(double monto) {
        return monto >= 0;
    }

    @Override
    public String nombreMetodo() {
        return "EFECTIVO";
    }
}
