package com.example.service;

import com.example.entity.Account;
import com.example.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    @Autowired
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }
    

    // Register a new account
    public Account register(Account account) throws Exception {
        if (account.getUsername() == null || account.getUsername().trim().isEmpty()) {
            throw new Exception("Username cannot be blank");
        }
        if (account.getPassword() == null || account.getPassword().length() < 4) {
            throw new Exception("Password must be at least 4 characters long");
        }
        Optional<Account> existingAccount = accountRepository.findByUsername(account.getUsername());
        if (existingAccount.isPresent()) {
            throw new Exception("Username already exists");
        }
        return accountRepository.save(account);
    }

    // Validate login credentials
    public Account login(String username, String password) throws Exception {
        return accountRepository.findByUsername(username)
            .filter(account -> account.getPassword().equals(password))
            .orElseThrow(() -> new Exception("Invalid login credentials"));
    }
}
