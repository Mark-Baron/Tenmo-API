package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.math.BigDecimal;
import java.util.List;

public interface TransferDao {

    List<Transfer> findAllTransfers();

    Transfer findTransferByTransferId(int transferId);

    Transfer findTransferByToAccountId(int toAccountId);

    Transfer findTransferByFromAccountId(int fromAccountId);

    public List<Transfer> findAllTransfersByUserName(String username);

    boolean create(Transfer transfer);



}
