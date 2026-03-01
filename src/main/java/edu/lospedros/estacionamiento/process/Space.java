package edu.lospedros.estacionamiento.process;

public class Space {
    public enum TipoEspacio { MOTO, PROMEDIO, GRANDE }

    private final int numero;
    private double mensualidad;
    private boolean ocupado;
    private final TipoEspacio tipo;

    public Space(int numero, TipoEspacio tipo) {
        this(numero, tipo, 0.0, false);
    }

    public Space(int numero, TipoEspacio tipo, double mensualidad, boolean ocupado) {
        this.numero = numero;
        this.tipo = tipo;
        this.mensualidad = mensualidad;
        this.ocupado = ocupado;
    }

    public int getNumero() {
        return numero;
    }

    public double getMensualidad() {
        return mensualidad;
    }

    public void setMensualidad(double mensualidad) {
        this.mensualidad = mensualidad;
    }

    public boolean isOcupado() {
        return ocupado;
    }

    public void setOcupado(boolean ocupado) {
        this.ocupado = ocupado;
    }

    public TipoEspacio getTipo() {
        return tipo;
    }
}
