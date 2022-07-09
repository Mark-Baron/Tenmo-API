package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.math.BigDecimal;
import java.util.List;

public interface TransferDao {

    List<Transfer> findAllTransfers();

    Transfer findTransferByTransferId(int transferId);

    public List<Transfer> findAllTransfersByUserId(int userId);

    Transfer sendTransfer(Transfer transfer);

    Transfer requestTransfer(Transfer transfer);


    void approveTransfer(Transfer transfer);
}
