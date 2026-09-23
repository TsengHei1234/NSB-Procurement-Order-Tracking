/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oodjassignment;

import java.util.List;
import java.util.Scanner;

public class Users {
    protected String userID,username,email,password,role,name,otp;

    protected String getUserID(){
        return userID;
    }
    protected String getUsername(){
        return username;
    }
    private String getEmail(){
        return email;
    }
    protected String getPassword(){
        return password;
    }
    protected String getRole(){
        return role;
    }
    protected String getName(){
        return name;
    }
    protected String getOTP(){
        return otp;
    }

    public Users(){}
    public Users(String userID, String username, String email, String password, String role, String name, String otp){
        this.userID = userID;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.name = name;
        this.otp = otp;
    }

    public Users login(){
        Scanner sc = new Scanner(System.in);
        System.out.println("Username/Email: ");
        String usernameEmailEntered = sc.nextLine();
        System.out.println("Password: ");
        String passwordEntered = sc.nextLine();

        FileHandler fh = new FileHandler();
        List<Users> usersList = fh.readUsers();
        for(Users user : usersList){
            if((usernameEmailEntered.equals(user.getUsername()) || usernameEmailEntered.equals(user.getEmail())) && passwordEntered.equals(user.getPassword())){
                return user;
            }
        }
        return null;
    }
}
