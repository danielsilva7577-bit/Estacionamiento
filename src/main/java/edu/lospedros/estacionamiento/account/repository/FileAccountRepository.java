package edu.lospedros.estacionamiento.account.repository;

import edu.lospedros.estacionamiento.account.Administrator;
import edu.lospedros.estacionamiento.account.Client;
import edu.lospedros.estacionamiento.account.Account;
import edu.lospedros.estacionamiento.auth.PasswordHasher;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Properties;

public class FileAccountRepository implements AccountRepository {
    private final Path filePath;
    private final PasswordHasher hasher;
    private final List<Account> accounts = new ArrayList<>();

    public FileAccountRepository(Path filePath, PasswordHasher hasher) {
        this.filePath = filePath;
        this.hasher = hasher;
        loadOrSeed();
    }

    @Override
    public List<Account> findAll() {
        return new ArrayList<>(accounts);
    }

    @Override
    public Optional<Account> findByEmail(String email) {
        if (email == null || email.isBlank()) return Optional.empty();
        String normalized = email.trim().toLowerCase(Locale.ROOT);
        return accounts.stream()
                .filter(a -> a.getEmail().trim().toLowerCase(Locale.ROOT).equals(normalized))
                .findFirst();
    }

    @Override
    public synchronized boolean saveAccount(Account account) {
        if (account == null) return false;
        String normalized = account.getEmail().trim().toLowerCase(Locale.ROOT);
        Optional<Account> existing = findByEmail(normalized);
        if (existing.isPresent()) return false;
        accounts.add(account);
        save();
        return true;
    }

    @Override
    public synchronized boolean deleteByEmail(String email) {
        if (email == null || email.isBlank()) return false;
        String normalized = email.trim().toLowerCase(Locale.ROOT);
        boolean removed = accounts.removeIf(a -> a.getEmail().trim().toLowerCase(Locale.ROOT).equals(normalized));
        if (removed) save();
        return removed;
    }

    @Override
    public synchronized boolean updateAccount(Account account) {
        if (account == null) return false;
        String normalizedEmail = account.getEmail().trim().toLowerCase(Locale.ROOT);
        for (int i = 0; i < accounts.size(); i++) {
            Account existing = accounts.get(i);
            if (existing.getEmail().trim().toLowerCase(Locale.ROOT).equals(normalizedEmail)) {
                accounts.set(i, account);
                save();
                return true;
            }
        }
        return false;
    }

    private void loadOrSeed() {
        if (!Files.exists(filePath)) {
            seedDefaults();
            save();
            return;
        }

        try {
            Properties props = new Properties();
            try (InputStream in = new BufferedInputStream(Files.newInputStream(filePath))) {
                props.load(in);
            }

            int count = parseInt(props.getProperty("accounts.count"), 0);
            for (int i = 0; i < count; i++) {
                String prefix = "account." + i + ".";
                String type = props.getProperty(prefix + "type", "").trim().toUpperCase(Locale.ROOT);
                String id = props.getProperty(prefix + "id", "").trim();
                String email = props.getProperty(prefix + "email", "").trim();
                String passwordHash = props.getProperty(prefix + "passwordHash", "").trim();
                if (id.isBlank() || email.isBlank()) continue;

                if ("ADMIN".equals(type)) {
                    accounts.add(new Administrator(id, email, passwordHash));
                } else if ("CLIENT".equals(type)) {
                    Client cliente = new Client(
                            id,
                            email,
                            passwordHash,
                            props.getProperty(prefix + "vehicleType", ""),
                            props.getProperty(prefix + "vehicleBrand", ""),
                            props.getProperty(prefix + "vehicleModel", ""),
                            props.getProperty(prefix + "vehicleColor", ""),
                            props.getProperty(prefix + "vehiclePlate", "")
                    );

                    int vehicleCount = parseInt(props.getProperty(prefix + "vehicles.count"), 0);
                    if (vehicleCount > 0) {
                        cliente.clearVehicles();
                        for (int v = 0; v < vehicleCount; v++) {
                            String vehiclePrefix = prefix + "vehicle." + v + ".";
                            cliente.addVehicle(
                                    props.getProperty(vehiclePrefix + "type", ""),
                                    props.getProperty(vehiclePrefix + "brand", ""),
                                    props.getProperty(vehiclePrefix + "model", ""),
                                    props.getProperty(vehiclePrefix + "color", ""),
                                    props.getProperty(vehiclePrefix + "plate", "")
                            );
                        }
                    }

                    accounts.add(cliente);
                }
            }

            if (accounts.isEmpty()) {
                seedDefaults();
                save();
            }
        } catch (IOException ex) {
            seedDefaults();
            save();
        }
    }

    private void seedDefaults() {
        accounts.clear();
        accounts.add(new Administrator("admin-1", "admin@parking.local", hasher.hash("admin123")));
        accounts.add(new Client("client-1", "cliente@parking.local", hasher.hash("cliente123"), "CAR", "GENERICA", "BASE", "BLANCO", "ABC1234"));
    }

    private void save() {
        try {
            Path parent = filePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }

            Properties props = new Properties();
            props.setProperty("accounts.count", String.valueOf(accounts.size()));
            for (int i = 0; i < accounts.size(); i++) {
                Account account = accounts.get(i);
                String prefix = "account." + i + ".";
                props.setProperty(prefix + "type", account.getTipo());
                props.setProperty(prefix + "id", account.getId());
                props.setProperty(prefix + "email", account.getEmail());
                props.setProperty(prefix + "passwordHash", account.getPasswordHash());
                if (account instanceof Client cliente) {
                    props.setProperty(prefix + "vehicleType", cliente.getVehicleType());
                    props.setProperty(prefix + "vehicleBrand", cliente.getVehicleBrand());
                    props.setProperty(prefix + "vehicleModel", cliente.getVehicleModel());
                    props.setProperty(prefix + "vehicleColor", cliente.getVehicleColor());
                    props.setProperty(prefix + "vehiclePlate", cliente.getVehiclePlate());
                    List<Client.VehicleProfile> vehicles = cliente.getVehicles();
                    props.setProperty(prefix + "vehicles.count", String.valueOf(vehicles.size()));
                    for (int v = 0; v < vehicles.size(); v++) {
                        Client.VehicleProfile vehicle = vehicles.get(v);
                        String vehiclePrefix = prefix + "vehicle." + v + ".";
                        props.setProperty(vehiclePrefix + "type", vehicle.getVehicleType());
                        props.setProperty(vehiclePrefix + "brand", vehicle.getVehicleBrand());
                        props.setProperty(vehiclePrefix + "model", vehicle.getVehicleModel());
                        props.setProperty(vehiclePrefix + "color", vehicle.getVehicleColor());
                        props.setProperty(vehiclePrefix + "plate", vehicle.getVehiclePlate());
                    }
                }
            }

            try (OutputStream out = new BufferedOutputStream(Files.newOutputStream(filePath))) {
                props.store(out, "Accounts");
            }
        } catch (IOException ignored) {
            // Best effort for local seed persistence.
        }
    }

    private static int parseInt(String value, int fallback) {
        if (value == null || value.isBlank()) return fallback;
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ex) {
            return fallback;
        }
    }
}
