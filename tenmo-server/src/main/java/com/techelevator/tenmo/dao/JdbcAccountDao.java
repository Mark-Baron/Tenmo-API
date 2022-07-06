package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcAccountDao implements AccountDao {

    private JdbcTemplate jdbcTemplate;

    public JdbcAccountDao(JdbcTemplate jdbcTemplate) {this.jdbcTemplate = jdbcTemplate;}

    @Override
    public List<Account> findAll() {
        List<Account> accounts = new ArrayList<>();
        String sql = "select account_id, user_id, balance from account;";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while(results.next()) {
            Account account = mapRowToAccount(results);
            accounts.add(account);
        }
        return accounts;
    }

    @Override
    public Account findByAccountId(int accountId) {
        String sql = "select account_id, user_id, balance from account where account_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountId);
        if(results.next()) {
            return mapRowToAccount(results);
        }
        //custom exception
        return null;
    }

    @Override
    public Account findByUserId(int userId) {
        String sql = "select account_id, user_id, balance from account where user_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
        if(results.next()) {
            return mapRowToAccount(results);
        }
        //custom exception
        return null;
    }

    @Override
    public boolean create(int userId) {
        String sql = "insert into account(user_id) values(?)";
        try {
            jdbcTemplate.update(sql, userId);
        } catch (DataAccessException e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean update(int accountId, Account account) {
        String sql = "update account set balance = ? where account_id = ?";
        try {
            return jdbcTemplate.update(sql, account.getBalance(), accountId) == 1;
        } catch (DataAccessException e) {
            return false;
        }
    }

    @Override
    public boolean delete(int accountId) {
        String sql = "delete from account where account_id = ?";
        return jdbcTemplate.update(sql, accountId) == 1;
    }

    private Account mapRowToAccount(SqlRowSet sqlRowSet) {
        Account account = new Account();
        account.setAccount_id(sqlRowSet.getInt("account_id"));
        account.setUser_id(sqlRowSet.getInt("user_id"));
        account.setBalance(sqlRowSet.getBigDecimal("balance"));
        return account;
    }

}
