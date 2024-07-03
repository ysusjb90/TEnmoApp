package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class TenmoService {
    private static final String API_BASE_URL = "http://localhost:8080/";
    private RestTemplate restTemplate;
    private AuthenticatedUser user;

    public TenmoService(AuthenticatedUser authenticatedUser) {
        this.user = authenticatedUser;
        this.restTemplate=new RestTemplate();
    }

    public HttpHeaders createAuthHeader(){
        HttpHeaders header = new HttpHeaders();
        String token = user.getToken();
        header.setBearerAuth(token);
        header.setContentType(MediaType.APPLICATION_JSON);
        return header;
    }
    public void viewCurrentBalance() {
        String url = API_BASE_URL + "accounts";
        // TODO Auto-generated method stub
        HttpHeaders header = createAuthHeader();
        HttpEntity accountEntity = new HttpEntity(header);
        ResponseEntity<Account> responseEntity =restTemplate.exchange(url, HttpMethod.GET, accountEntity, Account.class);
        Account currentAccount = responseEntity.getBody();
        BigDecimal currentBalance = currentAccount.getBalance();
        System.out.println(currentBalance);

    }

    public void viewTransferHistory() {
        HttpHeaders header = createAuthHeader();
        String url = API_BASE_URL + "accounts/history";
        HttpEntity<Void> entity = new HttpEntity<>(header);
        Transfer[] transfers =
                restTemplate.exchange(url, HttpMethod.GET, entity, Transfer[].class).getBody();
        for (Transfer t : transfers) {
            System.out.println(t);
        };
    }

    public void viewPendingRequests() {
        // TODO Auto-generated method stub

    }

    public void sendBucks(int userToId, BigDecimal amount) {
        // TODO Auto-generated method stub
        HttpHeaders header = createAuthHeader();
        HttpEntity<Void> entity = new HttpEntity<>(header);
        String url = API_BASE_URL + "accounts/";
        String send_url = url + "send";
        Map<String, String> bodyMap = new HashMap<>();
        bodyMap.put("userToId", String.valueOf(userToId));
        bodyMap.put("amount", amount.toPlainString());
        HttpEntity<Map<String, String>> moneyRequest = new HttpEntity<>(bodyMap, header);
        Transfer sentTransfer = restTemplate.exchange(send_url, HttpMethod.POST, moneyRequest, Transfer.class).getBody();
        System.out.println(sentTransfer);


    }

    public void requestBucks() {
        // TODO Auto-generated method stub

    }

    public Transfer getTransferByID(int transferID) {
        HttpHeaders header = createAuthHeader();
        String url = API_BASE_URL + "accounts/history";
        Transfer transfer = null;
        HttpEntity<Integer> entity = new HttpEntity<>(transferID,header);
        ResponseEntity<Transfer> responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, Transfer.class);

        return transfer;

    }
}
