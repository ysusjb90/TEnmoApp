package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Account;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcAccountDao implements AccountDAO {
    JdbcTemplate jdbcTemplate;


    public JdbcAccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Account getAccountByUserID(int userID) {
        List<Account> accounts = new ArrayList<>();
        Account account = null;
        String sql = "SELECT account_id, user_id, balance FROM account WHERE user_id = ?";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql,userID);
            if (results.next()){
                account = mapRowToAccount(results);
            }
            //while(results.next()){
            //    Account thisAccount = mapRowToAccount(results);
            //    accounts.add(thisAccount);
            //}
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Its a phantom.", e);
        }catch (DataIntegrityViolationException e) {
            throw new DaoException("Bad Syntax or somethin.", e);
        }
        return account;
    }

    @Override
    public Account getAccountByID(int accountID) {
        Account account = null;
        String sql = "SELECT account_id, user_id, balance FROM account WHERE account_id = ?";
        try {
            SqlRowSet result = jdbcTemplate.queryForRowSet(sql,accountID);
            if(result.next()){
                account = mapRowToAccount(result);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Its a phantom.", e);
        }catch (DataIntegrityViolationException e) {
            throw new DaoException("Bad Syntax or somethin.", e);
        }
        return account;
    }

    @Override
    public Account updateBalance(Account userAccount, BigDecimal amount) {
        String sql = "UPDATE account SET balance = ? WHERE account_id = ?";
        Account account = null;
        BigDecimal newBalance = null;
            newBalance = userAccount.getBalance().add(amount);
            int accountID = userAccount.getAccountID();
            try {
                int numRowsupdated = jdbcTemplate.update(sql, newBalance, accountID);
                if (numRowsupdated == 0) {
                    throw new DaoException("No rows updated");
                }
                account = this.getAccountByID(userAccount.getAccountID());
            } catch (CannotGetJdbcConnectionException e) {
                throw new DaoException("Its a phantom.", e);
            } catch (DataIntegrityViolationException e) {
                throw new DaoException("Bad Syntax or somethin.", e);
            }
        return account;
    }

    public Account mapRowToAccount(SqlRowSet accountRow){
        Account account = new Account(
                accountRow.getInt("account_id"),
                accountRow.getInt("user_id"),
                accountRow.getBigDecimal("balance")
        );
        return account;
    }
}
