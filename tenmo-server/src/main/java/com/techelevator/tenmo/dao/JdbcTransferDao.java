package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.Exceptions.InvalidTransferException;
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

    public JdbcTransferDao(JdbcTemplate jdbcTemplate) {this.jdbcTemplate = jdbcTemplate;}

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
        }
        //custom exception
        return null;
    }

//    public Transfer findTransferByToUserId(int toUserId) {
//        String sql = "select transfer_id, to_account, from_account, transfer_amount, transfer_state from transfer where to_account = ?";
//        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, toUserId);
//        if(results.next()) {
//            return mapRowToTransfer(results);
//        }
//        //custom exception
//        return null;
//    }
//
//    public Transfer findTransferByFromUserId(int fromUserId) {
//        String sql = "select transfer_id, to_account, from_account, transfer_amount, transfer_state from transfer where from_account = ?";
//        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, fromUserId);
//        if(results.next()) {
//            return mapRowToTransfer(results);
//        }
//        //custom exception
//        return null;
//    }

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

    public boolean sendTransfer(Transfer transfer) {
        String sql = "insert into transfer(to_user, from_user, transfer_amount, transfer_status)" +
                " values(?, ?, ?, 'Approved')";

        String sql2 = "SELECT balance FROM account as a JOIN tenmo_user as tu ON tu.user_id = a.user_id WHERE a.user_id = ?;";
        BigDecimal accountBalance = jdbcTemplate.queryForObject(sql2, BigDecimal.class, transfer.getFromUserId());

        if(transfer.getToUserId() == transfer.getFromUserId()) {
            throw new InvalidTransferException("Unable to send money to yourself.");
        }

        else if(accountBalance.compareTo(transfer.getTransferAmount()) == -1) {
            throw new InvalidTransferException("Unable to send more money than in account.");
        }

        else if(transfer.getTransferAmount().compareTo(new BigDecimal("0")) <= 0) {
            throw new InvalidTransferException("Unable to send 0 or negative amount.");
        }

        else {
            return jdbcTemplate.update(sql, transfer.getToUserId(), transfer.getFromUserId(),
                    transfer.getTransferAmount()) == 1;
        }

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
