package edu.lospedros.estacionamiento.account.repository;

import edu.lospedros.estacionamiento.account.Client;
import edu.lospedros.estacionamiento.auth.PasswordHasher;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class FileCuentaRepositoryTest {

    @Test
    void cargaSemillaYPermiteAltaBaja() throws IOException {
        Path dir = Files.createTempDirectory("accounts-test-");
        Path file = dir.resolve("accounts.properties");

        PasswordHasher hasher = new PasswordHasher();
        FileAccountRepository repo = new FileAccountRepository(file, hasher);

        assertTrue(repo.findByEmail("admin@parking.local").isPresent());
        assertTrue(repo.findByEmail("cliente@parking.local").isPresent());

        Client nuevo = new Client("client-99", "nuevo@parking.local", hasher.hash("secreto123"));
        assertTrue(repo.saveAccount(nuevo));
        assertTrue(repo.findByEmail("nuevo@parking.local").isPresent());

        // Duplicado
        assertFalse(repo.saveAccount(new Client("client-100", "nuevo@parking.local", hasher.hash("otro12345"))));

        assertTrue(repo.deleteByEmail("nuevo@parking.local"));
        assertFalse(repo.findByEmail("nuevo@parking.local").isPresent());
    }

    @Test
    void persisteMultiplesVehiculosPorCliente() throws IOException {
        Path dir = Files.createTempDirectory("accounts-multi-vehicles-");
        Path file = dir.resolve("accounts.properties");

        PasswordHasher hasher = new PasswordHasher();
        FileAccountRepository repo = new FileAccountRepository(file, hasher);

        Client cliente = new Client("client-200", "multi@parking.local", hasher.hash("secreto123"));
        assertTrue(cliente.addVehicle("CAR", "Toyota", "Corolla", "Rojo", "ABC1234"));
        assertTrue(cliente.addVehicle("MOTORCYCLE", "Honda", "CBR", "Negro", "MOT1234"));
        assertTrue(cliente.addVehicle("TRUCK", "Nissan", "Frontier", "Blanco", "TRK1234"));
        assertTrue(repo.saveAccount(cliente));

        FileAccountRepository reloaded = new FileAccountRepository(file, hasher);
        Client loaded = (Client) reloaded.findByEmail("multi@parking.local").orElseThrow();
        assertEquals(3, loaded.getVehicles().size());
    }

    @Test
    void actualizaCuentaSinBorrarla() throws IOException {
        Path dir = Files.createTempDirectory("accounts-update-");
        Path file = dir.resolve("accounts.properties");

        PasswordHasher hasher = new PasswordHasher();
        FileAccountRepository repo = new FileAccountRepository(file, hasher);

        Client original = (Client) repo.findByEmail("cliente@parking.local").orElseThrow();
        Client updated = new Client(original.getId(), original.getEmail(), original.getPasswordHash());
        assertTrue(updated.addVehicle("TRUCK", "Nissan", "Frontier", "Gris", "TRK1234"));

        assertTrue(repo.updateAccount(updated));

        Client loaded = (Client) repo.findByEmail("cliente@parking.local").orElseThrow();
        assertEquals("TRUCK", loaded.getVehicleType());
        assertEquals("TRK1234", loaded.getVehiclePlate());
    }
}
