package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.Transfer;

import java.util.List;

public interface TransferDAO {

    Transfer updateTransfer(Transfer transfer);
    Transfer getTransferByID(int transferID);
    List<Transfer> getTransfersByUserID(int userID);
    String getTransferTypeDescription(int transferTypeID);
    String getTransferStatusDescription(int transferStatusID);
    Transfer createTransfer (Transfer newTransfer);
}
