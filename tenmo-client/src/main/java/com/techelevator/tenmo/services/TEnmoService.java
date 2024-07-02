package com.techelevator.tenmo.services;

import com.techelevator.tenmo.App;
import com.techelevator.tenmo.model.Account;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;

public class TEnmoService extends App {
    private RestTemplate restTemplate;

    public TEnmoService() {

        super();
        this.restTemplate=new RestTemplate();
    }

    public HttpEntity createHttpEntity(){
        HttpHeaders header = new HttpHeaders();
        String token = super.currentUser.getToken();
        header.setBearerAuth(token);
        header.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Void> httpEntity= new HttpEntity<>(header);
        return httpEntity;
    }
    public void viewCurrentBalance() {
        String url = API_BASE_URL + "/accounts";
        // TODO Auto-generated method stub
        HttpEntity accountEntity = createHttpEntity();
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
