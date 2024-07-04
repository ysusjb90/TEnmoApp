package com.techelevator.tenmo;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.UserCredentials;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.ConsoleService;
import com.techelevator.tenmo.services.TenmoService;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

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
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                tenmoService.viewCurrentBalance();
            } else if (menuSelection == 2) {
                Transfer[] allTransfers = tenmoService.getAllTransfers();
                String direction = "";
                for (Transfer t: allTransfers){
                    if (t.getAccountToID() == tenmoService.getAccount().getAccountID()){
                        direction = "From: ";
                    } else {
                        direction = "To: ";
                    }
                    consoleService.printTransferShort(t.getTransferID(),direction, "Test User", t.getAmount());
                }

                try {
                    int selection = consoleService.promptForInt(
                            "Please enter transfer ID to view details (0 to cancel): ");
                    if (selection == 0){
                        consoleService.invalidSelection("Cancelling.");
                    }else {
                        consoleService.printTransferDetails(tenmoService.getTransferByID(selection));
                    }
                } catch (NullPointerException np) {
                    consoleService.invalidSelection("Invalid Selection.");
                }

            } else if (menuSelection == 3) {
                System.out.println("-------------------------------------------\n" +
                        "Pending Transfers\n" +
                        "-------------------------------------------");
                Transfer[] pendingTransfers = tenmoService.getPendingTransfers();
                for (Transfer t : pendingTransfers){
                    System.out.println(t);
                };

                int selection = 0;
                Transfer transfer = null;

                    selection = consoleService.promptForInt("Please enter transfer ID to approve/reject (0 to cancel)");
                    transfer = tenmoService.getTransferByID(selection);
                    if (transfer!=null) {

                        System.out.println("1: Approve\n2: Reject\n0: Don't approve or reject\n------");
                        selection = consoleService.promptForMenuSelection("Please choose an option: ");
                        switch (selection) {
                            case 0:
                                consoleService.invalidSelection("Transfer not modified.");
                                break;
                            case 1:
                                transfer.setTransferStatusID(2);
                                break;
                            case 2:
                                transfer.setTransferStatusID(3);
                            default:
                                consoleService.invalidSelection("Invalid Selection.");
                                break;
                        }
                    }else {
                        consoleService.invalidSelection("Invalid Selection.");
                    }

            } else if (menuSelection == 4) {
                int userToID= consoleService.promptForInt(
                        "Enter ID of user you are sending to (0 to cancel): ");
                try {
                    BigDecimal amountToSend=consoleService.promptForBigDecimal("Enter amount: ");
                    tenmoService.sendBucks(userToID,amountToSend);
                } catch (ResponseStatusException rse) {
                    consoleService.invalidSelection("Invalid UserID");
                }

            } else if (menuSelection == 5) {
                consoleService.printAllUsers(tenmoService.getAllUsers());
                int userFromId = consoleService.promptForInt("Enter ID of user you are requesting from: ");
                BigDecimal amount = consoleService.promptForBigDecimal("Enter amount: ");
                tenmoService.requestBucks(userFromId, amount);
                
            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }



}
