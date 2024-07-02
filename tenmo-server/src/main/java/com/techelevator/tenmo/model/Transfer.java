package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class Transfer {
    private int transferID;
    private int transferTypeID;
    private int transferStatusID;
    private int accountFromID;
    private int accountToID;
    private BigDecimal amountToTransfer;

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

    public BigDecimal getAmountToTransfer() {
        return amountToTransfer;
    }

    public void setAmountToTransfer(BigDecimal amountToTransfer) {
        this.amountToTransfer = amountToTransfer;
    }
    public Transfer(){
        }

    public Transfer(int transferID, int transferTypeID, int transferStatusID, int accountFromID, int accountToID, BigDecimal amountToTransfer) {
        this.transferID = transferID;
        this.transferTypeID = transferTypeID;
        this.transferStatusID = transferStatusID;
        this.accountFromID = accountFromID;
        this.accountToID = accountToID;
        this.amountToTransfer = amountToTransfer;
    }

}
