package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.http.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TenmoService {
    private static final String API_BASE_URL = "http://localhost:8080/";
    private RestTemplate restTemplate;
    ConsoleService consoleService;
    Account account;
    User[] allUsers;
    public Account getAccount() {
        return account;
    }

    public TenmoService(AuthenticatedUser authenticatedUser) {
        this.user = authenticatedUser;
        this.restTemplate=new RestTemplate();
        this.consoleService = new ConsoleService();
        String url = API_BASE_URL + "account";
        HttpHeaders header = createAuthHeader();
        HttpEntity accountEntity = new HttpEntity(header);
        ResponseEntity<Account> responseEntity =restTemplate.exchange(url, HttpMethod.GET, accountEntity, Account.class);
        this.account = responseEntity.getBody();
        this.allUsers = getAllUsers();
    }

    public HttpHeaders createAuthHeader(){
        HttpHeaders header = new HttpHeaders();
        String token = user.getToken();
        header.setBearerAuth(token);
        header.setContentType(MediaType.APPLICATION_JSON);
        return header;
    }

    public BigDecimal viewCurrentBalance() {
        String url = API_BASE_URL + "account";
        HttpHeaders header = createAuthHeader();
        HttpEntity accountEntity = new HttpEntity(header);
        ResponseEntity<Account> responseEntity =restTemplate.exchange(url, HttpMethod.GET, accountEntity, Account.class);
        Account currentAccount = responseEntity.getBody();
        BigDecimal currentBalance = currentAccount.getBalance();
        this.account = currentAccount;
        return currentBalance;

    }
    public Transfer[] getAllTransfers() {
        HttpHeaders header = createAuthHeader();
        String url = API_BASE_URL + "account/history";
        HttpEntity<Void> entity = new HttpEntity<>(header);
        Transfer[] allTransfers =
                restTemplate.exchange(url, HttpMethod.GET, entity, Transfer[].class).getBody();
        String direction = "";
        for (Transfer t: allTransfers){
            consoleService.printTransferShort(t, user.getUser());
        }
        try {
            int selection = consoleService.promptForInt(
                    "Please enter transfer ID to view details (0 to cancel): ");
            if (selection == 0){
                consoleService.invalidSelection("Cancelling.");
            }else {
                consoleService.printTransferDetails(getTransferByID(selection));
                consoleService.pause();
            }
        } catch (NullPointerException np) {
            consoleService.invalidSelection("Invalid Selection.");
        }
        return allTransfers;
    }

    public Transfer getTransferByID(int transferID) {
        HttpHeaders header = createAuthHeader();
        String url = API_BASE_URL + "account/transfers?id="+transferID;
        Transfer transfer = null;
        HttpEntity<Void> entity = new HttpEntity<>(header);
        transfer = restTemplate.exchange(url, HttpMethod.GET, entity, Transfer.class).getBody();
        return transfer;
    }
    public Transfer[] getPendingTransfers() {
        HttpHeaders header = createAuthHeader();
        String url = API_BASE_URL + "account/transfers/pending";
        HttpEntity entity = new HttpEntity(header);
        Transfer[] pendingTransfers = restTemplate.exchange(url, HttpMethod.GET, entity, Transfer[].class).getBody();
        for (Transfer t : pendingTransfers){
            consoleService.printTransferShort(t, user.getUser());
        };
        System.out.println();
        if (pendingTransfers.length == 0){
            System.out.println("No Pending Transfers");
        } else {
            int selection = 0;
            Transfer transfer = null;

            selection = consoleService.promptForInt("Please enter transfer ID to approve/reject (0 to cancel)");
            if (selection == 0) {
                consoleService.invalidSelection("Cancelling");
            } else {
                transfer = getTransferByID(selection);
                if (transfer != null) {
                    System.out.println("1: Approve\n2: Reject\n0: Don't approve or reject\n------");
                    selection = consoleService.promptForMenuSelection("Please choose an option: ");
                    Transfer modifiedTransfer = null;
                    switch (selection) {
                        case 0:
                            consoleService.invalidSelection("Transfer not modified.");
                        case 1:
                            transfer.setTransferStatusID(2);
                            modifiedTransfer = modifyTransfer(transfer);
                            System.out.print("Accepted Transfer ");
                            break;
                        case 2:
                            transfer.setTransferStatusID(3);
                            modifiedTransfer = modifyTransfer(transfer);
                            System.out.print("Rejected Transfer ");
                            break;
                        default:
                            consoleService.invalidSelection("Invalid Selection.");
                    }
                    consoleService.printTransferDetails(modifiedTransfer);

                } else {
                    consoleService.invalidSelection("Invalid Selection.");
                }
            }
        }
       return pendingTransfers;
    }
    public Transfer sendBucks() {
        consoleService.printAllUsers(allUsers);
        int userToId= consoleService.promptForInt(
                "Enter ID of user you are sending to (0 to cancel): ");
        BigDecimal amount=consoleService.promptForBigDecimal("Enter amount: ");
        Transfer sentTransfer = null;
        HttpHeaders header = createAuthHeader();
        HttpEntity<Void> entity = new HttpEntity<>(header);
        String url = API_BASE_URL + "send";
        Map<String, String> bodyMap = new HashMap<>();
        bodyMap.put("userToId", String.valueOf(userToId));
        bodyMap.put("amount", amount.toPlainString());
        HttpEntity<Map<String, String>> moneySend = new HttpEntity<>(bodyMap, header);
        try {
            sentTransfer = restTemplate.exchange(url, HttpMethod.POST, moneySend, Transfer.class).getBody();
            if (sentTransfer != null){
                consoleService.printTransferDetails(sentTransfer);
            }
        } catch (ResponseStatusException rse) {
            consoleService.invalidSelection("Invalid UserID or Amount");
        }
        return sentTransfer;
    }
    public Transfer requestBucks() {
        consoleService.printAllUsers(allUsers);
        int userFromID = consoleService.promptForInt("Enter ID of user you are requesting from: ");
        BigDecimal amount = consoleService.promptForBigDecimal("Enter amount: ");
        HttpHeaders header = createAuthHeader();
        String url = API_BASE_URL + "request";
        Map<String, String> bodyMap = new HashMap<>();
        bodyMap.put("userFromId", String.valueOf(userFromID));
        bodyMap.put("amount", amount.toPlainString());
        HttpEntity<Map<String, String>>  moneyRequest = new HttpEntity<>(bodyMap, header);
        Transfer requestedTransfer = null;

        try {
            requestedTransfer = restTemplate.exchange(url, HttpMethod.POST, moneyRequest, Transfer.class ).getBody();
        } catch (RestClientException e) {
            System.out.println("You can't request from yourself and requests must be more than $0.00");
        } if (requestedTransfer != null){
            consoleService.printTransferDetails(requestedTransfer);
        }
        return requestedTransfer;

    }
    public User[] getAllUsers() {
        HttpHeaders header = createAuthHeader();
        HttpEntity<Void> entity = new HttpEntity<>(header);
        String url = API_BASE_URL + "users";
        User[] users = restTemplate.exchange(url, HttpMethod.GET, entity, User[].class).getBody();
        return users;
    }

    public Transfer modifyTransfer(Transfer modifiedTransfer){
        HttpHeaders header = createAuthHeader();
        String url = API_BASE_URL + "account/transfers/pending";
        HttpEntity<Transfer> entity = new HttpEntity<>(modifiedTransfer, header);
        Transfer updatedTransfer = restTemplate.exchange(url, HttpMethod.PUT, entity, Transfer.class).getBody();
        return updatedTransfer;
    }
    private AuthenticatedUser user;
}
