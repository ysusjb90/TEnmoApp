package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TenmoService {
    private static final String API_BASE_URL = "http://localhost:8080/";
    private RestTemplate restTemplate;
    private AuthenticatedUser user;
    ConsoleService consoleService;
    Account account;

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
    }

    public HttpHeaders createAuthHeader(){
        HttpHeaders header = new HttpHeaders();
        String token = user.getToken();
        header.setBearerAuth(token);
        header.setContentType(MediaType.APPLICATION_JSON);
        return header;
    }
    public void viewCurrentBalance() {
        String url = API_BASE_URL + "account";
        HttpHeaders header = createAuthHeader();
        HttpEntity accountEntity = new HttpEntity(header);
        ResponseEntity<Account> responseEntity =restTemplate.exchange(url, HttpMethod.GET, accountEntity, Account.class);
        Account currentAccount = responseEntity.getBody();
        BigDecimal currentBalance = currentAccount.getBalance();
        this.account = currentAccount;
        System.out.println(currentBalance);

    }

    public Transfer[] getAllTransfers() {
        HttpHeaders header = createAuthHeader();
        String url = API_BASE_URL + "account/history";
        HttpEntity<Void> entity = new HttpEntity<>(header);
        Transfer[] transfers =
                restTemplate.exchange(url, HttpMethod.GET, entity, Transfer[].class).getBody();
        return transfers;
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
        Transfer[] transfers = restTemplate.exchange(url, HttpMethod.GET, entity, Transfer[].class).getBody();
       return transfers;
    }

    public void sendBucks(int userToId, BigDecimal amount) {
        HttpHeaders header = createAuthHeader();
        HttpEntity<Void> entity = new HttpEntity<>(header);
        String url = API_BASE_URL + "send";
        Map<String, String> bodyMap = new HashMap<>();
        bodyMap.put("userToId", String.valueOf(userToId));
        bodyMap.put("amount", amount.toPlainString());
        HttpEntity<Map<String, String>> moneySend = new HttpEntity<>(bodyMap, header);
        Transfer sentTransfer = restTemplate.exchange(url, HttpMethod.POST, moneySend, Transfer.class).getBody();
        System.out.println(sentTransfer);
    }
    public void requestBucks(int userFromID, BigDecimal amount) {
        HttpHeaders header = createAuthHeader();
        String url = API_BASE_URL + "request";
        Map<String, String> bodyMap = new HashMap<>();
        bodyMap.put("userFromId", String.valueOf(userFromID));
        bodyMap.put("amount", amount.toPlainString());
        HttpEntity<Map<String, String>>  moneyRequest = new HttpEntity<>(bodyMap, header);
        Transfer requestedTransfer = restTemplate.exchange(url,HttpMethod.POST, moneyRequest, Transfer.class ).getBody();
        System.out.println(requestedTransfer);

    }
    public User[] getAllUsers() {
        HttpHeaders header = createAuthHeader();
        HttpEntity<Void> entity = new HttpEntity<>(header);
        String url = API_BASE_URL + "users";
        User[] users = restTemplate.exchange(url, HttpMethod.GET, entity, User[].class).getBody();
        return users;
    }
}
