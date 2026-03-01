package edu.lospedros.estacionamiento.account;

import java.util.List;
import java.util.Objects;

public class Administrator extends Account {
    private int operacionesUsuarios;
    private double ultimoReporteIngresos;
    private int ultimaConfigNp;
    private List<Double> tarifasConfiguradas = List.of();

    public Administrator(String id, String email, String passwordHash) {
        super(id, email, passwordHash);
    }

    @Override
    public String getTipo() {
        return "ADMIN";
    }

    public void gestionarUsuarios() {
        operacionesUsuarios++;
    }

    public void generarReporteIngresos() {
        ultimoReporteIngresos = Math.max(0.0, ultimoReporteIngresos);
    }

    public void configurarTarifas(List<Double> tarifas, int np) {
        if (tarifas == null) {
            tarifasConfiguradas = List.of();
            ultimaConfigNp = Math.max(0, np);
            return;
        }
        tarifasConfiguradas = tarifas.stream()
                .filter(Objects::nonNull)
                .map(v -> Math.max(0.0, v))
                .toList();
        ultimaConfigNp = Math.max(0, np);
    }

    public int getOperacionesUsuarios() {
        return operacionesUsuarios;
    }

    public double getUltimoReporteIngresos() {
        return ultimoReporteIngresos;
    }

    public int getUltimaConfigNp() {
        return ultimaConfigNp;
    }

    public List<Double> getTarifasConfiguradas() {
        return tarifasConfiguradas;
    }

    public void setUltimoReporteIngresos(double ultimoReporteIngresos) {
        this.ultimoReporteIngresos = Math.max(0.0, ultimoReporteIngresos);
    }
}
