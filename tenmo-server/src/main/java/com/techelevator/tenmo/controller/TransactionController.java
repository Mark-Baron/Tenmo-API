package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
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

    @GetMapping (path="/transfers")
    public List<Transfer> getAllTransfers() {
        return transferDao.findAllTransfers();
    }

    @GetMapping(path="/transfers/{transferId}")
    public Transfer getTransfer(@PathVariable int transferId) {
        return transferDao.findTransferByTransferId(transferId);
    }

    @PostMapping(path="/transfers")
    public boolean makeTransfer(@RequestBody Transfer transfer) {
        Account fromAccount = accountDao.findByUserId(transfer.getFromUserId());
        fromAccount.setBalance(fromAccount.getBalance().subtract(transfer.getTransferAmount()));
        Account toAccount = accountDao.findByUserId(transfer.getToUserId());
        toAccount.setBalance(toAccount.getBalance().add(transfer.getTransferAmount()));
        if(transferDao.sendTransfer(transfer)){
            accountDao.update(fromAccount.getAccount_id(), fromAccount);
            accountDao.update(toAccount.getAccount_id(), toAccount);
            return true;
        } else
        {
            return false;
        }
    }

    @GetMapping(path="/users/{userId}/transfers")
    public List<Transfer> listTransfers(@PathVariable int userId) {
        return transferDao.findAllTransfersByUserId(userId);
    }

}
