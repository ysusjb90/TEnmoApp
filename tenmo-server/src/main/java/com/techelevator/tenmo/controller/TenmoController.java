package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDAO;
import com.techelevator.tenmo.dao.TransferDAO;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;

@PreAuthorize("isAuthenticated()")
@RestController
public class TenmoController {
    private UserDao userDao;
    private TransferDAO transferDAO;
    private AccountDAO accountDAO;
    private User loggedInUser;

    public TenmoController(TransferDAO transferDAO, AccountDAO accountDao, UserDao userDao, Principal user, User loggedInUser) {

        this.transferDAO = transferDAO;
        this.accountDAO = accountDao;
        this.userDao = userDao;
        this.loggedInUser = userDao.getUserByUsername(user.getName());

    }
@RequestMapping(path = "accounts/send", method = RequestMethod.POST)
    public Transfer sendMoney(Account accountTo, BigDecimal amountToSend) {
    Transfer transfer = null;
    Account accountFrom = accountDAO.getAccountByUserID(loggedInUser.getId());
    if (amountToSend.compareTo(BigDecimal.ZERO) == 1 &&
            accountFrom.getBalance().compareTo(amountToSend) == 1
            && accountFrom.getUserID() != accountTo.getUserID()
    ) {
        transfer = new Transfer();
        transfer.setAccountFromID(accountFrom.getAccountID());
        transfer.setAccountToID(accountTo.getAccountID());
        transfer.setAmountToTransfer(amountToSend);
        transfer.setTransferStatusID(1);
        transfer.setTransferTypeID(2);

    }
    return transferDAO.createTransfer(transfer);
}

    public Transfer requestMoney(Account accountFrom, BigDecimal amountRequested) {
        return null;
    }

    @RequestMapping(path = "accounts", method = RequestMethod.GET)
    public Account getAccount(Principal user){
        int userID = loggedInUser.getId();
        Account account = accountDAO.getAccountByUserID(userID);
        return account;
    }

}
