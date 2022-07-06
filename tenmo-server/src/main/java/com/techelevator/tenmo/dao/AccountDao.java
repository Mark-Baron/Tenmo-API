package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;

import java.math.BigDecimal;
import java.util.List;

public interface AccountDao {

    List<Account> findAll();

    Account findByAccountId(int accountId);

    Account findByUserId(int userId);

    Account findByUsername(String username);

    boolean create(Long userId);

    boolean update(int accountId, Account account);

    boolean delete(int accountId);



}
