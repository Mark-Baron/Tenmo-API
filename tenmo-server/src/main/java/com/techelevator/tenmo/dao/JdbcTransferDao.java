package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.Exceptions.InvalidTransferException;
import com.techelevator.tenmo.Exceptions.TransferNotFoundException;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDao{

    private JdbcTemplate jdbcTemplate;
    private AccountDao accountDao;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate, AccountDao accountDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.accountDao = accountDao;
    }

    public List<Transfer> findAllTransfers() {
        List<Transfer> transfers = new ArrayList<>();
        String sql =
                "select transfer_id, to_user, from_user, transfer_amount, transfer_status from transfer";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while(results.next()) {
            transfers.add(mapRowToTransfer(results));
        }
        return transfers;
    }

    //#7
    public Transfer findTransferByTransferId(int transferId) {
        String sql = "select transfer_id, to_user, from_user, transfer_amount, transfer_status from transfer where transfer_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, transferId);
        if(results.next()) {
            return mapRowToTransfer(results);
        } else {
            throw new TransferNotFoundException("Invalid transferId");
        }
    }

    //#6
    public List<Transfer> findAllTransfersByUserId(int userId) {
        List<Transfer> transfers = new ArrayList<>();
        String sql =
                "select transfer_id, to_user, from_user, transfer_amount, transfer_status" +
                " from transfer as t" +
                " where to_user = ? or from_user = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId, userId);
        while(results.next()) {
            transfers.add(mapRowToTransfer(results));
        }
        return transfers;
    }

    //#5
    public Transfer sendTransfer(Transfer transfer) {
        String sql = "insert into transfer(to_user, from_user, transfer_amount, transfer_status)" +
                " values(?, ?, ?, 'Approved') returning transfer_id";

        String sql2 = "select balance from account as a join tenmo_user as tu on tu.user_id = a.user_id where a.user_id = ?;";
        BigDecimal accountBalance = jdbcTemplate.queryForObject(sql2, BigDecimal.class, transfer.getFromUserId());

        if(transfer.getToUserId() == transfer.getFromUserId()) {
            throw new InvalidTransferException("Unable to send money from and to the same account");
        }

        else if(accountBalance.compareTo(transfer.getTransferAmount()) == -1) {
            throw new InvalidTransferException("Unable to send more money than in account");
        }

        else if(transfer.getTransferAmount().compareTo(new BigDecimal("0")) <= 0) {
            throw new InvalidTransferException("Unable to send 0 or negative amount");
        }

        else {
            updateAccounts(transfer);
            int newId = jdbcTemplate.queryForObject(sql, Integer.class, transfer.getToUserId(), transfer.getFromUserId(),
                    transfer.getTransferAmount());
            transfer.setTransferId(newId);
            transfer.setTransferStatus("Approved");
            return transfer;
        }
    }

    public Transfer requestTransfer(Transfer transfer) {
        String sql = "insert into transfer(to_user, from_user, transfer_amount, transfer_status)" +
                " values(?, ?, ?, 'Pending') returning transfer_id";

        if(transfer.getToUserId() == transfer.getFromUserId()) {
            throw new InvalidTransferException("Unable to request money from and to the same account");
        }

        else if(transfer.getTransferAmount().compareTo(new BigDecimal("0")) <= 0) {
            throw new InvalidTransferException("Unable to request 0 or negative amount");
        }

        else {
            int newId = jdbcTemplate.queryForObject(sql, Integer.class, transfer.getToUserId(), transfer.getFromUserId(),
                    transfer.getTransferAmount());
            transfer.setTransferId(newId);
            transfer.setTransferStatus("Pending");
            return transfer;
        }

    }

    public void approveTransfer(Transfer transfer) {
        String sql = "update transfer set transfer_status = 'Approved' where transfer_id = ?";
        jdbcTemplate.update(sql, transfer.getTransferId());
        if(transfer.getTransferStatus().equalsIgnoreCase("Pending")) {
            if(transfer.getTransferAmount().compareTo(accountDao.findByUserId(transfer.getFromUserId()).getBalance()) <= 0) {
                updateAccounts(transfer);
            } else {
                throw new InvalidTransferException("Cannot accept a request that is greater than account balance");
            }
        }
    }

    public void rejectTransfer(Transfer transfer) {
        String sql = "update transfer set transfer_status = 'Rejected' where transfer_id = ?";
        if (transfer.getTransferStatus().equalsIgnoreCase("Pending")) {
            jdbcTemplate.update(sql, transfer.getTransferId());
        }
    }

    private void updateAccounts(Transfer transfer) {
        Account fromAccount = accountDao.findByUserId(transfer.getFromUserId());
        fromAccount.setBalance(fromAccount.getBalance().subtract(transfer.getTransferAmount()));
        Account toAccount = accountDao.findByUserId(transfer.getToUserId());
        toAccount.setBalance(toAccount.getBalance().add(transfer.getTransferAmount()));
        accountDao.update(fromAccount.getAccount_id(), fromAccount);
        accountDao.update(toAccount.getAccount_id(), toAccount);
    }

    private Transfer mapRowToTransfer(SqlRowSet sqlRowSet) {
        Transfer transfer = new Transfer();
        transfer.setTransferId(sqlRowSet.getInt("transfer_id"));
        transfer.setToUserId(sqlRowSet.getInt("to_user"));
        transfer.setFromUserId(sqlRowSet.getInt("from_user"));
        transfer.setTransferAmount(sqlRowSet.getBigDecimal("transfer_amount"));
        transfer.setTransferStatus(sqlRowSet.getString("transfer_status"));
        return transfer;
    }

}
