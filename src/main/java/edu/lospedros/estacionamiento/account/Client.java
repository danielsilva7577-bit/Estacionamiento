package edu.lospedros.estacionamiento.account;

import edu.lospedros.estacionamiento.data.Vehicle;
import edu.lospedros.estacionamiento.data.LargeVehicle;
import edu.lospedros.estacionamiento.data.StandardVehicle;
import edu.lospedros.estacionamiento.payment.PaymentProcessor;
import edu.lospedros.estacionamiento.validation.PlateValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Client extends Account {
    public static final int MAX_VEHICLES = 3;

    public static final class VehicleProfile {
        private final String vehicleType;
        private final String vehicleBrand;
        private final String vehicleModel;
        private final String vehicleColor;
        private final String vehiclePlate;

        public VehicleProfile(String vehicleType, String vehicleBrand, String vehicleModel, String vehicleColor, String vehiclePlate) {
            this.vehicleType = normalizeVehicleType(vehicleType);
            this.vehicleBrand = normalizeText(vehicleBrand);
            this.vehicleModel = normalizeText(vehicleModel);
            this.vehicleColor = normalizeText(vehicleColor);
            this.vehiclePlate = PlateValidator.normalize(vehiclePlate);
        }

        public String getVehicleType() {
            return vehicleType;
        }

        public String getVehicleBrand() {
            return vehicleBrand;
        }

        public String getVehicleModel() {
            return vehicleModel;
        }

        public String getVehicleColor() {
            return vehicleColor;
        }

        public String getVehiclePlate() {
            return vehiclePlate;
        }
    }

    private final List<VehicleProfile> vehicles = new ArrayList<>();
    private String preferredPaymentMethod = "";

    public Client(String id, String email, String passwordHash) {
        super(id, email, passwordHash);
    }

    public Client(String id, String email, String passwordHash, String vehicleType, String vehicleBrand, String vehicleModel, String vehicleColor, String vehiclePlate) {
        super(id, email, passwordHash);
        addVehicle(vehicleType, vehicleBrand, vehicleModel, vehicleColor, vehiclePlate);
    }

    @Override
    public String getTipo() {
        return "CLIENT";
    }

    public double saldoCredito;

    public void registrarVehiculo(Vehicle v) {
        if (v == null) return;
        String type = (v instanceof LargeVehicle) ? "TRUCK" : (v instanceof StandardVehicle ? "CAR" : "MOTORCYCLE");
        String plate = PlateValidator.normalize(v.getPlaca());
        if (!PlateValidator.isValid(plate)) return;
        addVehicle(type, v.getMarca(), "N/A", "N/A", plate);
    }

    public void configurarPago(PaymentProcessor p) {
        if (p == null) {
            preferredPaymentMethod = "";
            return;
        }
        preferredPaymentMethod = p.nombreMetodo();
    }

    public List<VehicleProfile> getVehicles() {
        return new ArrayList<>(vehicles);
    }

    public boolean addVehicle(String vehicleType, String vehicleBrand, String vehicleModel, String vehicleColor, String vehiclePlate) {
        if (vehicles.size() >= MAX_VEHICLES) return false;
        VehicleProfile profile = new VehicleProfile(vehicleType, vehicleBrand, vehicleModel, vehicleColor, vehiclePlate);
        if (profile.getVehicleType().isBlank()) return false;
        if (!PlateValidator.isValid(profile.getVehiclePlate())) return false;
        boolean duplicatedPlate = vehicles.stream().anyMatch(v -> v.getVehiclePlate().equals(profile.getVehiclePlate()));
        if (duplicatedPlate) return false;
        vehicles.add(profile);
        return true;
    }

    public void clearVehicles() {
        vehicles.clear();
    }

    private VehicleProfile primaryVehicle() {
        return vehicles.isEmpty() ? null : vehicles.get(0);
    }

    // Backward-compatible accessors used by existing UI flow.
    public String getVehicleType() {
        VehicleProfile v = primaryVehicle();
        return v == null ? "" : v.getVehicleType();
    }

    public String getVehicleBrand() {
        VehicleProfile v = primaryVehicle();
        return v == null ? "" : v.getVehicleBrand();
    }

    public String getVehicleModel() {
        VehicleProfile v = primaryVehicle();
        return v == null ? "" : v.getVehicleModel();
    }

    public String getVehicleColor() {
        VehicleProfile v = primaryVehicle();
        return v == null ? "" : v.getVehicleColor();
    }

    public String getVehiclePlate() {
        VehicleProfile v = primaryVehicle();
        return v == null ? "" : v.getVehiclePlate();
    }

    public String getPreferredPaymentMethod() {
        return preferredPaymentMethod;
    }

    private static String normalizeVehicleType(String raw) {
        if (raw == null || raw.isBlank()) return "";
        String key = raw.trim().toUpperCase(Locale.ROOT);
        return switch (key) {
            case "CAR", "CARRO", "PROMEDIO" -> "CAR";
            case "MOTO", "MOTORCYCLE" -> "MOTORCYCLE";
            case "TRUCK", "CAMIONETA", "GRANDE" -> "TRUCK";
            default -> "";
        };
    }

    private static String normalizeText(String raw) {
        if (raw == null) return "";
        return raw.trim();
    }
}
