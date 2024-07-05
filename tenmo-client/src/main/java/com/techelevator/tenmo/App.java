package com.techelevator.tenmo;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.UserCredentials;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.ConsoleService;
import com.techelevator.tenmo.services.TenmoService;

public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";

    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);

    private AuthenticatedUser currentUser;
    private TenmoService tenmoService;

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void run() {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
            mainMenu();
        }
    }
    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }

    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        if (currentUser == null) {
            consoleService.printErrorMessage();
        }
    }

    private void mainMenu() {
        tenmoService = new TenmoService(currentUser);
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu(currentUser.getUser().getUsername(), tenmoService.viewCurrentBalance());
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                consoleService.printBalance();
                System.out.println(String.format("$%.2f",tenmoService.viewCurrentBalance()));
            } else if (menuSelection == 2) {
                Transfer[] allTransfers = tenmoService.transferHistory();
                continue;
            } else if (menuSelection == 3) {
                consoleService.printPendingTransfersBanner();
                Transfer[] pendingTransfers = tenmoService.getPendingTransfers();
                continue;
            } else if (menuSelection == 4) {
                tenmoService.sendBucks();
                continue;
            } else if (menuSelection == 5) {
               tenmoService.requestBucks();
               continue;
            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
                continue;
            }
            consoleService.pause();
        }
    }




}
