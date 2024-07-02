package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcTransferDao implements TransferDAO {
   JdbcTemplate jdbcTemplate;

    public JdbcTransferDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Transfer updateTransfer(Transfer transfer) {
        Transfer updatedTransfer = null;
        String sql = "UPDATE transfer " +
                "SET transfer_type_id = ?, transfer_status_id = ?," +
                " account_from = ?,account_to = ?, amount = ?" +
                " WHERE transfer_id = ?";
        try {
            int numRowsUpdated = jdbcTemplate.update(sql,transfer.getTransferTypeID(), transfer.getTransferStatusID(),
                    transfer.getAccountFromID(), transfer.getAccountToID(), transfer.getAmountToTransfer());
            if (numRowsUpdated == 0){
                throw new DaoException("No Rows Were Updated, Invalid Transfer");
            }
            updatedTransfer = getTransferByID(transfer.getTransferID());
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Its a phantom.", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Bad Syntax or somethin.", e);
        }
        return updatedTransfer;
    }

    @Override
    public Transfer getTransferByID(int transferID) {
        Transfer transfer = null;
        String sql = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount " +
                "FROM transfer WHERE transfer_id = ?";
        try {
            SqlRowSet result = jdbcTemplate.queryForRowSet(sql, transferID);
            if (result.next()) {
                transfer = mapRowToTransfer(result);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Its a phantom.", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Bad Syntax or somethin.", e);
        }
        return transfer;
    }

    @Override
    public List<Transfer> getAllTransfers() {
        List<Transfer> allTransfers = new ArrayList<>();
        String sql = "SELECT transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount ";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
            while (results.next()){
                allTransfers.add(mapRowToTransfer(results));
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Its a phantom.", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Bad Syntax or somethin.", e);
        }
        return allTransfers;
    }

    @Override
    public String getTransferTypeDescription(int transferTypeID) {
        String description = null;
        String sql = "SELECT transfer_type_desc from transfer_type " +
                "WHERE transfer_type_id = ?";
    SqlRowSet result = jdbcTemplate.queryForRowSet(sql, transferTypeID);
    if (result.next()){
        description = result.getString("transfer_type_desc");
    }
        return description;
    }

    @Override
    public String getTransferStatusDescription(int transferStatusID) {
        String description = null;
        String sql = "SELECT transfer_status_desc from transfer_status " +
                "WHERE transfer_status_id = ?";
        SqlRowSet result = jdbcTemplate.queryForRowSet(sql, transferStatusID);
        if (result.next()){
            description = result.getString("transfer_status_desc");
        }
        return description;
    }

    public Transfer mapRowToTransfer(SqlRowSet result){
        Transfer transfer = new Transfer();
        transfer.setTransferID(result.getInt("transfer_id"));
        transfer.setTransferTypeID(result.getInt("transfer_type_id"));
        transfer.setTransferStatusID(result.getInt("transfer_status_id"));
        transfer.setAccountFromID(result.getInt("account_from"));
        transfer.setAccountToID(result.getInt("account_to"));
        transfer.setAmountToTransfer(result.getBigDecimal("amount"));
        return transfer;
    }
}
