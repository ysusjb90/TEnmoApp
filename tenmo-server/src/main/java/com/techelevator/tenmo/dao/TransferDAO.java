package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import java.math.BigDecimal;
import java.util.List;

public interface TransferDAO {

    Transfer updateTransfer(Transfer transfer);
    Transfer getTransferByID(int transferID);
    List<Transfer> getAllTransfers();
    String getTransferTypeDescription(int transferTypeID);
    String getTransferStatusDescription(int transferStatusID);
    Transfer createTransfer (Transfer newTransfer);
}
