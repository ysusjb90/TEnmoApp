package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDAO;
import com.techelevator.tenmo.dao.TransferDAO;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.http.HttpMethod;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
public class TenmoController {
    private TransferDAO transferDAO;
    private AccountDAO accountDAO;

    public TenmoController(TransferDAO transferDAO, AccountDAO accountDao) {
        this.transferDAO = transferDAO;
        this.accountDAO = accountDao;
    }

    public Transfer sendMoney(Account accountTo, BigDecimal amountToSend) {
        return null;
    }

    public Transfer requestMoney(Account accountFrom, BigDecimal amountRequested) {
        return null;
    }

    @RequestMapping(path = "accounts", method = RequestMethod.GET)
    public Account getAccount(User user){
        int userID = user.getId();
        Account account = accountDAO.getAccountByID(userID);
        return account;
    }

}
