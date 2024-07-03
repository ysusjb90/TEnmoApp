package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDAO;
import com.techelevator.tenmo.dao.TransferDAO;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;
import java.util.Map;

@PreAuthorize("isAuthenticated()")
@RestController
public class TenmoController {
    private UserDao userDao;
    private TransferDAO transferDAO;
    private AccountDAO accountDAO;



    public TenmoController(TransferDAO transferDAO, AccountDAO accountDao, UserDao userDao) {

        this.transferDAO = transferDAO;
        this.accountDAO = accountDao;
        this.userDao = userDao;


    }
    @RequestMapping(path = "accounts/send", method = RequestMethod.POST)
    public Transfer sendMoney(@RequestBody Map<String, String> request, Principal user) {
    Transfer transfer = null;
    int userToID = Integer.parseInt(request.get("userToId"));
    BigDecimal amountToSend = new BigDecimal(request.get("amount")) ;
    Account accountFrom = accountDAO.getAccountByUserID(
            userDao.getUserByUsername(user.getName()).getId());
    Account accountTo = accountDAO.getAccountByUserID(userToID);
    if (amountToSend.compareTo(BigDecimal.ZERO) == 1 &&
            accountFrom.getBalance().compareTo(amountToSend) == 1
            && accountFrom.getUserID() != accountTo.getUserID()
    ) {
        transfer = new Transfer();
        transfer.setAccountFromID(accountFrom.getAccountID());
        transfer.setAccountToID(accountTo.getAccountID());
        transfer.setAmount(amountToSend);
        transfer.setTransferStatusID(1);
        transfer.setTransferTypeID(2);
    }
    accountDAO.updateBalance(accountFrom, amountToSend.negate());
    accountDAO.updateBalance(accountTo, amountToSend);
    return transferDAO.createTransfer(transfer);
}

    @RequestMapping(path = "accounts", method = RequestMethod.GET)
    public Account getAccount(Principal user){
        int userID = userDao.getUserByUsername(user.getName()).getId();
        Account account = accountDAO.getAccountByUserID(userID);
        return account;
    }

    @RequestMapping(path = "accounts/history", method = RequestMethod.GET)
    public Transfer[] accountHistory(Principal user){
        int userId = userDao.getUserByUsername(user.getName()).getId();
        List<Transfer> accountHistory = transferDAO.getTransfersByUserID(userId);
        return accountHistory.toArray(Transfer[]::new);
    }
    @RequestMapping(path = "accounts/history?id={transferId}", method = RequestMethod.GET)
    public Transfer getTransfer(@PathVariable int transferId){
        Transfer transfer = null;
        transferDAO.getTransferByID(transferId);
        return transfer;
    }

    public Transfer requestMoney(Account accountFrom, BigDecimal amountRequested) {
        return null;
    }


}
