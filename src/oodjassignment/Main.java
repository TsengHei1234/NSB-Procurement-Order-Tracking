/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodjassignment;

import java.util.Scanner;

public class Main {
    public static void main(String[] args){
        while(true){
            Scanner sc = new Scanner(System.in);
            System.out.println("\n\nNSB PROCUREMENT ORDER TRACKING SYSTEM");
            System.out.println("1.Login\n2.Close programme\nSelect your option: ");
            String option = sc.nextLine();
            switch(option){
                case "1" -> {
                    Users users = new Users();
                    Users currentUser = users.login();
                    if(currentUser == null){
                        System.out.println("Login failed.");
                    }
                    else if (!currentUser.getRole().equals("purchaseManager") && !currentUser.getRole().equals("admin")){
                        System.out.println("Access denied. This submission contains the Purchase Manager module.");
                    }
                    else{
                        Notification n = new Notification();
                        n.reorderNotification();
                        PurchaseManager pm = new PurchaseManager(currentUser);
                        pm.PurchaseManagerPage();
                    }
                }
                case "2" -> {
                    System.exit(0);
                }
                default -> {
                    System.out.println("Invalid input. Please try again.");
                }
            }
        }
    }
}
