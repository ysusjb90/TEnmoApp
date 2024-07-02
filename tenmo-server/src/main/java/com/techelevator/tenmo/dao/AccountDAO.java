package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.List;

public interface AccountDAO {
    List<Account> getAccountsByUserID();
    Account getAccountByID(int accountID);
    Account updateBalance(Account userAccount, BigDecimal amount);


}
