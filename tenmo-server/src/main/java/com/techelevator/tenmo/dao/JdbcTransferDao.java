package com.techelevator.tenmo.dao;

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

    public JdbcTransferDao(JdbcTemplate jdbcTemplate) {this.jdbcTemplate = jdbcTemplate;}

    public List<Transfer> findAllTransfers() {
        List<Transfer> transfers = new ArrayList<>();
        String sql =
                "select transfer_id, to_account, from_account, transfer_amount, transfer_state from transfer";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while(results.next()) {
            transfers.add(mapRowToTransfer(results));
        }
        return transfers;
    }

    //#7
    public Transfer findTransferByTransferId(int transferId) {
        String sql = "select transfer_id, to_account, from_account, transfer_amount, transfer_state from transfer where transfer_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, transferId);
        if(results.next()) {
            return mapRowToTransfer(results);
        }
        //custom exception
        return null;
    }

    public Transfer findTransferByToAccountId(int toAccountId) {
        String sql = "select transfer_id, to_account, from_account, transfer_amount, transfer_state from transfer where to_account = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, toAccountId);
        if(results.next()) {
            return mapRowToTransfer(results);
        }
        //custom exception
        return null;
    }

    public Transfer findTransferByFromAccountId(int fromAccountId) {
        String sql = "select transfer_id, to_account, from_account, transfer_amount, transfer_state from transfer where from_account = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, fromAccountId);
        if(results.next()) {
            return mapRowToTransfer(results);
        }
        //custom exception
        return null;
    }

    //#6
    public List<Transfer> findAllTransfersByUserName(String username) {
        List<Transfer> transfers = new ArrayList<>();
        //from transfers aka deposits
        String sql =
                "select transfer_id, to_account, from_account, transfer_amount, transfer_state" +
                " from transfer as t" +
                " join account as a on from_account = account_id" +
                " join tenmo_user as tu on a.user_id = tu.user_id" +
                " where username = ?;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, username);
        while(results.next()) {
            transfers.add(mapRowToTransfer(results));
        }
        //to transfers aka withdraws
        sql =
                "select transfer_id, to_account, from_account, transfer_amount, transfer_state" +
                        " from transfer as t" +
                        " join account as a on to_account = account_id" +
                        " join tenmo_user as tu on a.user_id = tu.user_id" +
                        " where username = ?;";
        results = jdbcTemplate.queryForRowSet(sql, username);
        while(results.next()) {
            transfers.add(mapRowToTransfer(results));
        }
        return transfers;
    }

    public boolean create(Transfer transfer) {
        String sql = "insert into transfer(to_account, from_account, transfer_amount)" +
                " values(?, ?, ?, 'Approved')";
        return jdbcTemplate.update(sql, transfer.getToAccountId(), transfer.getFromAccountId(),
                transfer.getTransferAmount()) == 1;
    }

    private Transfer mapRowToTransfer(SqlRowSet sqlRowSet) {
        Transfer transfer = new Transfer();
        transfer.setToAccountId(sqlRowSet.getInt("to_account"));
        transfer.setFromAccountId(sqlRowSet.getInt("from_account"));
        transfer.setTransferAmount(sqlRowSet.getBigDecimal("transfer_amount"));
        transfer.setTransferStatus(sqlRowSet.getString("transfer_state"));
        return transfer;
    }

}
