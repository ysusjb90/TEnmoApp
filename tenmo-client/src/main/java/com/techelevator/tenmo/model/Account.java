package com.techelevator.tenmo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class Account{
    @JsonProperty("account_id")
    private int accountID;
    @JsonProperty("user_id")
    private int userID;
    private BigDecimal balance;

    public int getAccountID() {
        return accountID;
    }

    public void setAccountID(int accountID) {
        this.accountID = accountID;
    }

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
    public Account(){
    }

    public Account(int accountID, int userID, BigDecimal balance) {
        this.accountID = accountID;
        this.userID = userID;
        this.balance = balance;
    }
}
