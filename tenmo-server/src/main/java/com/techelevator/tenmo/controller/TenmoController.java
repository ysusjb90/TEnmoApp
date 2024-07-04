package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.AccountDAO;
import com.techelevator.tenmo.dao.TransferDAO;
import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.exception.DaoException;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
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
    @RequestMapping(path = "send", method = RequestMethod.POST)
    public Transfer sendMoney(@RequestBody Map<String, String> request, Principal user) {
    Transfer transfer = null;
    int userToID = Integer.parseInt(request.get("userToId"));
    BigDecimal amountToSend = new BigDecimal(request.get("amount")) ;
        try {
            Account accountFrom = accountDAO.getAccountByUserID(
                    userDao.getUserByUsername(user.getName()).getId());
            Account accountTo = accountDAO.getAccountByUserID(userToID);
            transfer = new Transfer();
                transfer.setAccountFromID(accountFrom.getAccountID());
                transfer.setAccountToID(accountTo.getAccountID());
                transfer.setAmount(amountToSend);
                transfer.setTransferStatusID(2);
                transfer.setTransferTypeID(2);
            if (isValidTransfer(transfer)) {
                accountDAO.updateBalance(accountFrom, amountToSend.negate());
                accountDAO.updateBalance(accountTo, amountToSend);
            } else { throw new ResponseStatusException(HttpStatus.BAD_REQUEST);}
        } catch (NullPointerException np) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No results found.");
        }
    return transferDAO.createTransfer(transfer);
}
    @RequestMapping(path = "request", method = RequestMethod.POST)
    public Transfer requestMoney(@RequestBody Map<String, String> request, Principal user) {
        Transfer returnedTransfer = null;
        Transfer createdTransfer = null;
        Account usersAccount = accountDAO.getAccountByUserID(userDao.getUserByUsername(user.getName()).getId());
        Account userFromAccount = accountDAO.getAccountByUserID(Integer.parseInt(request.get("userFromId")));
        BigDecimal amount = new BigDecimal(request.get("amount"));
        if (usersAccount.getAccountID() != userFromAccount.getAccountID() &&
                amount.compareTo(BigDecimal.ZERO) == 1){
            createdTransfer = new Transfer();
            createdTransfer.setTransferTypeID(1);
            createdTransfer.setTransferStatusID(1);
            createdTransfer.setAccountFromID(userFromAccount.getAccountID());
            createdTransfer.setAccountToID(usersAccount.getAccountID());
            createdTransfer.setAmount(amount);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Can't request from your self." +
                    "Request must be greater than $0.00");
        }
        returnedTransfer =  transferDAO.createTransfer(createdTransfer);
        return returnedTransfer;
    }
    @RequestMapping(path = "account", method = RequestMethod.GET)
    public Account getAccount(Principal user){
        int userID = userDao.getUserByUsername(user.getName()).getId();
        Account account = accountDAO.getAccountByUserID(userID);
        return account;
    }
    @RequestMapping(path = "account/history", method = RequestMethod.GET)
    public Transfer[] accountHistory(Principal user){
        int userId = userDao.getUserByUsername(user.getName()).getId();
        List<Transfer> accountHistory = transferDAO.getTransfersByUserID(userId);
        return accountHistory.toArray(Transfer[]::new);
        
    }
    @RequestMapping(path = "account/transfers", method = RequestMethod.GET)
    public Transfer getTransfer(@RequestParam (name = "id") int transferId){
        Transfer transfer = null;
        transfer = transferDAO.getTransferByID(transferId);
        return transfer;
    }
    @RequestMapping(path= "account/transfers/pending", method =RequestMethod.GET)
    public Transfer[] pendingTransfers(Principal principal){
        User thisUser = userDao.getUserByUsername(principal.getName());
        List<Transfer> userTransfers = transferDAO.getTransfersByUserID(thisUser.getId());
        List<Transfer> pendingTransfers = new ArrayList<>();
        for (Transfer t: userTransfers){
            if (t.getTransferStatusID() == 1 &&
                    (t.getAccountFromID() == accountDAO.getAccountByUserID(thisUser.getId()).getAccountID()
                    || t.getAccountToID() == accountDAO.getAccountByUserID(thisUser.getId()).getAccountID()))
            {
                pendingTransfers.add(t);
            }
        }
        return pendingTransfers.toArray(Transfer[]::new);
    }
    @RequestMapping(path= "account/transfers/pending", method = RequestMethod.PUT)
    public Transfer modifyTransfer(@RequestBody Transfer transfer){
        Transfer updatedTransfer;
        updatedTransfer = transferDAO.updateTransfer(transfer);
        Account accountFrom = accountDAO.getAccountByID(transfer.getAccountFromID());
        Account accountTo = accountDAO.getAccountByID(transfer.getAccountToID());
        BigDecimal amountToSend = transfer.getAmount();

        if (transfer.getTransferStatusID() == 2 && isValidTransfer(transfer)){
            accountDAO.updateBalance(accountFrom, amountToSend.negate());
            accountDAO.updateBalance(accountTo, amountToSend);
        } else { throw new ResponseStatusException(HttpStatus.BAD_REQUEST);}
        return updatedTransfer;
    }
    @RequestMapping(path = "users", method = RequestMethod.GET)
    public User[] getUsers(){
        return userDao.getUsers().toArray(User[]::new);
    }

    private Boolean isValidTransfer(Transfer transferToCheck){
        Boolean valid = false;
        Account accountFrom = accountDAO.getAccountByID(transferToCheck.getAccountFromID());
        Account accountTo = accountDAO.getAccountByID(transferToCheck.getAccountToID());
        BigDecimal amountToSend = transferToCheck.getAmount();
        if (accountFrom.getUserID() != accountTo.getUserID() &&
                amountToSend.compareTo(BigDecimal.ZERO) == 1 &&
                accountFrom.getBalance().compareTo(amountToSend) == 1){
            valid = true;
        }
        return valid;
    }



}
