package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.AuthenticatedUser;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;

public class TenmoService {
    private static final String API_BASE_URL = "http://localhost:8080/";
    private RestTemplate restTemplate;
    private AuthenticatedUser user;

    public TenmoService(AuthenticatedUser authenticatedUser) {
        this.user = authenticatedUser;
        this.restTemplate=new RestTemplate();
    }

    public HttpEntity createHttpEntity(AuthenticatedUser user){
        HttpHeaders header = new HttpHeaders();
        String token = user.getToken();
        header.setBearerAuth(token);
        header.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AuthenticatedUser> httpEntity= new HttpEntity<>(user,header);
        return httpEntity;
    }
    public void viewCurrentBalance() {
        String url = API_BASE_URL + "accounts";
        // TODO Auto-generated method stub
        HttpEntity accountEntity = createHttpEntity(user);
        ResponseEntity<Account> responseEntity =restTemplate.exchange(url, HttpMethod.GET, accountEntity, Account.class);
        Account currentAccount = responseEntity.getBody();
        BigDecimal currentBalance = currentAccount.getBalance();
        System.out.println(currentBalance);

    }

    public void viewTransferHistory() {
        // TODO Auto-generated method stub

    }

    public void viewPendingRequests() {
        // TODO Auto-generated method stub

    }

    public void sendBucks() {
        // TODO Auto-generated method stub

    }

    public void requestBucks() {
        // TODO Auto-generated method stub

    }

}
