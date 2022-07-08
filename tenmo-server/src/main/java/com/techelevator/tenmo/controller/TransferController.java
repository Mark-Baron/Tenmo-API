package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.Exceptions.UnauthorizedUserException;
import com.techelevator.tenmo.dao.AccountDao;
import com.techelevator.tenmo.dao.TransferDao;
import com.techelevator.tenmo.dao.UserDao;
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

    //For test purposes
    @GetMapping (path="/transfers")
    public List<Transfer> getAllTransfers() {
        return transferDao.findAllTransfers();
    }

    //#7
    @GetMapping(path="/transfers/{transferId}")
    public Transfer getTransfer(@PathVariable int transferId, Principal principal) {
        String userName = principal.getName();
        Long ActualUserId = userDao.findByUsername(userName).getId();
        int fromUserId = transferDao.findTransferByTransferId(transferId).getFromUserId();
        int toUserId = transferDao.findTransferByTransferId(transferId).getToUserId();
        if(ActualUserId.intValue() == fromUserId || ActualUserId.intValue() == toUserId) {
            return transferDao.findTransferByTransferId(transferId);
        } else {
            throw new UnauthorizedUserException("Unauthorized to view transfer");
        }
    }

    //#6
    @GetMapping(path="/users/{ProvidedUserId}/transfers")
    public List<Transfer> listTransfersByUserId(@PathVariable int ProvidedUserId, Principal principal) {
        String userName = principal.getName();
        Long ActualUserId = userDao.findByUsername(userName).getId();

        if(ProvidedUserId == ActualUserId.intValue()) {
            return transferDao.findAllTransfersByUserId(ProvidedUserId);

            //Must be appropriate User to view transfers
        } else {
            throw new UnauthorizedUserException("Unauthorized to view transfers of this account");
        }
    }

    //#5
    @ResponseStatus(value = HttpStatus.CREATED)
    @PostMapping(path="/transfers")
    public Transfer sendTransfer(@Valid @RequestBody Transfer transfer, Principal principal) {
        int transferFromId = transfer.getFromUserId();
        int principalId = userDao.findIdByUsername(principal.getName());

        if(transferFromId == principalId) {
                return transferDao.sendTransfer(transfer);

            //Must be appropriate User to send money
        } else {
            throw new UnauthorizedUserException("Unauthorized to send money from this account");
        }
    }
}
