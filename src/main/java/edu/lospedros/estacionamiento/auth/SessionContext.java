package edu.lospedros.estacionamiento.auth;

import edu.lospedros.estacionamiento.account.Account;
import edu.lospedros.estacionamiento.account.Guest;

import java.util.Objects;

public class SessionContext {
    private final Account currentAccount;

    public SessionContext(Account currentAccount) {
        this.currentAccount = Objects.requireNonNull(currentAccount, "currentAccount");
    }

    public Account getCurrentAccount() {
        return currentAccount;
    }

    public boolean isGuest() {
        return currentAccount instanceof Guest;
    }

    public boolean isAdmin() {
        return "ADMIN".equals(currentAccount.getTipo());
    }
}
