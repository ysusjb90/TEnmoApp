package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class TenmoService {
    private static final String API_BASE_URL = "http://localhost:8080/";
    private RestTemplate restTemplate;
    ConsoleService consoleService;
    Account account;
    Map<Integer,User> allUsers;
    public Account getAccount() {
        return account;
    }
    private AuthenticatedUser user;

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
    public Transfer[] transferHistory() {
        HttpHeaders header = createAuthHeader();
        String url = API_BASE_URL + "account/history";
        HttpEntity<Void> entity = new HttpEntity<>(header);
        Transfer[] allTransfers =
                restTemplate.exchange(url, HttpMethod.GET, entity, Transfer[].class).getBody();
        Map<Integer, Transfer> allTransfersMap = new HashMap<>();
        for (Transfer t : allTransfers){
            allTransfersMap.put(t.getTransferID(), t);
        }
        if (allTransfers.length == 0){
            consoleService.returnToMainMenu("No Transfers.");
        } else {
            consoleService.printTransferOverviewBanner();
            for (Transfer t : allTransfers) {
                consoleService.printTransferOverview(t, user.getUser());
            }
            int selection = consoleService.promptForInt(
                    "Please enter transfer ID to view details (0 to cancel): ");
            if (selection == 0) {
                consoleService.returnToMainMenu("Cancelling.");
            } else {
                if (allTransfersMap.containsKey(selection)) {
                    consoleService.printTransferDetails(allTransfersMap.get(selection));
                }
                else {
                    consoleService.returnToMainMenu("Invalid Transfer ID selected");
                }
            }
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
        Map<Integer, Transfer> transferMap = new HashMap<>();
        for (Transfer t :pendingTransfers){
            transferMap.put(t.getTransferID(), t);
            consoleService.printTransferOverview(t, user.getUser());
        }
        if (pendingTransfers.length == 0){
            consoleService.returnToMainMenu("No Pending Transfers. ");
        } else {
            int selection = 0;
            Transfer transfer = null;

            selection = consoleService.promptForInt("Please enter transfer ID to approve/reject (0 to cancel): ");
            if (selection == 0) {
                consoleService.returnToMainMenu("Cancelling");
            } else {
                if (transferMap.containsKey(selection)) {
                    transfer = transferMap.get(selection);
                }
                if (transfer != null) {
                    consoleService.modifyPendingTransferMenu();
                    selection = consoleService.promptForMenuSelection("Please choose an option: ");
                    Transfer modifiedTransfer = null;
                    try {
                        switch (selection) {
                            case 0:
                                consoleService.returnToMainMenu("Transfer not modified.");
                                break;
                            case 1:
                                transfer.setTransferStatusID(2);
                                modifiedTransfer = modifyTransfer(transfer);
                                if (modifiedTransfer != null) {
                                    consoleService.printTransferDetails(modifiedTransfer);
                                    consoleService.returnToMainMenu("Accepted Transfer.");
                                }
                                break;
                            case 2:
                                transfer.setTransferStatusID(3);
                                modifiedTransfer = modifyTransfer(transfer);
                                if (modifiedTransfer != null) {
                                    consoleService.printTransferDetails(modifiedTransfer);
                                    consoleService.returnToMainMenu("Rejected Transfer.");
                                }
                                break;
                            default:
                                consoleService.returnToMainMenu("Invalid Input.");
                        }

                    } catch (NullPointerException np) {
                    }

                } else {
                    consoleService.returnToMainMenu("Transfer not found.");
                }
            }
        }
       return pendingTransfers;
    }
    public Transfer modifyTransfer(Transfer modifiedTransfer){

        HttpHeaders header = createAuthHeader();
        String url = API_BASE_URL + "account/transfers/pending";
        HttpEntity<Transfer> entity = new HttpEntity<>(modifiedTransfer, header);
        Transfer updatedTransfer = null;
        try {
            updatedTransfer = restTemplate.exchange(url, HttpMethod.PUT, entity, Transfer.class).getBody();
        } catch (HttpClientErrorException hcex) {
            consoleService.returnToMainMenu("Transfer could not be approved.");
        }
        return updatedTransfer;
    }
    public Transfer sendBucks() {
        consoleService.printAllUsers(allUsers, this.user.getUser());
        Transfer sentTransfer = null;
        int userToId= consoleService.promptForInt(
                "Enter ID of user you are sending to (0 to cancel): ");
        if (userToId == 0){
            consoleService.returnToMainMenu("Cancelling.");
        }else {
            if (allUsers.containsKey(userToId)) {
                BigDecimal amount = consoleService.promptForBigDecimal("Enter amount: ");
                HttpHeaders header = createAuthHeader();
                HttpEntity<Void> entity = new HttpEntity<>(header);
                String url = API_BASE_URL + "send";
                Map<String, String> bodyMap = new HashMap<>();
                bodyMap.put("userToId", String.valueOf(userToId));
                bodyMap.put("amount", amount.toPlainString());
                HttpEntity<Map<String, String>> moneySend = new HttpEntity<>(bodyMap, header);
                try {
                    sentTransfer = restTemplate.exchange(url, HttpMethod.POST, moneySend, Transfer.class).getBody();
                    if (sentTransfer != null) {
                        consoleService.printTransferDetails(sentTransfer);
                    }
                } catch (HttpClientErrorException hcex) {
                    consoleService.returnToMainMenu("Invalid Amount");
                }
            } else {
                consoleService.returnToMainMenu("Invalid User ID Selected");
            }
        }
        return sentTransfer;
    }
    public Transfer requestBucks() {
        consoleService.printAllUsers(allUsers, this.user.getUser());
        Transfer requestedTransfer = null;
        int userFromID = consoleService.promptForInt("Enter ID of user you are requesting from (0 to cancel): ");
        if (userFromID != 0) {
            if (allUsers.containsKey(userFromID)) {
                BigDecimal amount = consoleService.promptForBigDecimal("Enter amount: ");
                HttpHeaders header = createAuthHeader();
                String url = API_BASE_URL + "request";
                Map<String, String> bodyMap = new HashMap<>();
                bodyMap.put("userFromId", String.valueOf(userFromID));
                bodyMap.put("amount", amount.toPlainString());
                HttpEntity<Map<String, String>> moneyRequest = new HttpEntity<>(bodyMap, header);
                try {
                    requestedTransfer = restTemplate.exchange(url, HttpMethod.POST, moneyRequest, Transfer.class).getBody();
                    consoleService.printTransferDetails(requestedTransfer);
                } catch (HttpClientErrorException hcex) {
                   consoleService.returnToMainMenu("You can't request from yourself and requests must be more than $0.00.\n");
                }
            } else {
                consoleService.returnToMainMenu("Invalid User ID Selected");
            }
        }

        return requestedTransfer;

    }
    public Map<Integer, User> getAllUsers() {
        HttpHeaders header = createAuthHeader();
        HttpEntity<Void> entity = new HttpEntity<>(header);
        String url = API_BASE_URL + "users";
        User[] users = restTemplate.exchange(url, HttpMethod.GET, entity, User[].class).getBody();
        Map<Integer,User> userMap = new HashMap<>();
        for (User u: users){
            userMap.put(u.getId(), u);
        }
        return userMap;
    }


}
