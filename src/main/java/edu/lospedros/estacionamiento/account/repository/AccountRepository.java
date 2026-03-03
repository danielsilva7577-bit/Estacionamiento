package edu.lospedros.estacionamiento.account.repository;

import edu.lospedros.estacionamiento.account.Account;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz para el repositorio de cuentas de usuario.
 * <p>
 * Define las operaciones CRUD básicas para gestionar las cuentas del sistema.
 * </p>
 */
public interface AccountRepository {
    /**
     * Obtiene todas las cuentas registradas.
     *
     * @return Lista de todas las cuentas.
     */
    List<Account> findAll();

    /**
     * Busca una cuenta por su correo electrónico.
     *
     * @param email El correo a buscar.
     * @return Un {@link Optional} con la cuenta si se encuentra.
     */
    Optional<Account> findByEmail(String email);

    /**
     * Guarda una nueva cuenta en el repositorio.
     *
     * @param account La cuenta a guardar.
     * @return {@code true} si se guardó correctamente, {@code false} si ya existía.
     */
    boolean saveAccount(Account account);

    /**
     * Actualiza los datos de una cuenta existente.
     *
     * @param account La cuenta con los datos actualizados.
     * @return {@code true} si la actualización fue exitosa.
     */
    boolean updateAccount(Account account);

    /**
     * Elimina una cuenta por su correo electrónico.
     *
     * @param email El correo de la cuenta a eliminar.
     * @return {@code true} si se eliminó correctamente.
     */
    boolean deleteByEmail(String email);
}
