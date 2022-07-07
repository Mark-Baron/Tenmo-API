package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.math.BigDecimal;
import java.util.List;

public interface TransferDao {

    List<Transfer> findAllTransfers();

    Transfer findTransferByTransferId(int transferId);

//    Transfer findTransferByToUserId(int toUserId);
//
//    Transfer findTransferByFromUserId(int fromUserId);

    public List<Transfer> findAllTransfersByUserId(int userId);

    boolean sendTransfer(Transfer transfer);



}
