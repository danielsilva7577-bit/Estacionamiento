package edu.lospedros.estacionamiento.account;

import java.util.List;
import java.util.Objects;

/**
 * Representa una cuenta de administrador con privilegios elevados.
 * <p>
 * Los administradores pueden gestionar usuarios, configurar tarifas y generar reportes.
 * </p>
 */
public class Administrator extends Account {
    private int operacionesUsuarios;
    private double ultimoReporteIngresos;
    private int ultimaConfigNp;
    private List<Double> tarifasConfiguradas = List.of();

    /**
     * Crea una nueva cuenta de administrador.
     *
     * @param id           Identificador único.
     * @param email        Correo electrónico.
     * @param passwordHash Hash de la contraseña.
     */
    public Administrator(String id, String email, String passwordHash) {
        super(id, email, passwordHash);
    }

    @Override
    public String getTipo() {
        return "ADMIN";
    }

    /**
     * Registra una operación de gestión de usuarios realizada por este administrador.
     */
    public void gestionarUsuarios() {
        operacionesUsuarios++;
    }

    /**
     * Simula la generación de un reporte de ingresos.
     */
    public void generarReporteIngresos() {
        ultimoReporteIngresos = Math.max(0.0, ultimoReporteIngresos);
    }

    /**
     * Configura las tarifas del sistema.
     *
     * @param tarifas Lista de nuevas tarifas.
     * @param np      Número de parámetro de configuración.
     */
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
