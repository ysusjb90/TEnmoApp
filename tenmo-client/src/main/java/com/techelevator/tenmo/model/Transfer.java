package com.techelevator.tenmo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class Transfer {
    @JsonProperty("transfer_id")
    private int transferID;
    @JsonProperty("transfer_type_id")
    private int transferTypeID;
    @JsonProperty("transfer_status_id")
    private int transferStatusID;
    @JsonProperty("account_from")
    private int accountFromID;
    @JsonProperty("account_to")
    private int accountToID;
    @JsonProperty("amount")
    private BigDecimal amount;
    private String userTo = "";
    private String userFrom = "";
    public String transferType(){
        String transferType = "";
        switch (this.transferTypeID){
            case 1:
                transferType = "Request";
                break;
            case 2:
                transferType = "Send";
                break;
        }
        return transferType;
    }
    public String transferStatus(){
        String transferStatus = "";
        switch (this.transferStatusID){
            case 1:
                transferStatus = "Pending";
                break;
            case 2:
                transferStatus = "Approved";
                break;
            case 3:
                transferStatus = "Rejected";
                break;
        }
        return transferStatus;
    }





    public int getTransferID() {
        return transferID;
    }

    public void setTransferID(int transferID) {
        this.transferID = transferID;
    }

    public int getTransferTypeID() {
        return transferTypeID;
    }

    public void setTransferTypeID(int transferTypeID) {
        this.transferTypeID = transferTypeID;
    }

    public int getTransferStatusID() {
        return transferStatusID;
    }

    public void setTransferStatusID(int transferStatusID) {
        this.transferStatusID = transferStatusID;
    }

    public int getAccountFromID() {
        return accountFromID;
    }

    public void setAccountFromID(int accountFromID) {
        this.accountFromID = accountFromID;
    }

    public int getAccountToID() {
        return accountToID;
    }

    public void setAccountToID(int accountToID) {
        this.accountToID = accountToID;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getUserTo() {
        return userTo;
    }

    public void setUserTo(String userTo) {
        this.userTo = userTo;
    }

    public String getUserFrom() {
        return userFrom;
    }

    public void setUserFrom(String userFrom) {
        this.userFrom = userFrom;
    }

    public Transfer(){
    }

    public Transfer(int transferID, int transferTypeID, int transferStatusID, int accountFromID, int accountToID, BigDecimal amountToTransfer) {
        this.transferID = transferID;
        this.transferTypeID = transferTypeID;
        this.transferStatusID = transferStatusID;
        this.accountFromID = accountFromID;
        this.accountToID = accountToID;
        this.amount = amountToTransfer;
    }

    @Override
    public String toString(){
        String string = "%d\t %s\t%s\t$%f";
        String recipient = "test recipient";
        String toOrFrom = "test direction";
        return String.format(string,
                this.transferID, toOrFrom , recipient, this.amount);

    }

}
