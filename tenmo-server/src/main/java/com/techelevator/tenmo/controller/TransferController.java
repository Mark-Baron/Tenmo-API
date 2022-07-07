package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.Exceptions.InvalidTransferException;
import com.techelevator.tenmo.Exceptions.UnauthorizedUserException;
import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;

@RestController
@PreAuthorize("isAuthenticated()")
public class TransferController {

    private AccountDao accountDao;
    private TransferDao transferDao;
    private UserDao userDao;

    public TransferController(AccountDao accountDao, TransferDao transferDao, UserDao userDao) {
        this.accountDao = accountDao;
        this.transferDao = transferDao;
        this.userDao = userDao;
    }

    @GetMapping (path="/transfers")
    public List<Transfer> getAllTransfers() {
        return transferDao.findAllTransfers();
    }

    @GetMapping(path="/transfers/{transferId}")
    public Transfer getTransfer(@PathVariable int transferId) {
        return transferDao.findTransferByTransferId(transferId);
    }

    @GetMapping(path="/users/{userId}/transfers")
    public List<Transfer> listTransfersByUserId(@PathVariable int userId) {
        return transferDao.findAllTransfersByUserId(userId);
    }

    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping(path="/transfers")
    public boolean sendTransfer(@Valid @RequestBody Transfer transfer, Principal principal) {
        int transferFromId = transfer.getFromUserId();
        int principalId = userDao.findIdByUsername(principal.getName());

        if(transferFromId == principalId) {
            Account fromAccount = accountDao.findByUserId(transfer.getFromUserId());
            fromAccount.setBalance(fromAccount.getBalance().subtract(transfer.getTransferAmount()));
            Account toAccount = accountDao.findByUserId(transfer.getToUserId());
            toAccount.setBalance(toAccount.getBalance().add(transfer.getTransferAmount()));

            if(transferDao.sendTransfer(transfer)){
                accountDao.update(fromAccount.getAccount_id(), fromAccount);
                accountDao.update(toAccount.getAccount_id(), toAccount);
                return true;
            } else {
                throw new InvalidTransferException("An error occurred");
            }

            //Must be appropriate User to send money
        } else {
            throw new UnauthorizedUserException("Unauthorized to send money from this account");
        }
    }
}
