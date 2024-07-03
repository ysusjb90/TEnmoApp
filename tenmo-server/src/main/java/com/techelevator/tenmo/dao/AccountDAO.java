package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;

import java.math.BigDecimal;
import java.util.List;

public interface AccountDAO {
    Account getAccountByUserID(int userID);
    Account getAccountByID(int accountID);
    Account updateBalance(Account userAccount, BigDecimal amount);


}
