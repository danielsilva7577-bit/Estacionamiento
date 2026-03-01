package edu.lospedros.estacionamiento.account.repository;

import edu.lospedros.estacionamiento.account.Account;

import java.util.List;
import java.util.Optional;

public interface AccountRepository {
    List<Account> findAll();
    Optional<Account> findByEmail(String email);
    boolean saveAccount(Account account);
    boolean updateAccount(Account account);
    boolean deleteByEmail(String email);
}
