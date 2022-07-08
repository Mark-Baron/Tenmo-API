package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.Exceptions.UnauthorizedUserException;
import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.model.Account;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

@PreAuthorize("isAuthenticated()")
@RestController
public class AccountController {

    private AccountDao accountDao;

    public AccountController(AccountDao accountDao) {
        this.accountDao = accountDao;
    }

    @GetMapping(path="/accounts")
    public List<Account> listAccounts(){
        return accountDao.findAll();
    }

    @GetMapping(path="/accounts/{username}")
    public Account getAccount(@PathVariable String username, Principal principal){
        String userName = principal.getName();
        if(userName.equalsIgnoreCase(username)) {
            return accountDao.findByUsername(username);
        } else {
            throw new UnauthorizedUserException("Unauthorized to view account");
        }
    }

}
