package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.math.BigDecimal;

public interface TransferDAO {
    Transfer sendMoney(Account accountTo, BigDecimal amountToSend);
    Transfer requestMoney(Account accountFrom, BigDecimal amountRequested);
    Transfer updateTransfers();
    Transfer getTransferByID(int transferID);
    Transfer getAllTransfers();
    String getTransferTypeDescription(int transferTypeID);
    String getTransferStatusDescription(int transferStatusID);
}
