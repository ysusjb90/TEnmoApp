package com.techelevator.tenmo.services;


import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserCredentials;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Scanner;

public class ConsoleService {

    private final Scanner scanner = new Scanner(System.in);

    public int promptForMenuSelection(String prompt) {
        int menuSelection;
        System.out.print(prompt);
        try {
            menuSelection = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            menuSelection = -1;
        }
        return menuSelection;
    }

    public void printGreeting() {
        System.out.println("*********************");
        System.out.println("* Welcome to TEnmo! *");
        System.out.println("*********************");
    }

    public void printLoginMenu() {
        System.out.println();
        System.out.println("1: Register");
        System.out.println("2: Login");
        System.out.println("0: Exit");
        System.out.println();
    }

    public void printMainMenu() {
        System.out.println();
        System.out.println("1: View your current balance");
        System.out.println("2: View your past transfers");
        System.out.println("3: View your pending requests");
        System.out.println("4: Send TE bucks");
        System.out.println("5: Request TE bucks");
        System.out.println("0: Exit");
        System.out.println();
    }

    public void printBalance(){
        System.out.println();
        System.out.print("Your current account balance is: ");
    }

    public UserCredentials promptForCredentials() {
        String username = promptForString("Username: ");
        String password = promptForString("Password: ");
        return new UserCredentials(username, password);
    }

    public String promptForString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    public int promptForInt(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }

    public BigDecimal promptForBigDecimal(String prompt) {
        System.out.print(prompt);
        while (true) {
            try {
                return new BigDecimal(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a decimal number.");
            }
        }
    }

    public void pause() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    public void printErrorMessage() {
        System.out.println("An error occurred. Check the log for details.");
    }
    public void printPendingTransfersBanner() {
        System.out.println("-------------------------------------------");
        System.out.println("Pending Transfers");
        System.out.println( "-------------------------------------------");
    }
    public void printTransferOverviewBanner(){
        System.out.println("-------------------------------------------");
        System.out.println("Transfers");
        System.out.println(String.format("%-10s %-10s\t\t %-10s", "ID", "From/To", "Amount"));
        System.out.println("-------------------------------------------");
    }
    public void printTransferDetails (Transfer transfer) {
        String transferFormat = "Id: %d\nFrom: %s\nTo: %s\nType: %s\nStatus: %s\nAmount: $%.2f";
        String transferDetails = String.format(transferFormat,
                transfer.getTransferID(), transfer.getUserFrom(), transfer.getUserTo(),
                transfer.transferType(),transfer.transferStatus(), transfer.getAmount());
        System.out.println("---------------------------");
        System.out.println("Transfer Details");
        System.out.println("---------------------------");
        System.out.println(transferDetails);
    }
    
    public void printAllUsers(Map<Integer,User> users, User thisUser){
        //TODO: Show all users except the user
        System.out.println("--------\nUsers\nID\tName\n--------");
        //for (User u : users){
        //    System.out.println(u.getId() + "\t" + u.getUsername());
        //}
        for (Map.Entry u: users.entrySet()){
            int userID = (int)u.getKey();
            User user = (User)u.getValue();
            if (!thisUser.getUsername().equals(user.getUsername())) {

                System.out.println(userID + "\t" + user.getUsername());
            }
        }
    }

    public void printTransferOverview(Transfer transferToPrint, User thisUser){
        String string = "%-10d %-7s %-10s $%.2f";
        String direction = "";
        String otherUser = "";
        if (transferToPrint.getUserFrom().equals(thisUser.getUsername())){
            direction = "To: ";
            otherUser = transferToPrint.getUserTo();
        } else {
            direction = "From: ";
            otherUser = transferToPrint.getUserFrom();
        }
        System.out.println(String.format(
                string, transferToPrint.getTransferID(), direction,otherUser, transferToPrint.getAmount()));
    }
    public void invalidSelection (String message){
        System.out.println();
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        System.out.println((message + " Returning to Main Menu.").toUpperCase());
        System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
    }

}
