package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TransactionController {

    private AccountDao accountDao;
    private TransferDao transferDao;
    private UserDao userDao;

    public TransactionController(AccountDao accountDao, TransferDao transferDao, UserDao userDao){
        this.accountDao = accountDao;
        this.transferDao = transferDao;
        this.userDao = userDao;
    }

    @PostMapping(path="/users")
    public User addUser(@RequestBody User user){
        userDao.create(user.getUsername(), user.getPassword());
        //accountDao.create(user.getId());
        return user;
    }

    @GetMapping(path="/users")
    public List<User> listUsers(){
        return userDao.findAll();
    }

    @GetMapping(path="/users/{username}")
    public User getUser(@PathVariable String username){
        return userDao.findByUsername(username);
    }

    @GetMapping(path="/accounts")
    public List<Account> listAccounts(){
        return accountDao.findAll();
    }

    @GetMapping(path="/accounts/{username}")
    public Account getAccount(@PathVariable String username){
        return accountDao.findByUsername(username);
    }

    @PostMapping(path="/transfers")
    public boolean makeTransfer(@RequestBody Transfer transfer) {
        return transferDao.create(transfer);
    }

    @GetMapping(path="/transfers/{username}")
    public List<Transfer> listTransfers(@PathVariable String username) {
        return transferDao.findAllTransfersByUserName(username);
    }

}
