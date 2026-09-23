package oodjassignment;

import java.io.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class PurchaseManager extends Users{
    public PurchaseManager(Users currentUser){
        this.userID = currentUser.userID;
        this.username = currentUser.username;
        this.email = currentUser.email;
        this.password = currentUser.password;
        this.role = currentUser.role;
        this.name = currentUser.name;
    }
    
    public void pauseConsole(int time){
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public String currentDateTime(){
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        String formattedDateTime = now.format(formatter);
        return formattedDateTime;
    }
    
    public String provideID(String frontChar, String fileName){
        int maxID = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))){
            String line;
            while ((line = br.readLine()) != null){
                String[] fields = line.split(",");
                if (fields.length > 0 && fields[0].startsWith(frontChar)){
                    String numberID = fields[0].substring(frontChar.length());
                    try{
                        int numInFile = Integer.parseInt(numberID);
                        if (numInFile > maxID){
                            maxID = numInFile;
                        }
                    }catch(NumberFormatException e){System.out.println("Invalid ID format: " + fields[0]);}
                }
            }
        }catch(IOException e){System.out.println("Error reading file: " + e.getMessage());}
        
        int nextNum = maxID + 1;
        String formattedID;
        
        if (nextNum < 10){
            formattedID = frontChar+"00"+nextNum;
        }
        else if (nextNum < 100){
            formattedID = frontChar+"0"+nextNum;
        }
        else{
            formattedID = frontChar+nextNum;
        }
        
        return formattedID;
    }
    
    public boolean withinTwoHours(String dateTime){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDateTime inputDateTime = LocalDateTime.parse(dateTime, formatter);
        LocalDateTime currentDateTime = LocalDateTime.now();
        long hoursDifference = ChronoUnit.HOURS.between(currentDateTime, inputDateTime);
        return Math.abs(hoursDifference) <= 2;
    }
    
    public boolean afterTwoHours(String dateTime){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        LocalDateTime inputDateTime = LocalDateTime.parse(dateTime, formatter);
        LocalDateTime currentDateTime = LocalDateTime.now();
        long hoursDifference = ChronoUnit.HOURS.between(currentDateTime, inputDateTime);
        return Math.abs(hoursDifference) >= 2;
    }

    public void PurchaseManagerPage(){
        Scanner sc = new Scanner(System.in);
        boolean exitPMPage = false;
        while (!exitPMPage){
            System.out.println("\n     Welcome back, " + this.username + "!");
            System.out.println("------------------------------\n     Purchase Manager Page\n------------------------------");
            System.out.println("1.List of Items (View)\n2.List of Suppliers (View)\n3.Create and View Requisition created by SM\n4.View List and Generate Purchase Order\n5.Log Out");
            boolean chooseOption = false;
            while (!chooseOption){
                System.out.print("Enter your option: ");
                String option = sc.nextLine();
                switch (option){
                    case "1" ->{
                        chooseOption = true;
                        ListOfItem();
                        break;
                    }
                    case "2" ->{
                        chooseOption = true;
                        listOfSuppliers();
                        break;
                    }
                    case "3" ->{
                        chooseOption = true;
                        createViewPurchaseRequisition();
                        break;
                    }
                    case "4" ->{
                        chooseOption = true;
                        createViewPurchaseOrder();
                        break;
                    }
                    case "5" ->{
                        chooseOption = true;
                        exitPMPage = true;
                        System.out.println(this.username + ", you have logged out successfully!");
                        break;
                    }
                    default ->{
                        System.out.println("Invalid input option, please re-input.");
                    }
               }
            }
        }
    }
    
    public void ListOfItem(){
        Scanner sc = new Scanner(System.in);
        Filter f = new Filter();
        boolean exitItemPage = false;
        String filterItemID = "All", filterItemName = "All", filterItemSellingPrice = "All", filterstocklevel = "All";
        
        while (!exitItemPage){
            displayItem(false, filterItemID, filterItemName, filterItemSellingPrice, filterstocklevel);
            System.out.println("\n1.Edit filter\n2.Return to PM Page");
            boolean chooseOption = false;
            while (!chooseOption){
                System.out.print("Enter your option: ");
                String option = sc.nextLine();
                switch (option){
                    case "1":{
                        ArrayList<String> filters = f.itemPageFilterEdit(filterItemID, filterItemName, filterItemSellingPrice, filterstocklevel);
                        filterItemID = filters.get(0);
                        filterItemName = filters.get(1);
                        filterItemSellingPrice = filters.get(2);
                        filterstocklevel = filters.get(3);
                        chooseOption = true;
                        break;
                    }
                    case "2":{
                        chooseOption = true;
                        exitItemPage = true;
                        break;
                    }
                    default :{
                        System.out.println("Invalid input option, please re-input.");
                    }
                }
            }
        }   
    }
    
    public boolean displayItem(boolean checkItem, String filterItemID, String filterItemName, String filterItemSellingPrice, String filterstocklevel){
        FileHandler fh = new FileHandler();
        ArrayList<String[]> itemList = fh.readFile("item.txt");
        
        if (!checkItem){
            System.out.println("\nCurrent Filter: Item ID: "+filterItemID+" | Item Name: "+filterItemName+" | Item Selling Price: "+filterItemSellingPrice+" | Stock Level: "+filterstocklevel);
            System.out.println("----------------------------------------- List of Items --------------------------------------------");
            System.out.println("| Item ID    | Item Name          | Item Selling Price | Stock Level | Reorder Level | Updated Date |");
            System.out.println("----------------------------------------------------------------------------------------------------");
        }

        boolean recordFound = false;
        for (String[] item : itemList){
            String itemID = item[0];
            String itemName = item[1];
            String itemSellingPrice = item[2];
            String stockLevel = item[3];
            boolean matchesID = filterItemID.equals("All") || itemID.equalsIgnoreCase(filterItemID);            
            boolean matchesName = filterItemName.equals("All") || itemName.equalsIgnoreCase(filterItemName);
            boolean matchesSellingPrice = filterItemSellingPrice.equals("All") || itemSellingPrice.equalsIgnoreCase(filterItemSellingPrice);
            boolean matchesStockLevel = filterstocklevel.equals("All") || stockLevel.equalsIgnoreCase(filterstocklevel);
            if (!checkItem && matchesID && matchesName && matchesSellingPrice && matchesStockLevel){
                System.out.printf("| %-10s | %-18s | %-18s | %-11s | %-13s | %-12s |\n",itemID,itemName,itemSellingPrice,stockLevel,item[4],item[5]);    
                recordFound = true;
            }
            else if (checkItem && matchesID && matchesName && matchesSellingPrice && matchesStockLevel){
                System.out.println("Item Chosen:");   
                System.out.println("Item Code: "+item[0]+"\nItem Name: "+item[1]+"\nItem Selling Price: "+item[2]+"\nItem Current Stock Level: "+item[3]+"\nItem Reorder Level: "+item[4]+"\nItem Updated Date: "+item[5]); 
                recordFound = true;
                return true;
            }
        }
        if (!recordFound){
            System.out.println("Item is not found.");
            return false;
        }
        return false;
    }
    
    public void listOfSuppliers(){
        Scanner sc = new Scanner(System.in);
        Filter f = new Filter();
        boolean exitItemPage = false;
        String filterSupplierID = "All", filterSupplierName = "All", filterContactNumber = "All", filterAddress = "All";
        
        while (!exitItemPage){
            displaySuppliers(filterSupplierID, filterSupplierName, filterContactNumber, filterAddress);
            System.out.println("\n1.Edit supplier filter\n2.Return to PM Page");
            boolean chooseOption = false;
            while (!chooseOption){
                System.out.print("Enter your option: ");
                String option = sc.nextLine();
                switch (option){
                    case "1" ->{
                        ArrayList<String> filters = f.supplierPageFilterEdit(filterSupplierID, filterSupplierName, filterContactNumber, filterAddress);
                        filterSupplierID = filters.get(0);
                        filterSupplierName = filters.get(1);
                        filterContactNumber = filters.get(2);
                        filterAddress = filters.get(3);
                        chooseOption = true;
                        break;
                    }
                    case "2" ->{
                        chooseOption = true;
                        exitItemPage = true;
                        break;
                    }
                    default ->{
                        System.out.println("Invalid input option, please re-input.");
                    }
                }
            }
        }   
    }
    
    public boolean displaySuppliers(String filterSupplierID, String filterSupplierName, String filterContactNumber, String filterAddress){
        FileHandler fh = new FileHandler();
        ArrayList<String[]> supplierList = fh.readFile("supplier.txt");
        
        System.out.println("\nCurrent Filter: Supplier ID: "+filterSupplierID+" | Supplier Name: "+filterSupplierName+" | Contact Number: "+filterContactNumber+" | Address: "+filterAddress);
        System.out.println("-------------\nList of Items\n-------------");
        System.out.println("| Supplier ID | Supplier Name        | Contact Number | Address ");

        boolean recordFound = false;
        for (String[] supplier : supplierList){
            String supplierID = supplier[0];
            String supplierName = supplier[1];
            String contactNumber = supplier[2];
            String address = supplier[3];
            boolean matchesID = filterSupplierID.equals("All") || supplierID.equalsIgnoreCase(filterSupplierID);            
            boolean matchesName = filterSupplierName.equals("All") || supplierName.equalsIgnoreCase(filterSupplierName);
            boolean matchesContactNumber = filterContactNumber.equals("All") || contactNumber.equalsIgnoreCase(filterContactNumber);
            boolean matchesAddress = filterAddress.equals("All") || address.equalsIgnoreCase(filterAddress);
            
            if (matchesID && matchesName && matchesContactNumber && matchesAddress){
                System.out.printf("| %-11s | %-20s | %-14s | %s \n",supplierID, supplierName, contactNumber, address);    
                recordFound = true;
            }
        }
        if (!recordFound){
            System.out.println("Supplier List is not found.");
            return false;
        }
        return false;
    }
    
    public void createViewPurchaseRequisition(){
        Scanner sc = new Scanner(System.in);
        FileHandler fh = new FileHandler();
        Filter f = new Filter();
        String filterReqID = "All", filterSubmitterID = "All", filterSubmitterUsername = "All", filterApproverID = "All", filterApproverName = "All", filterSupplierID = "All", 
               filterSupplierName = "All", filterItemID = "All", filterItemName = "All", filterQuantity = "All", filterStatus = "All", filterDateTime = "XX-XX-XXXX";
        boolean exitPRPage = false;
        
        while (!exitPRPage){
            displayRequisition(false, false, filterReqID, filterSubmitterID, filterSubmitterUsername, filterApproverID, filterApproverName, filterSupplierID, filterSupplierName, filterItemID, filterItemName, filterQuantity, filterStatus, filterDateTime);
            System.out.println("\n1.Edit filter\n2.Validate Requisition created by SM\n3.Create New Requisition\n4.Delete Requisition (within 2hrs)\n5.Return to PM Page");
            boolean chooseOption = false;
            while(!chooseOption){
                System.out.print("Enter your option: ");
                String option = sc.nextLine();
                switch (option){
                    case "1":{
                        ArrayList<String> filter = f.requisitionPageFilterEdit("None", filterReqID, filterSubmitterID, filterSubmitterUsername, filterApproverID, filterApproverName, filterSupplierID, filterSupplierName, filterItemID, filterItemName, filterQuantity, filterStatus, filterDateTime);
                        filterReqID = filter.get(0);
                        filterSubmitterID = filter.get(1);
                        filterSubmitterUsername = filter.get(2);
                        filterApproverID = filter.get(3);
                        filterApproverName = filter.get(4);
                        filterSupplierID = filter.get(5);
                        filterSupplierName = filter.get(6);
                        filterItemID = filter.get(7);
                        filterItemName = filter.get(8);
                        filterQuantity = filter.get(9);
                        filterStatus = filter.get(10);
                        filterDateTime = filter.get(11);
                        chooseOption = true;
                        break;
                    }
                    case "2":{
                        boolean exitValidateReq = false;
                        while (!exitValidateReq){
                            ArrayList<String[]> purchaseRequisitionList = fh.readFile("purchaseRequisitions.txt");
                            displayRequisition(false, false, filterReqID, filterSubmitterID, filterSubmitterUsername, filterApproverID, filterApproverName, filterSupplierID, filterSupplierName, filterItemID, filterItemName, filterQuantity, "Pending", filterDateTime);
                            System.out.println("\n1.Edit filter\n2.Choose requisition created by SM to validate\n3.Return to Purchase Requisition Page");
                            boolean validateReq = false;
                            while(!validateReq){
                                System.out.print("Enter your option: ");
                                String optionValidate = sc.nextLine().trim();
                                switch (optionValidate){
                                    case "1"->{
                                        ArrayList<String> filter = f.requisitionPageFilterEdit("Validation", filterReqID, filterSubmitterID, filterSubmitterUsername, filterApproverID, filterApproverName, filterSupplierID, filterSupplierName, filterItemID, filterItemName, filterQuantity, "Pending", filterDateTime);
                                        filterReqID = filter.get(0);
                                        filterSubmitterID = filter.get(1);
                                        filterSubmitterUsername = filter.get(2);
                                        filterApproverID = filter.get(3);
                                        filterApproverName = filter.get(4);
                                        filterSupplierID = filter.get(5);
                                        filterSupplierName = filter.get(6);
                                        filterItemID = filter.get(7);
                                        filterItemName = filter.get(8);
                                        filterQuantity = filter.get(9);
                                        filterStatus = filter.get(10);
                                        filterDateTime = filter.get(11);
                                        validateReq = true;
                                        break;
                                    }
                                    case "2"->{
                                        System.out.println();
                                        boolean processValidate = false;
                                        while (!processValidate){
                                            System.out.print("Enter Requisition ID to validate (input Q to exit validation): ");
                                            String inputRequisitionID = sc.nextLine().trim();
                                            
                                            if(inputRequisitionID.equalsIgnoreCase("q")){
                                                System.out.println("Exit Validation successfully.");
                                                processValidate = true;
                                                validateReq = true;
                                            }
                                            else if (inputRequisitionID.isEmpty()) {
                                                System.out.println("Please enter a valid Item ID.");
                                            }
                                            else if(inputRequisitionID.length() < 2 || !inputRequisitionID.substring(0,2).equals("PR") || inputRequisitionID.length() > 6){
                                                System.out.println("Requisition ID format wrong, please re-enter correct format requisition ID.");
                                            }
                                            else{
                                                boolean found = false;
                                                boolean validated = false;
                                                boolean editedPRList = false;
                                                String statusChange = "";
                                                for (String[] requisition : purchaseRequisitionList){
                                                    if(requisition[0].equals(inputRequisitionID) && requisition[1].startsWith("SM") && requisition[6].equals("Pending")){
                                                        found = true;
                                                        System.out.println("Requisition ID "+inputRequisitionID+" is selected for validation.");
                                                        System.out.println("1.Approved\n2.Reject\n3.Terminate validation");
                                                        while (true){
                                                            System.out.print("Choose your option to validate: ");
                                                            String chooseStatus = sc.nextLine().trim();
                                                            if (chooseStatus.equals("1")){
                                                                requisition[6] = "Approved";
                                                                requisition[2] = this.userID;
                                                                statusChange = "Approved";
                                                                validated = true;
                                                                editedPRList = true;
                                                                break;
                                                            }
                                                            else if (chooseStatus.equals("2")){
                                                                requisition[6] = "Rejected";
                                                                requisition[2] = this.userID;
                                                                statusChange = "Rejected";
                                                                validated = true;
                                                                editedPRList = true;
                                                                break;
                                                            }
                                                            else if (chooseStatus.equals("3")){
                                                                System.out.println("Terminated Validation successfully.");
                                                                validated = true;
                                                                break;
                                                            }
                                                            else{
                                                                System.out.println("Invalid input. Please choose between 1 to 3.");
                                                            }    
                                                        }
                                                        break;
                                                    }
                                                }
                                                if (!found){
                                                     System.out.println("Requisition ID is not found or not under 'Pending' status.");
                                                }
                                                else if (validated){
                                                    if (editedPRList){
                                                        // IF WANT TO ADD "ARE YOU SURE YOU WANT TO VALIDATE THIS REQUISITION", then add if else here, no need touch others.
                                                        System.out.println("Requisition " + inputRequisitionID + " has been "+statusChange+".");
                                                        fh.updateFile("purchaseRequisitions.txt", purchaseRequisitionList);
                                                        System.out.println("Purchase Requisition file successfully updated."); 
                                                    }
                                                    while(true){
                                                        System.out.print("Do you want to validate another requisition? (Y/N): ");
                                                        String repeatValidate = sc.nextLine().trim();
                                                        if (repeatValidate.equalsIgnoreCase("y")){
                                                            processValidate = true;
                                                            validateReq = true;
                                                            break;
                                                        }
                                                        else if (repeatValidate.equalsIgnoreCase("n")){
                                                            filterReqID = "All";
                                                            filterSubmitterID = "All";
                                                            filterSubmitterUsername = "All";
                                                            filterApproverID = "All";
                                                            filterApproverName = "All";
                                                            filterSupplierID = "All";
                                                            filterSupplierName = "All";
                                                            filterItemID = "All";
                                                            filterItemName = "All";
                                                            filterQuantity = "All";
                                                            filterStatus = "All";
                                                            filterDateTime = "XX-XX-XXXX";
                                                            processValidate = true;
                                                            exitValidateReq = true;
                                                            validateReq = true;
                                                            chooseOption = true;
                                                            System.out.print("Returning to Purchase Requisition Page.");
                                                            break;
                                                        } 
                                                        else {
                                                            System.out.println("Invalid option, please re-enter only Y or N.");
                                                        }
                                                    }
                                                }
                                                //break; ??
                                            }
                                            
                                        }
                                        break;
                                        
                                    }
                                    case "3"->{
                                        filterReqID = "All";
                                        filterSubmitterID = "All";
                                        filterSubmitterUsername = "All";
                                        filterApproverID = "All";
                                        filterApproverName = "All";
                                        filterSupplierID = "All";
                                        filterSupplierName = "All";
                                        filterItemID = "All";
                                        filterItemName = "All";
                                        filterQuantity = "All";
                                        filterStatus = "All";
                                        filterDateTime = "XX-XX-XXXX";
                                        exitValidateReq = true;
                                        validateReq = true;
                                        chooseOption = true;
                                        break;
                                    }
                                    default->{
                                        System.out.println("Invalid input option, please re-input.");
                                    }
                                }
                            }
                        }
                        break;
                    }
                    case "3":{
                        String filterItemIDCPR = "All", filterItemNameCPR = "All", filterItemSellingPriceCPR = "All", filterStockLevelCPR = "All";
                        boolean exitCreateReq = false;
                        while (!exitCreateReq){
                            displayItem(false, filterItemIDCPR, filterItemNameCPR, filterItemSellingPriceCPR, filterStockLevelCPR);
                            System.out.println("\n1.Edit Item filter\n2.Choose Item to create Requisition\n3.Return to Purchase Requisition Page");
                            boolean chooseItem = false;
                            while (!chooseItem){
                                System.out.print("Enter your option: ");
                                String chooseItemOption = sc.nextLine().trim();
                                switch (chooseItemOption){
                                    case "1"->{
                                        ArrayList<String> filters = f.itemPageFilterEdit(filterItemIDCPR, filterItemNameCPR, filterItemSellingPriceCPR, filterStockLevelCPR);
                                        filterItemIDCPR = filters.get(0);
                                        filterItemNameCPR = filters.get(1);
                                        filterItemSellingPriceCPR = filters.get(2);
                                        filterStockLevelCPR = filters.get(3);
                                        chooseItem = true;
                                        break;
                                    }
                                    case "2"->{
                                        System.out.println();
                                        boolean processCreatePR = false;
                                        boolean itemExists;
                                        while (!processCreatePR){
                                            System.out.print("Enter Item ID to create requisition (input Q to terminate create requisition): ");
                                            String inputItemID = sc.nextLine().trim().toUpperCase();
                                            if (inputItemID.equalsIgnoreCase("q")){
                                                System.out.println("Terminated creating this requisition.");
                                                chooseItem = true;
                                                break;
                                            }
                                            else if (inputItemID.isEmpty()) {
                                                System.out.println("Please enter a valid Item ID.");
                                            }
                                            else if(!inputItemID.substring(0,1).equals("I") || inputItemID.length() > 5){
                                                System.out.println("Item ID format wrong, please re-enter correct format requisition ID.");
                                            }
                                            else {
                                                String itemSelected;
                                                itemExists = displayItem(true, inputItemID, "All", "All", "All");
                                                if (!itemExists){
                                                    System.out.println("Item ID is not found or doesnt exists. Please re-input Item ID.");
                                                }
                                                else{
                                                    itemSelected = inputItemID;
                                                    String filterSupplierIDCPR = "All", filterSupplierNameCPR = "All", filterItemBuyingPriceCPR = "All";
                                                    boolean exitCreateReq2 = false;
                                                    while (!exitCreateReq2){
                                                        displayItemSupplier(false, filterSupplierIDCPR, filterSupplierNameCPR, itemSelected, filterItemBuyingPriceCPR);
                                                        System.out.println("\n1.Edit Item Supplier filter\n2.Choose Supplier with Item to create Requisition\n3.Return to Purchase Requisition Page");
                                                        boolean chooseSupplier = false;
                                                        while (!chooseSupplier){
                                                            System.out.print("Enter your option: ");
                                                            String chooseSupplierOption = sc.nextLine().trim();
                                                            switch (chooseSupplierOption){
                                                                case "1"->{
                                                                    ArrayList<String> filters = f.itemSupplierFilterEdit(filterSupplierIDCPR, filterSupplierNameCPR, filterItemBuyingPriceCPR);
                                                                    filterSupplierIDCPR = filters.get(0);
                                                                    filterSupplierNameCPR = filters.get(1);
                                                                    filterItemBuyingPriceCPR = filters.get(2);
                                                                    chooseSupplier = true;
                                                                    break;
                                                                }
                                                                case "2"->{
                                                                    System.out.println();
                                                                    boolean processCreatePR2 = false;
                                                                    boolean supplierExists;
                                                                    while (!processCreatePR2){
                                                                        System.out.print("Enter Supplier ID to create requisition (input Q to terminate create requisition): ");
                                                                        String inputSupplierID = sc.nextLine().trim().toUpperCase();
                                                                        if (inputSupplierID.equalsIgnoreCase("q")){
                                                                            System.out.println("Terminated creating this requisition.");
                                                                            processCreatePR = true;
                                                                            chooseItem = true;
                                                                            exitCreateReq2 = true;
                                                                            chooseSupplier = true;
                                                                            break;
                                                                        }
                                                                        else if (inputSupplierID.isEmpty()) {
                                                                            System.out.println("Please enter a valid Supplier ID.");
                                                                        }
                                                                        else if(!inputSupplierID.substring(0,1).equals("S") || inputSupplierID.length() > 5){
                                                                            System.out.println("Supplier ID format wrong, please re-enter correct format Supplier ID.");
                                                                        }
                                                                        else {
                                                                            supplierExists = displayItemSupplier(true, inputSupplierID, "All", itemSelected, "All");
                                                                            if (!supplierExists){
                                                                               System.out.println("Supplier ID is not found or doesnt exists. Please re-input Supplier ID.");
                                                                            }
                                                                            else if (supplierExists){
                                                                                LinkedHashMap<String, String[]> itemListMap = fh.readFileMap("item.txt");
                                                                                LinkedHashMap<String, String[]> supplierListMap = fh.readFileMap("supplier.txt");
                                                                                boolean processEnterQuantity = false;
                                                                                while (!processEnterQuantity){
                                                                                    System.out.print("\nEnter quantity amount for item "+itemListMap.get(inputItemID)[1]+" (input Q to terminate create requisition): ");
                                                                                    String inputQuantity = sc.nextLine();
                                                                                    if (inputQuantity.equalsIgnoreCase("q")){
                                                                                       System.out.println("Terminated creating this requisition."); //CONTINUE FROM HERE
                                                                                       processEnterQuantity = true;
                                                                                       processCreatePR2 = true;
                                                                                       processCreatePR = true;
                                                                                       chooseItem = true;
                                                                                       exitCreateReq2 = true;
                                                                                       chooseSupplier = true;
                                                                                       //break;
                                                                                    }
                                                                                    else if (inputQuantity.isEmpty()) {
                                                                                        System.out.println("Please re-enter only digits for quantity..");
                                                                                     }
                                                                                    else if (inputQuantity.contains(".")){
                                                                                        System.out.println("Only Integers are allow for quantity, please re-input quantity.");
                                                                                    }
                                                                                    else{
                                                                                        boolean checkDigits = f.onlyDigits(inputQuantity);
                                                                                        if (!checkDigits){
                                                                                            System.out.println("Only Integers are allow for quantity, please re-input quantity.");
                                                                                        }
                                                                                        else{
                                                                                            System.out.println("\nSummary for this new purchase requisition: ");
                                                                                            System.out.println("Supplier ID: "+inputSupplierID+"\nSupplier Name: "+supplierListMap.get(inputSupplierID)[1]+"\nItem ID: "+inputItemID+
                                                                                                               "\nItem Name: "+itemListMap.get(inputItemID)[1]+"\nQuantity: "+inputQuantity);
                                                                                            boolean comfirmReq = false;
                                                                                            while (!comfirmReq){
                                                                                                System.out.print("Are you sure you want to create this purchase requisition? (Y/N): ");
                                                                                                String enterComfirmReq = sc.nextLine();
                                                                                                if (enterComfirmReq.equalsIgnoreCase("n")){
                                                                                                    System.out.println("Terminated creating this requisition.");
                                                                                                    //processEnterQuantity = true;
                                                                                                    //processCreatePR2 = true;
                                                                                                    //processCreatePR = true;
                                                                                                    //chooseItem = true;
                                                                                                    //comfirmReq = true;
                                                                                                    //exitCreateReq2 = true;
                                                                                                    //chooseSupplier = true;
                                                                                                    break;
                                                                                                } 
                                                                                                else if (enterComfirmReq.equalsIgnoreCase("y")) {
                                                                                                    fh.appendToFile("purchaseRequisitions.txt", provideID("PR", "purchaseRequisitions.txt")+","+this.userID+","+this.userID+","+inputSupplierID+","+inputItemID+","+inputQuantity+",Approved,"+currentDateTime());
                                                                                                    System.out.println("Successfully created this requisition.");
                                                                                                    break;
                                                                                                }
                                                                                                else{
                                                                                                    System.out.println("Only Y or N are allow for answer, please re-input quantity.");
                                                                                                }
                                                                                            }
                                                                                            while (true){
                                                                                                System.out.print("Do you want to create another requisition? (Y/N): ");
                                                                                                String inputRepeatCreate = sc.nextLine();
                                                                                                if (inputRepeatCreate.equalsIgnoreCase("n")){
                                                                                                    processEnterQuantity = true;
                                                                                                    processCreatePR2 = true;
                                                                                                    processCreatePR = true;
                                                                                                    chooseItem = true;
                                                                                                    comfirmReq = true;
                                                                                                    exitCreateReq2 = true;
                                                                                                    exitCreateReq = true;
                                                                                                    chooseSupplier = true;
                                                                                                    chooseOption = true;
                                                                                                    break;
                                                                                                }
                                                                                                else if (inputRepeatCreate.equalsIgnoreCase("y")){
                                                                                                    processEnterQuantity = true;
                                                                                                    processCreatePR2 = true;
                                                                                                    processCreatePR = true;
                                                                                                    chooseItem = true;
                                                                                                    comfirmReq = true;
                                                                                                    exitCreateReq2 = true;
                                                                                                    chooseSupplier = true;
                                                                                                    chooseOption = true;
                                                                                                    filterItemNameCPR = "All";
                                                                                                    filterItemSellingPriceCPR = "All";
                                                                                                    filterStockLevelCPR = "All";
                                                                                                    break;
                                                                                                }
                                                                                                else{
                                                                                                    System.out.println("Only Y or N are allow for answer, please re-input quantity.");
                                                                                                }
                                                                                            }
                                                                                        }    
                                                                                    }
                                                                               }
                                                                            }
                                                                        }
                                                                    }
                                                                    break;
                                                                }
                                                                case "3"->{
                                                                    processCreatePR = true;
                                                                    chooseItem = true;
                                                                    exitCreateReq2 = true;
                                                                    chooseSupplier = true;
                                                                    exitCreateReq = true;
                                                                    chooseItem = true;
                                                                    chooseOption = true;
                                                                    break;
                                                                }
                                                                default->{
                                                                    System.out.println("Invalid input option, please re-input from 1 to 3.");
                                                                }
                                                            }
                                                        }
                                                    }
                                                    break;
                                                }
                                            }
                                        }
                                        break;
                                    }
                                    case "3"->{
                                        exitCreateReq = true;
                                        chooseItem = true;
                                        chooseOption = true;
                                        break;
                                    }
                                    default->{
                                        System.out.println("Invalid input option, please re-input from 1 to 3.");
                                    }
                                }
                            }
                        }
                        break;
                    }
                    case "4":{
                        String filterReqIDDPR = "All", filterSubmitterIDDPR = "All", filterSubmitterUsernameDPR = "All", filterApproverIDDPR = "All", filterApproverNameDPR = "All", filterSupplierIDDPR = "All", 
                        filterSupplierNameDPR = "All", filterItemIDDPR = "All", filterItemNameDPR = "All", filterQuantityDPR = "All", filterStatusDPR = "All", filterDateTimeDPR = "XX-XX-XXXX";
                        boolean processDltReq = false;
                        while (!processDltReq){
                            displayRequisition(true, false, filterReqIDDPR, filterSubmitterIDDPR, filterSubmitterUsernameDPR, filterApproverIDDPR, filterApproverNameDPR, filterSupplierIDDPR, filterSupplierNameDPR, filterItemIDDPR, filterItemNameDPR, filterQuantityDPR, filterStatusDPR, filterDateTimeDPR);
                            System.out.println("\n1.Edit purchase requisition filter\n2.Choose purchase requisition to delete\n3.Return to Purchase Requisition Page");
                            boolean chooseDltReq = false;
                            while (!chooseDltReq){
                                System.out.print("Enter your option: ");
                                String dltReqOption = sc.nextLine();
                                switch (dltReqOption){
                                    case "1"->{ // go to filter editer and change so that they cannot edit DateTime.
                                        ArrayList<String> filter = f.requisitionPageFilterEdit("Deletion", filterReqIDDPR, filterSubmitterIDDPR, filterSubmitterUsernameDPR, filterApproverIDDPR, filterApproverNameDPR, filterSupplierIDDPR, filterSupplierNameDPR, filterItemIDDPR, filterItemNameDPR, filterQuantityDPR, filterStatusDPR, filterDateTimeDPR);
                                        filterReqIDDPR = filter.get(0);
                                        filterSubmitterIDDPR = filter.get(1);
                                        filterSubmitterUsernameDPR = filter.get(2);
                                        filterApproverIDDPR = filter.get(3);
                                        filterApproverNameDPR = filter.get(4);
                                        filterSupplierIDDPR = filter.get(5);
                                        filterSupplierNameDPR = filter.get(6);
                                        filterItemIDDPR = filter.get(7);
                                        filterItemNameDPR = filter.get(8);
                                        filterQuantityDPR = filter.get(9);
                                        filterStatusDPR = filter.get(10);
                                        filterDateTimeDPR = filter.get(11);
                                        chooseDltReq = true;
                                        break;
                                    }
                                    case "2"->{
                                        System.out.println();
                                        boolean processDltPR = false;
                                        while (!processDltPR){
                                            System.out.print("Enter purchase requisition ID to delete requisition (input Q to terminate delete requisition): ");
                                            String inputDltReqID = sc.nextLine().trim().toUpperCase();
                                            if (inputDltReqID.equalsIgnoreCase("q")){
                                                System.out.println("Terminated deleting this requisition.");
                                                chooseDltReq = true;
                                                processDltPR = true;
                                                break;
                                            }
                                            else if (inputDltReqID.isEmpty()) {
                                                System.out.println("Please enter a valid requisition ID.");
                                            }
                                            else if(inputDltReqID.length() < 2 || !inputDltReqID.substring(0,2).equals("PR") || inputDltReqID.length() > 6){
                                                System.out.println("Purchase Requistion ID format wrong, please re-enter correct format requisition ID.");
                                            }
                                            else {
                                                //String prSelected;
                                                boolean prExists;
                                                prExists = displayRequisition(true, true, inputDltReqID, "All", "All", "All", "All", "All", "All", "All", "All", "All", "All", "XX-XX-XXXX");
                                                if (!prExists){
                                                    System.out.println("Purchase Requisition ID is not found or doesnt exists. Please re-input purchase requisition ID.");
                                                }
                                                else{
                                                    boolean comfirmDltReq = false;
                                                    while (!comfirmDltReq){
                                                        System.out.print("Are you sure you want to delete this purchase requisition? (Y/N): ");
                                                        String enterComfirmReq = sc.nextLine();
                                                        if (enterComfirmReq.equalsIgnoreCase("n")){
                                                            System.out.println("Terminated deleting this requisition.");
                                                            break;
                                                        } 
                                                        else if (enterComfirmReq.equalsIgnoreCase("y")) {
                                                            fh.deleteLineFile("purchaseRequisitions.txt", inputDltReqID);
                                                            System.out.println("Successfully deleted this requisition.");
                                                            break;
                                                        }
                                                        else{
                                                            System.out.println("Only Y or N are allow for answer, please re-input quantity.");
                                                        }
                                                    }
                                                    while (true){
                                                        System.out.print("Do you want to delete another requisition? (Y/N): ");
                                                        String inputRepeatCreate = sc.nextLine();
                                                        if (inputRepeatCreate.equalsIgnoreCase("n")){
                                                            chooseOption = true;
                                                            processDltReq = true;
                                                            chooseDltReq = true;
                                                            processDltPR = true;
                                                            comfirmDltReq = true;
                                                            break;
                                                        }
                                                        else if (inputRepeatCreate.equalsIgnoreCase("y")){
                                                            chooseDltReq = true;
                                                            processDltPR = true;
                                                            comfirmDltReq = true;
                                                            break;
                                                        }
                                                        else{
                                                            System.out.println("Only Y or N are allow for answer, please re-input quantity.");
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    case "3"->{
                                        chooseOption = true;
                                        processDltReq = true;
                                        chooseDltReq = true;
                                        break;
                                    }
                                    default->{
                                        System.out.println("Invalid input option, please re-input.");
                                    }
                                }
                            }
                        }
                        break;
                    }
                    case "5":{
                        chooseOption = true;
                        exitPRPage = true;
                        break;
                    }
                    default:{
                        System.out.println("Invalid input option, please re-input.");
                    }
                }
            }
        }
    }
    
    public boolean displayRequisition(boolean checkDate, boolean dltReq, String filterReqID, String filterSubmitterID, String filterSubmitterUsername, String filterApproverID, String filterApproverName, String filterSupplierID, String filterSupplierName, String filterItemID, String filterItemName, String filterQuantity, String filterStatus, String filterDateTime){
        FileHandler fh = new FileHandler();
        Filter f = new Filter();
        ArrayList<String[]> purchaseRequisitionList = fh.readFile("purchaseRequisitions.txt");
        LinkedHashMap<String, String[]> itemListMap = fh.readFileMap("item.txt");
        LinkedHashMap<String, String[]> supplierListMap = fh.readFileMap("supplier.txt");
        LinkedHashMap<String, String[]> userListMap = fh.readUserPR();
        //String filterReqID = "All", filterUsername = "All", filterApproverName = "All", filterSupplierName = "All", filterItemName = "All", filterQuantity = "All", filterStatus = "All", filterDateTime = "XX-XX-XXXX";
        
        if ((!checkDate && !dltReq) || (checkDate && !dltReq)){
            System.out.println("\nCurrent Filter: Requisition ID: "+filterReqID+" | Submitter ID: "+filterSubmitterID+" | Submitter Username: "+filterSubmitterUsername+" | Approval ID: "+filterApproverID+" | Approval Username: "+filterApproverName+" | Supplier ID: "+filterSupplierID+" | Supplier Name: "+filterSupplierName+" | Item ID: "+filterItemID+" | Item Name: "+filterItemName+" | Quantity: "+filterQuantity+" | Status: "+filterStatus+" | Date: "+filterDateTime);
            System.out.println("------------------------------------------------------------------------- Purchase Requisition List ---------------------------------------------------------------------------------------");
            System.out.println("| Requisition ID | Submitter ID (Username)      | Approver ID (Username)       | Supplier ID (Name)            | Item ID (Item Name)         | Quantity | Status    | Date and Time       |");
            System.out.println("-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        }
         
        boolean recordFound = false;
        boolean matchesStatus = false;
        boolean matchesDateTime = false;
        for (String[] requisition : purchaseRequisitionList){
            String requisitionID = requisition[0];  
            String submitterID = requisition[1];
            String approverID = requisition[2];
            String supplierID = requisition[3];
            String itemID = requisition[4];
            String quantity = requisition[5];
            String status = requisition[6];
            String dateTime = requisition[7];
            
            String submitterName = userListMap.containsKey(submitterID) ? userListMap.get(submitterID)[5] : "Unknown";
            String approverName = userListMap.containsKey(approverID) ? userListMap.get(approverID)[5] : "Unknown";
            String supplierName = supplierListMap.containsKey(supplierID) ? supplierListMap.get(supplierID)[1] : "Unknown";
            String itemName = itemListMap.containsKey(itemID) ? itemListMap.get(itemID)[1] : "Unknown";
            
            boolean matchesReqID = f.dataCompare(filterReqID, requisitionID);
            boolean matchesSubmitterID = f.dataCompare(filterSubmitterID, submitterID);
            boolean matchesSubmitterUsername = f.dataCompare(filterSubmitterUsername, submitterName);
            boolean matchesApproverID = f.dataCompare(filterApproverID, approverID);
            boolean matchesApproverName = f.dataCompare(filterApproverName, approverName);
            boolean matchesSupplierID = f.dataCompare(filterSupplierID, supplierID);
            boolean matchesSupplierName = f.dataCompare(filterSupplierName, supplierName);
            boolean matchesItemID = f.dataCompare(filterItemID, itemID);
            boolean matchesItemName = f.dataCompare(filterItemName, itemName);
            boolean matchesQuantity = f.dataCompare(filterQuantity, quantity);
            
            if (!checkDate){    // normal checking
                matchesDateTime = f.dateCompare(filterDateTime, dateTime);
                matchesStatus = f.dataCompare(filterStatus, status);
            }
            else if (checkDate){    // when dlt req, check date if its within 2 hrs and status is not "Processed"
                matchesDateTime = withinTwoHours(dateTime);
                if (status.equalsIgnoreCase("processed")){
                    matchesStatus = false;
                }
                else{
                    matchesStatus = f.dataCompare(filterStatus, status);
                }
            }
            
            if (!checkDate && !dltReq && matchesReqID && matchesSubmitterID && matchesSubmitterUsername && matchesApproverID && matchesApproverName && matchesSupplierID && matchesSupplierName && matchesItemID && matchesItemName && matchesQuantity && matchesStatus && matchesDateTime){
                System.out.printf("| %-14s | %-5s (%-20s) | %-5s (%-20s) | %-6s (%-20s) | %-4s (%-20s) | %-8s | %-9s | %-19s |\n", requisitionID, submitterID, submitterName, approverID, approverName, supplierID, supplierName, itemID, itemName, quantity, status, dateTime);
                recordFound = true;
            }
            else if (checkDate && !dltReq && matchesReqID && matchesSubmitterID && matchesSubmitterUsername && matchesApproverID && matchesApproverName && matchesSupplierID && matchesSupplierName && matchesItemID && matchesItemName && matchesQuantity && matchesStatus && matchesDateTime){
                System.out.printf("| %-14s | %-5s (%-20s) | %-5s (%-20s) | %-6s (%-20s) | %-4s (%-20s) | %-8s | %-9s | %-19s |\n", requisitionID, submitterID, submitterName, approverID, approverName, supplierID, supplierName, itemID, itemName, quantity, status, dateTime);
                recordFound = true;
            }
            else if (checkDate && dltReq && matchesReqID && matchesSubmitterID && matchesSubmitterUsername && matchesApproverID && matchesApproverName && matchesSupplierID && matchesSupplierName && matchesItemID && matchesItemName && matchesQuantity && matchesStatus && matchesDateTime){
                System.out.println("\nPurchase Requisition to delete chosen: ");
                System.out.println("Requisition ID: "+requisitionID+"\nSubmitter ID & Name: "+submitterID+" ("+submitterName+")\nApprover ID & Name: "+approverID+" ("+approverName+")\nSupplier ID & Name: "+supplierID+" ("+supplierName+")"
                                + "\nItem ID & Name: "+itemID+" ("+itemName+")\nQuantity: "+quantity+"\nStatus: "+status+"\nDate & Time: "+dateTime);
                recordFound = true;
                return true;
            }
        }
        if (!recordFound){
            System.out.println("Purchase Requisition is not found.");
            return false;
        }
        
        return true;
    }
    
    public boolean displayItemSupplier(boolean checkItemSupplier, String filterSupplierID, String filterSupplierName, String filterItemID, String filterItemBuyingPrice){
        FileHandler fh = new FileHandler();
        Filter f = new Filter();
        ArrayList<String[]> itemSupplierList = fh.readFile("itemSupplier.txt");
        LinkedHashMap<String, String[]> itemListMap = fh.readFileMap("item.txt");
        LinkedHashMap<String, String[]> supplierListMap = fh.readSupplierCPR();
        
        if (!checkItemSupplier){
            System.out.println("\nCurrent Filter: Supplier ID: "+filterSupplierID+" | Supplier Name: "+filterSupplierName+" | Item ID: "+filterItemID+" |Item Buying Price: "+filterItemBuyingPrice);
            System.out.println("----------------------- Item Supplier List -------------------------------------------------");
            System.out.println("| Supplier ID | Supplier Name        | Item ID (Item Name)       | Item Buying Price |");
            System.out.println("--------------------------------------------------------------------------------------------");
        }

        boolean recordFound = false;
        for (String[] itemSupplier : itemSupplierList){
            String iSItemID = itemSupplier[0];
            String iSSupplierName = itemSupplier[1];
            String iSItemBuyingPrice = itemSupplier[2];
            
            String supplierID = supplierListMap.containsKey(iSSupplierName) ? supplierListMap.get(iSSupplierName)[0] : "Unknown";
            String itemName = itemListMap.containsKey(iSItemID) ? itemListMap.get(iSItemID)[1] : "Unknown";
            
            boolean matchesSupplierID = f.dataCompare(filterSupplierID, supplierID);
            boolean matchesSupplierName = f.dataCompare(filterSupplierName, iSSupplierName);
            boolean matchesItemID = f.dataCompare(filterItemID, iSItemID);
            boolean matchesItemBuyingPrice = f.dataCompare(filterItemBuyingPrice, iSItemBuyingPrice);
            
            if (!checkItemSupplier && matchesSupplierID && matchesSupplierName && matchesItemID && matchesItemBuyingPrice){
                System.out.printf("| %-11s | %-20s | %-4s (%-20s) | %-17s |\n", supplierID, iSSupplierName, iSItemID, itemName, iSItemBuyingPrice);
                recordFound = true;
            }
            else if (checkItemSupplier && matchesSupplierID && matchesSupplierName && matchesItemID && matchesItemBuyingPrice){
                System.out.println("\nItem with Supplier ID chosen: ");
                System.out.println("Supplier ID: "+supplierID+"\nSupplier Name: "+iSSupplierName+"\nItem ID: "+iSItemID+"\nItem Name: "+itemName+"\nItem Buying Price: "+iSItemBuyingPrice); 
                recordFound = true;
                return true;
            }
        }          
        if (!recordFound){
            System.out.println("Item with Supplier ID not found!");
            return false;
        }
        return true;
    }
    
    public void createViewPurchaseOrder(){
        Scanner sc = new Scanner(System.in);
        FileHandler fh = new FileHandler();
        Filter f = new Filter();
        String filterPurchaseOrderID = "All", filterSubmitterID = "All", filterSubmitterUsername = "All", filterApproverID = "All", filterApproverName = "All", 
               filterSupplierID = "All", filterSupplierName = "All", filterStatus = "All", filterPaymentStatus = "All", filterDateTime = "XX-XX-XXXX";
        boolean exitPOPage = false;
        
        while (!exitPOPage){
            displayPurchaseOrder(true, false, filterPurchaseOrderID, filterSubmitterID, filterSubmitterUsername, filterApproverID, filterApproverName, filterSupplierID, filterSupplierName, filterStatus, filterPaymentStatus, filterDateTime);
            System.out.println("\n1.Edit filter\n2.View purchase order item list\n3.Create new purchase order\n4.Delete purchase order (within 2hrs)\n5.Return to PM Page");
            boolean chooseOption = false;
            while(!chooseOption){
                System.out.print("Enter your option: ");
                String option = sc.nextLine();
                switch (option){
                    case "1"->{
                        ArrayList<String> filter = f.orderPageFilterEdit("None", filterPurchaseOrderID, filterSubmitterID, filterSubmitterUsername, filterApproverID, filterApproverName, filterSupplierID, filterSupplierName, filterStatus, filterPaymentStatus, filterDateTime);
                        filterPurchaseOrderID = filter.get(0);
                        filterSubmitterID = filter.get(1);
                        filterSubmitterUsername = filter.get(2);
                        filterApproverID = filter.get(3);
                        filterApproverName = filter.get(4);
                        filterSupplierID = filter.get(5);
                        filterSupplierName = filter.get(6);
                        filterStatus = filter.get(7);
                        filterPaymentStatus = filter.get(8);
                        filterDateTime = filter.get(9);
                        chooseOption = true;
                        break;
                    }
                    case "2"->{
                        System.out.println();
                        while (true){
                            System.out.print("Enter Purchase Order ID to view purchase order item list (input Q to return to purchase order page): ");
                            String inputViewOrder = sc.nextLine().trim().toUpperCase();
                            if (inputViewOrder.equalsIgnoreCase("q")){
                                System.out.println("Returning to Purchase Order Page...");
                                chooseOption = true;
                                break;
                            }
                            else if (inputViewOrder.isEmpty()){
                                System.out.println("Please enter a valid Supplier ID.");
                            }
                            else{
                                boolean orderItemExists = displayPurchaseOrderItem(true, inputViewOrder);
                                if (!orderItemExists){
                                    System.out.println("Invalid input Purchase Order ID, please re-input starting with PO.");
                                }
                                else{
                                    displayPurchaseOrderItem(false, inputViewOrder);
                                }
                            }
                        }
                    }
                    case "3"->{ //SCO = Supplier Create Order
                        String filterSupplierIDSCO = "All", filterSupplierNameSCO = "All", filterReqIDSCO = "All", filterSubmitterIDSCO = "All", filterSubmitterUsernameSCO = "All", filterApproverIDSCO = "All", filterApproverNameSCO = "All", filterItemIDSCO = "All", filterItemNameSCO = "All",filterQuantitySCO = "All", filterStatusSCO = "Approved", filterDateTimeSCO = "XX-XX-XXXX";
                        boolean exitCreateOrder = false;
                        while (!exitCreateOrder){
                            displaySupplierWithReq(false, filterSupplierIDSCO, filterSupplierNameSCO, filterReqIDSCO, filterSubmitterIDSCO, filterSubmitterUsernameSCO, filterApproverIDSCO, filterApproverNameSCO, filterItemIDSCO, filterItemNameSCO, filterQuantitySCO, "Approved", filterDateTimeSCO);
                            System.out.println("\n1.Edit purchase order filter\n2.Choose supplier to create purchase order\n3.Return to Purchase Order Page");
                            boolean chooseSupplier = false;
                            while (!chooseSupplier){
                                System.out.print("Enter your option: ");
                                String chooseSupplierOption = sc.nextLine().toUpperCase().trim();
                                switch (chooseSupplierOption){
                                    case "1"->{
                                        ArrayList<String> filters = f.orderSupplierFilterEdit(filterSupplierIDSCO, filterSupplierNameSCO, filterReqIDSCO, filterSubmitterIDSCO, filterSubmitterUsernameSCO, filterApproverIDSCO, filterApproverNameSCO, filterItemIDSCO, filterItemNameSCO, filterQuantitySCO, filterStatusSCO, filterDateTimeSCO);
                                        filterSupplierIDSCO = filters.get(0);
                                        filterSupplierNameSCO = filters.get(1);
                                        filterReqIDSCO = filters.get(2);
                                        filterSubmitterIDSCO = filters.get(3);
                                        filterSubmitterUsernameSCO = filters.get(4);
                                        filterApproverIDSCO = filters.get(5);
                                        filterApproverNameSCO = filters.get(6);
                                        filterItemIDSCO = filters.get(7);
                                        filterItemNameSCO = filters.get(8);
                                        filterQuantitySCO = filters.get(9);
                                        filterStatusSCO = filters.get(10);
                                        filterDateTimeSCO = filters.get(11);
                                        chooseSupplier = true;
                                        break;
                                    }
                                    case "2"->{
                                        System.out.println();
                                        boolean processCreatePO = false;
                                        boolean supplierExists;
                                        while (!processCreatePO){
                                            System.out.print("Enter Supplier ID to create purchase order (input Q to terminate create purchase order): ");
                                            String inputSupplierID = sc.nextLine().trim().toUpperCase();
                                            if (inputSupplierID.equalsIgnoreCase("q")){
                                                System.out.println("Terminated creating this purchase order.");
                                                chooseSupplier = true;
                                                break;
                                            }
                                            else if (inputSupplierID.isEmpty()) {
                                                System.out.println("Please enter a valid Supplier ID.");
                                            }
                                            else if(!inputSupplierID.substring(0,1).equals("S") || inputSupplierID.length() > 5){
                                                System.out.println("Supplier ID format wrong, please re-enter correct format requisition ID.");
                                            }
                                            else {
                                                String supplierSelected;
                                                supplierExists = displaySupplierWithReq(true, inputSupplierID, "All", "All", "All", "All", "All", "All", "All", "All", "All", "Approved", "XX-XX-XXXX");
                                                if (!supplierExists){
                                                    System.out.println("Supplier ID is not found or doesnt exists. Please re-input Supplier ID.");
                                                }
                                                else{
                                                    supplierSelected = inputSupplierID;
                                                    ArrayList<String> chosenReqID = new ArrayList<>();
                                                    boolean reqFound;
                                                    boolean reqChosenFound;
                                                    String filterReqIDCPO = "All", filterSubmitterIDCPO = "All", filterSubmitterUsernameCPO = "All", filterApproverIDCPO = "All", filterApproverNameCPO = "All", filterSupplierIDCPO = supplierSelected, 
                                                           filterSupplierNameCPO = "All", filterItemIDCPO = "All", filterItemNameCPO = "All", filterQuantityCPO = "All", filterStatusCPO = "Approved", filterDateTimeCPO = "XX-XX-XXXX";
                                                    boolean exitCreatePO2 = false;
                                                    while (!exitCreatePO2){
                                                        System.out.println("\nRequisitions under Supplier that can be chosen: ");
                                                        reqFound = displayRequisition(true, false, "display", chosenReqID, filterReqIDCPO, filterSubmitterIDCPO, filterSubmitterUsernameCPO, filterApproverIDCPO, filterApproverNameCPO, supplierSelected, filterSupplierNameCPO, filterItemIDCPO, filterItemNameCPO, filterQuantityCPO, "Approved", filterDateTimeCPO);
                                                        System.out.println("\nSelected Requisitions: ");
                                                        reqChosenFound = displayRequisition(true, false, "displayChosenReqID", chosenReqID, filterReqIDCPO, filterSubmitterIDCPO, filterSubmitterUsernameCPO, filterApproverIDCPO, filterApproverNameCPO, supplierSelected, filterSupplierNameCPO, filterItemIDCPO, filterItemNameCPO, filterQuantityCPO, "Approved", filterDateTimeCPO);
                                                        System.out.println("\n1.Edit purchase requisition filter\n2.Choose purchase requisition to create Requisition\n3.Remove purchase requisition from selected list\n4.Comfirm create purchase order\n5.Return to Purchase Order Page");
                                                        boolean chooseRequisition = false;
                                                        while(!chooseRequisition){
                                                            System.out.print("Enter your option: ");
                                                            String chooseRequisitionOption = sc.nextLine().trim();
                                                            switch (chooseRequisitionOption){
                                                                case "1"->{
                                                                    ArrayList<String> filter = f.requisitionPageFilterEdit("CPO", filterReqIDCPO, filterSubmitterIDCPO, filterSubmitterUsernameCPO, filterApproverIDCPO, filterApproverNameCPO, filterSupplierIDCPO, filterSupplierNameCPO, filterItemIDCPO, filterItemNameCPO, filterQuantityCPO, filterStatusCPO, filterDateTimeCPO);
                                                                    filterReqIDCPO = filter.get(0);
                                                                    filterSubmitterIDCPO = filter.get(1);
                                                                    filterSubmitterUsernameCPO = filter.get(2);
                                                                    filterApproverIDCPO = filter.get(3);
                                                                    filterApproverNameCPO = filter.get(4);
                                                                    filterSupplierIDCPO = filter.get(5);
                                                                    filterSupplierNameCPO = filter.get(6);
                                                                    filterItemIDCPO = filter.get(7);
                                                                    filterItemNameCPO = filter.get(8);
                                                                    filterQuantityCPO = filter.get(9);
                                                                    filterStatusCPO = filter.get(10);
                                                                    filterDateTimeCPO = filter.get(11);
                                                                    chooseRequisition = true;
                                                                    break;
                                                                }
                                                                case "2"->{
                                                                    System.out.println();
                                                                    if (!reqFound){
                                                                        System.out.println("No requisition can be selected, returning to selecting requisition to create order page.");
                                                                        chooseRequisition = true;
                                                                        break;
                                                                    }
                                                                    boolean processCreatePO2 = false;
                                                                    boolean reqNotChosen;    // if not chosen before then can add, if chosen before then cannot add.
                                                                    while (!processCreatePO2){
                                                                        System.out.print("Enter Requisition ID to create purchase order (input Q to return to selecting requisition to create order page): ");
                                                                        String inputReqID = sc.nextLine().trim().toUpperCase();
                                                                        if (inputReqID.equalsIgnoreCase("q")){
                                                                            System.out.println("Return to selecting requisition to create order page");
                                                                            chooseRequisition = true;
                                                                            processCreatePO2 = true;
                                                                            break;
                                                                        }
                                                                        else if (inputReqID.isEmpty()) {
                                                                            System.out.println("Please enter a valid Requisition ID.");
                                                                        }
                                                                        else if(inputReqID.length() < 2 || !inputReqID.substring(0, 2).equals("PR") || inputReqID.length() > 6){
                                                                            System.out.println("Requisition ID format wrong, please re-enter correct format requisition ID.");
                                                                        }
                                                                        else {
                                                                            reqNotChosen = displayRequisition(false, true, "input", chosenReqID, inputReqID, "All", "All", "All", "All", supplierSelected, "All", "All", "All", "All", "Approved", "XX-XX-XXXX");
                                                                            if (reqNotChosen){
                                                                                while (true){
                                                                                    System.out.print("Are you sure you want to select this requisition? (Y/N):");
                                                                                    String inputAddReq = sc.nextLine();
                                                                                    if (inputAddReq.equalsIgnoreCase("n")){
                                                                                        System.out.println("Abort successfully. Please re-enter your requisition ID to create purchase order.\n");
                                                                                        break;
                                                                                    }
                                                                                    else if (inputAddReq.equalsIgnoreCase("y")){
                                                                                        chosenReqID.add(inputReqID);
                                                                                        System.out.println("Successfully added requisition into selected requisitions.");
                                                                                        chooseRequisition = true;
                                                                                        processCreatePO2 = true;
                                                                                        break;
                                                                                    }
                                                                                    else{
                                                                                        System.out.println("Only Y or N are allow for answer, please re-input quantity.");
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    } 
                                                                }
                                                                case "3"->{
                                                                    System.out.println();
                                                                    if (!reqChosenFound){
                                                                        System.out.println("No requisition can be selected, returning to selecting requisition to create order page.");
                                                                        chooseRequisition = true;
                                                                        break;
                                                                    }
                                                                    boolean processDeletePO2 = false;
                                                                    boolean reqExists;
                                                                    while (!processDeletePO2){
                                                                        System.out.print("Enter Requisition ID to remove a selected requisition (input Q to return to selecting requisition to create order page): ");
                                                                        String inputReqID = sc.nextLine().trim().toUpperCase();
                                                                        if (inputReqID.equalsIgnoreCase("q")){
                                                                            System.out.println("Return to selecting requisition to create order page.");
                                                                            chooseRequisition = true;
                                                                            processDeletePO2 = true;
                                                                            break;
                                                                        }
                                                                        else if (inputReqID.isEmpty()) {
                                                                            System.out.println("Please enter a valid Requisition ID.");
                                                                        }
                                                                        else if(inputReqID.length() < 2 || !inputReqID.substring(0, 2).equals("PR") || inputReqID.length() > 6){
                                                                            System.out.println("Requisition ID format wrong, please re-enter correct format requisition ID.");
                                                                        }
                                                                        else {
                                                                            reqExists = displayRequisition(false, true, "delete", chosenReqID, inputReqID, "All", "All", "All", "All", supplierSelected, "All", "All", "All", "All", "Approved", "XX-XX-XXXX");
                                                                            if (reqExists){
                                                                                while (true){
                                                                                    System.out.print("Are you sure you want to remove this requisition? (Y/N):");
                                                                                    String inputAddReq = sc.nextLine();
                                                                                    if (inputAddReq.equalsIgnoreCase("n")){
                                                                                        System.out.println("Abort successfully. Please re-enter your requisition ID to remove requisition from selected requisitions.\n");
                                                                                        break;
                                                                                    }
                                                                                    else if (inputAddReq.equalsIgnoreCase("y")){
                                                                                        chosenReqID.remove(inputReqID);
                                                                                        System.out.println("Successfully removed requisition from selected requisitions.");
                                                                                        chooseRequisition = true;
                                                                                        processDeletePO2 = true;
                                                                                        break;
                                                                                    }
                                                                                    else{
                                                                                        System.out.println("Only Y or N are allow for answer, please re-input quantity.");
                                                                                    }
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                                case "4"->{
                                                                    System.out.println();
                                                                    if (chosenReqID.isEmpty()){
                                                                        System.out.println("No requisition is selected, returning to selecting requisition to create order page.");
                                                                        chooseRequisition = true;
                                                                        break;
                                                                    }
                                                                    System.out.println("Selected Requisitions: ");
                                                                    displayRequisition(true, false, "displayChosenReqID", chosenReqID, filterReqIDCPO, filterSubmitterIDCPO, filterSubmitterUsernameCPO, filterApproverIDCPO, filterApproverNameCPO, supplierSelected, filterSupplierNameCPO, filterItemIDCPO, filterItemNameCPO, filterQuantityCPO, "Approved", filterDateTimeCPO);
                                                                    System.out.println();
                                                                    boolean comfirmCreatePO = false;
                                                                    while (!comfirmCreatePO){
                                                                        System.out.print("Are you sure you want to create this purchase order? (input Q to return to selecting requisition to create order page) (Y/N):");
                                                                        String inputAddReq = sc.nextLine();
                                                                        if (inputAddReq.equalsIgnoreCase("q")){
                                                                            System.out.println("Return to selecting requisition to create order page");
                                                                            chooseRequisition = true;
                                                                            comfirmCreatePO = true;
                                                                            break;
                                                                        }
                                                                        else if (inputAddReq.equalsIgnoreCase("n")){
                                                                            System.out.println("Abort successfully.");
                                                                            chooseRequisition = true;
                                                                            comfirmCreatePO = true;
                                                                            break;
                                                                        }
                                                                        else if (inputAddReq.equalsIgnoreCase("y")){
                                                                            ArrayList<String[]> purchaseRequisitionList = fh.readFile("purchaseRequisitions.txt");
                                                                            ArrayList<String[]> itemSupplierList = fh.readFile("itemSupplier.txt");
                                                                            LinkedHashMap<String, String[]> supplierListMap = fh.readFileMap("supplier.txt");
                                                                            ArrayList<String[]> purchaseOrderItems = new ArrayList<>();
                                                                            ArrayList<String[]> orderRequisitions = new ArrayList<>();
                                                                            String supplierID = supplierSelected;
                                                                            String orderID = provideID("PO", "purchaseOrders.txt"); // Generate a new PO ID
                                                                            String currentDateTime = currentDateTime();

                                                                            for (String reqID : chosenReqID) {
                                                                                for (String[] requisition : purchaseRequisitionList) {
                                                                                    if (requisition[0].equals(reqID)) {
                                                                                        requisition[6] = "Processed";
                                                                                        String itemID = requisition[4];
                                                                                        String quantity = requisition[5];
                                                                                        String supplierName = supplierListMap.containsKey(supplierID) ? supplierListMap.get(supplierID)[1] : "Unknown";
                                                                                        String unitPrice = "0";
                                                                                        for (String[] itemSupplier : itemSupplierList) {
                                                                                            if (itemSupplier[0].equals(itemID) && itemSupplier[1].equals(supplierName)) {
                                                                                                unitPrice = itemSupplier[2];
                                                                                                break;
                                                                                            }
                                                                                        }
                                                                                        
                                                                                        boolean itemExists = false;
                                                                                        for (String[] orderItem : purchaseOrderItems) {
                                                                                            if (orderItem[1].equals(itemID)) {
                                                                                                orderItem[2] = Integer.toString(Integer.parseInt(orderItem[2]) + Integer.parseInt(quantity));
                                                                                                itemExists = true;
                                                                                                break;
                                                                                            }
                                                                                        }
                                                                                        if (!itemExists) {
                                                                                            purchaseOrderItems.add(new String[]{orderID, itemID, quantity, unitPrice});
                                                                                        }

                                                                                        orderRequisitions.add(new String[]{orderID, reqID});
                                                                                        break;
                                                                                    }
                                                                                }
                                                                            }

                                                                            fh.updateFile("purchaseRequisitions.txt", purchaseRequisitionList);
                                                                            fh.appendToFile("purchaseOrders.txt", orderID + "," + this.userID + ",-," + supplierID + ",Pending,None," + currentDateTime);
                                                                            for (String[] orderItem : purchaseOrderItems) {
                                                                                fh.appendToFile("purchaseOrderItems.txt", orderItem[0] + "," + orderItem[1] + "," + orderItem[2] + "," + orderItem[3]);
                                                                            }
                                                                            for (String[] order : orderRequisitions) {
                                                                                fh.appendToFile("orderRequisitions.txt", order[0] + "," + order[1]);
                                                                            }

                                                                            System.out.println("Successfully created this purchase order.");
                                                                            
                                                                            while (true){
                                                                                System.out.print("Do you want to create another purchase order? (Y/N): ");
                                                                                String inputRepeatCreate = sc.nextLine();
                                                                                if (inputRepeatCreate.equalsIgnoreCase("n")){
                                                                                    chooseOption = true;
                                                                                    exitCreateOrder = true;
                                                                                    chooseSupplier = true;
                                                                                    processCreatePO = true;
                                                                                    exitCreatePO2 = true;
                                                                                    chooseRequisition = true;
                                                                                    comfirmCreatePO = true;
                                                                                    break;
                                                                                }
                                                                                else if (inputRepeatCreate.equalsIgnoreCase("y")){
                                                                                    chooseSupplier = true;
                                                                                    processCreatePO = true;
                                                                                    exitCreatePO2 = true;
                                                                                    chooseRequisition = true;
                                                                                    comfirmCreatePO = true;
                                                                                    filterSupplierIDSCO = "All";
                                                                                    filterSupplierNameSCO = "All";
                                                                                    filterReqIDSCO = "All";
                                                                                    filterSubmitterIDSCO = "All";
                                                                                    filterSubmitterUsernameSCO = "All";
                                                                                    filterApproverIDSCO = "All";
                                                                                    filterApproverNameSCO = "All";
                                                                                    filterItemIDSCO = "All";
                                                                                    filterItemNameSCO = "All";
                                                                                    filterQuantitySCO = "All";
                                                                                    filterStatusSCO = "Approved";
                                                                                    filterDateTimeSCO = "XX-XX-XXXX";
                                                                                    break;
                                                                                }
                                                                                else{
                                                                                    System.out.println("Only Y or N are allow for answer, please re-input quantity.");
                                                                                }
                                                                            }
                                                                            break;
                                                                        }
                                                                        else{
                                                                            System.out.println("Only Y or N are allow for answer, please re-input quantity.");
                                                                        }
                                                                    }
                                                                    break;
                                                                }
                                                                case "5"->{
                                                                    chooseOption = true;
                                                                    exitCreateOrder = true;
                                                                    chooseSupplier = true;
                                                                    processCreatePO = true;
                                                                    exitCreatePO2 = true;
                                                                    chooseRequisition = true;
                                                                    break;
                                                                }
                                                                default->{
                                                                    System.out.println("Invalid input option, please re-input.");
                                                                }
                                                            }
                                                        }
                                                    }
                                                }                                        
                                            }
                                        }
                                        break;
                                    }
                                    case "3"->{
                                        chooseOption = true;
                                        exitCreateOrder = true;
                                        chooseSupplier = true;
                                        break;
                                    }
                                }
                            }   
                        }
                    }
                    case "4"->{
                        String filterPurchaseOrderIDDPO = "All", filterSubmitterIDDPO = "All", filterSubmitterUsernameDPO = "All", filterApproverIDDPO = "All", filterApproverNameDPO = "All", 
                        filterSupplierIDDPO = "All", filterSupplierNameDPO = "All", filterStatusDPO = "Pending", filterPaymentStatusDPO = "None", filterDateTimeDPO = "XX-XX-XXXX";
                        boolean processDltOrder = false;
                        boolean poDltFound = false;
                        while (!processDltOrder){
                            poDltFound = displayPurchaseOrder(true, true, filterPurchaseOrderIDDPO, filterSubmitterIDDPO, filterSubmitterUsernameDPO, filterApproverIDDPO, filterApproverNameDPO, filterSupplierIDDPO, filterSupplierNameDPO, "Pending", "None", filterDateTimeDPO);                            
                            if (!poDltFound){
                                System.out.println("Returning to Purchase Order Page... ");
                                chooseOption = true;
                                break;
                            }
                            System.out.println("\n1.Edit purchase order filter\n2.Choose purchase order to delete\n3.Return to Purchase Order Page");
                            boolean chooseDltOrder = false;
                            while (!chooseDltOrder){
                                System.out.print("Enter your option: ");
                                String dltReqOption = sc.nextLine();
                                switch (dltReqOption){
                                    case "1"->{ // go to filter editer and change so that they cannot edit DateTime.
                                        ArrayList<String> filter = f.orderPageFilterEdit("Deletion", filterPurchaseOrderIDDPO, filterSubmitterIDDPO, filterSubmitterUsernameDPO, filterApproverIDDPO, filterApproverNameDPO, filterSupplierIDDPO, filterSupplierNameDPO, filterStatusDPO, filterPaymentStatusDPO, filterDateTimeDPO);
                                        filterPurchaseOrderIDDPO = filter.get(0);
                                        filterSubmitterIDDPO = filter.get(1);
                                        filterSubmitterUsernameDPO = filter.get(2);
                                        filterApproverIDDPO = filter.get(3);
                                        filterApproverNameDPO = filter.get(4);
                                        filterSupplierIDDPO = filter.get(5);
                                        filterSupplierNameDPO = filter.get(6);
                                        filterStatusDPO = filter.get(7);
                                        filterPaymentStatusDPO = filter.get(8);
                                        filterDateTimeDPO = filter.get(9);
                                        chooseDltOrder = true;
                                        break;
                                    }
                                    case "2"->{
                                        System.out.println();
                                        boolean processDltPO = false;
                                        while (!processDltPO){
                                            System.out.print("Enter purchase order ID to delete order (input Q to terminate delete purchase order): ");
                                            String inputDltOrderID = sc.nextLine().trim().toUpperCase();
                                            if (inputDltOrderID.equalsIgnoreCase("q")){
                                                System.out.println("Terminated deleting this purchase order.");
                                                chooseDltOrder = true;
                                                processDltPO = true;
                                                break;
                                            }
                                            else if (inputDltOrderID.isEmpty()) {
                                                System.out.println("Please enter a valid purchase order ID.");
                                            }
                                            else if(inputDltOrderID.length() < 2 || !inputDltOrderID.substring(0,2).equals("PO") || inputDltOrderID.length() > 6){
                                                System.out.println("Purchase Order ID format wrong, please re-enter correct format purchase order ID.");
                                            }
                                            else {
                                                //String poSelected;
                                                boolean poExists;
                                                poExists = displayPurchaseOrder(false, true, inputDltOrderID, "All", "All", "All", "All", "All", "All", "Pending", "None", "XX-XX-XXXX");
                                                if (!poExists){
                                                    System.out.println("Purchase Order ID is not found or doesnt exists. Please re-input purchase order ID.");
                                                }
                                                else{
                                                    boolean comfirmDltOrder = false;
                                                    while (!comfirmDltOrder){
                                                        System.out.print("Are you sure you want to delete this purchase order? (Y/N): ");
                                                        String enterComfirmReq = sc.nextLine();
                                                        if (enterComfirmReq.equalsIgnoreCase("n")){
                                                            System.out.println("Terminated deleting this purchase order.");
                                                            break;
                                                        } 
                                                        else if (enterComfirmReq.equalsIgnoreCase("y")) {
                                                            ArrayList<String[]> purchaseRequisitionList = fh.readFile("purchaseRequisitions.txt");
                                                            ArrayList<String[]> orderRequisitions = fh.readFile("orderRequisitions.txt");
                                                            for (String[] orderReq : orderRequisitions){
                                                                if (orderReq[0].equals(inputDltOrderID)){
                                                                    String requisitionID = orderReq[1];
                                                                    for (String[] requisition : purchaseRequisitionList){
                                                                        if (requisition[0].equals(requisitionID)){
                                                                            requisition[6] = "Approved";
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                            fh.updateFile("purchaseRequisitions.txt", purchaseRequisitionList);
                                                            fh.deleteLineFile("purchaseOrders.txt", inputDltOrderID);
                                                            fh.deleteLineFile("purchaseOrderItems.txt", inputDltOrderID);
                                                            fh.deleteLineFile("orderRequisitions.txt", inputDltOrderID);
                                                            System.out.println("Successfully deleted this purchase order.");
                                                            break;
                                                        }
                                                        else{
                                                            System.out.println("Only Y or N are allow for answer, please re-input quantity.");
                                                        }
                                                    }
                                                    while (true){
                                                        System.out.print("Do you want to delete another purchase order? (Y/N): ");
                                                        String inputRepeatCreate = sc.nextLine();
                                                        if (inputRepeatCreate.equalsIgnoreCase("n")){
                                                            chooseOption = true;
                                                            processDltOrder = true;
                                                            chooseDltOrder = true;
                                                            processDltPO = true;
                                                            comfirmDltOrder = true;
                                                            break;
                                                        }
                                                        else if (inputRepeatCreate.equalsIgnoreCase("y")){
                                                            chooseDltOrder = true;
                                                            processDltPO = true;
                                                            comfirmDltOrder = true;
                                                            break;
                                                        }
                                                        else{
                                                            System.out.println("Only Y or N are allow for answer, please re-input quantity.");
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    case "3"->{
                                        chooseOption = true;
                                        processDltOrder = true;
                                        chooseDltOrder = true;
                                        break;
                                    }
                                    default->{
                                        System.out.println("Invalid input option, please re-input.");
                                    }
                                }
                            }
                        }
                        break;
                    }
                    case "5"->{
                        chooseOption = true;
                        exitPOPage = true;
                        break;
                    }
                    default->{
                        System.out.println("Invalid input option, please re-input.");
                    }
                }
            }
        }
    }
    
    // method override displayRequisition with arrayList
    public boolean displayRequisition(boolean display, boolean checkReqExists, String requisitionMode, ArrayList<String> chosenReqID, String filterReqID, String filterSubmitterID, String filterSubmitterUsername, String filterApproverID, String filterApproverName, String filterSupplierID, String filterSupplierName, String filterItemID, String filterItemName, String filterQuantity, String filterStatus, String filterDateTime){
        FileHandler fh = new FileHandler();
        Filter f = new Filter();
        ArrayList<String[]> purchaseRequisitionList = fh.readFile("purchaseRequisitions.txt");
        LinkedHashMap<String, String[]> itemListMap = fh.readFileMap("item.txt");
        LinkedHashMap<String, String[]> supplierListMap = fh.readFileMap("supplier.txt");
        LinkedHashMap<String, String[]> userListMap = fh.readUserPR();
        //String filterReqID = "All", filterUsername = "All", filterApproverName = "All", filterSupplierName = "All", filterItemName = "All", filterQuantity = "All", filterStatus = "All", filterDateTime = "XX-XX-XXXX";
        
        if (display){
            if (requisitionMode.equals("display")){
                System.out.println("Current Filter: Requisition ID: "+filterReqID+" | Submitter ID: "+filterSubmitterID+" | Submitter Username: "+filterSubmitterUsername+" | Approval ID: "+filterApproverID+" | Approval Username: "+filterApproverName+" | Supplier ID: "+filterSupplierID+" | Supplier Name: "+filterSupplierName+" | Item ID: "+filterItemID+" | Item Name: "+filterItemName+" | Quantity: "+filterQuantity+" | Status: "+filterStatus+" | Date: "+filterDateTime);            
            }
            System.out.println("------------------------------------------------------------------------- Purchase Requisition List ---------------------------------------------------------------------------------------");
            System.out.println("| Requisition ID | Submitter ID (Username)      | Approver ID (Username)       | Supplier ID (Name)            | Item ID (Item Name)         | Quantity | Status    | Date and Time       |");
            System.out.println("-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        }
         
        boolean recordFound = false;
        for (String[] requisition : purchaseRequisitionList){
            String requisitionID = requisition[0];  
            String submitterID = requisition[1];
            String approverID = requisition[2];
            String supplierID = requisition[3];
            String itemID = requisition[4];
            String quantity = requisition[5];
            String status = requisition[6];
            String dateTime = requisition[7];
            
            String submitterName = userListMap.containsKey(submitterID) ? userListMap.get(submitterID)[5] : "Unknown";
            String approverName = userListMap.containsKey(approverID) ? userListMap.get(approverID)[5] : "Unknown";
            String supplierName = supplierListMap.containsKey(supplierID) ? supplierListMap.get(supplierID)[1] : "Unknown";
            String itemName = itemListMap.containsKey(itemID) ? itemListMap.get(itemID)[1] : "Unknown";
            
            boolean reqIDNotChosen = true;  
            for (String reqID : chosenReqID){
                if (reqID.equals(requisitionID)){
                    reqIDNotChosen = false;
                    break;
                }
            }
            boolean matchesReqID = f.dataCompare(filterReqID, requisitionID);
            boolean matchesSubmitterID = f.dataCompare(filterSubmitterID, submitterID);
            boolean matchesSubmitterUsername = f.dataCompare(filterSubmitterUsername, submitterName);
            boolean matchesApproverID = f.dataCompare(filterApproverID, approverID);
            boolean matchesApproverName = f.dataCompare(filterApproverName, approverName);
            boolean matchesSupplierID = f.dataCompare(filterSupplierID, supplierID);
            boolean matchesSupplierName = f.dataCompare(filterSupplierName, supplierName);
            boolean matchesItemID = f.dataCompare(filterItemID, itemID);
            boolean matchesItemName = f.dataCompare(filterItemName, itemName);
            boolean matchesQuantity = f.dataCompare(filterQuantity, quantity);
            boolean matchesStatus = status.equals("Approved"); 
            
            boolean matchesDateTime;    // check date only for creating po choosing after two hours req.
            if (display && !checkReqExists && reqIDNotChosen && requisitionMode.equals("display")){
                matchesDateTime = afterTwoHours(dateTime);
            }
            else{
                matchesDateTime = f.dateCompare(filterDateTime, dateTime);
            }
                     
            if (display && !checkReqExists && reqIDNotChosen && requisitionMode.equals("display") && matchesReqID && matchesSubmitterID && matchesSubmitterUsername && matchesApproverID && matchesApproverName && matchesSupplierID && matchesSupplierName && matchesItemID && matchesItemName && matchesQuantity && matchesStatus && matchesDateTime){
                System.out.printf("| %-14s | %-5s (%-20s) | %-5s (%-20s) | %-6s (%-20s) | %-4s (%-20s) | %-8s | %-9s | %-19s |\n", requisitionID, submitterID, submitterName, approverID, approverName, supplierID, supplierName, itemID, itemName, quantity, status, dateTime);
                recordFound = true;
            }
            else if (display && !checkReqExists && !reqIDNotChosen && requisitionMode.equals("displayChosenReqID") && matchesReqID && matchesSubmitterID && matchesSubmitterUsername && matchesApproverID && matchesApproverName && matchesSupplierID && matchesSupplierName && matchesItemID && matchesItemName && matchesQuantity && matchesStatus && matchesDateTime){
                System.out.printf("| %-14s | %-5s (%-20s) | %-5s (%-20s) | %-6s (%-20s) | %-4s (%-20s) | %-8s | %-9s | %-19s |\n", requisitionID, submitterID, submitterName, approverID, approverName, supplierID, supplierName, itemID, itemName, quantity, status, dateTime);
                recordFound = true;
            }
            else if (!display && checkReqExists && requisitionMode.equals("input") && matchesReqID && matchesSubmitterID && matchesSubmitterUsername && matchesApproverID && matchesApproverName && matchesSupplierID && matchesSupplierName && matchesItemID && matchesItemName && matchesQuantity && matchesStatus && matchesDateTime){
                if (reqIDNotChosen){    // if reqID not chosen before, then true so can choose 
                    System.out.println("\nPurchase Requisition chosen: ");
                    System.out.println("Requisition ID: "+requisitionID+"\nSubmitter ID & Name: "+submitterID+" ("+submitterName+")\nApprover ID & Name: "+approverID+" ("+approverName+")\nSupplier ID & Name: "+supplierID+" ("+supplierName+")"
                                    + "\nItem ID & Name: "+itemID+" ("+itemName+")\nQuantity: "+quantity+"\nStatus: "+status+"\nDate & Time: "+dateTime);
                    recordFound = true;
                    return true;
                }
                else if (!reqIDNotChosen){  // if reqID chosen before, then false because choose before de you stil wan overlap???
                    System.out.println("\nPurchase Requisition has been already chosen, please choose another requisition to input!");
                    return false;
                }
            }
            else if (!display && checkReqExists && requisitionMode.equals("delete") && matchesReqID && matchesSubmitterID && matchesSubmitterUsername && matchesApproverID && matchesApproverName && matchesSupplierID && matchesSupplierName && matchesItemID && matchesItemName && matchesQuantity && matchesStatus && matchesDateTime){
                if (!reqIDNotChosen){   // if reqID in arrayList, delete
                    System.out.println("\nPurchase Requisition chosen to delete: ");
                    System.out.println("Requisition ID: "+requisitionID+"\nSubmitter ID & Name: "+submitterID+" ("+submitterName+")\nApprover ID & Name: "+approverID+" ("+approverName+")\nSupplier ID & Name: "+supplierID+" ("+supplierName+")"
                                    + "\nItem ID & Name: "+itemID+" ("+itemName+")\nQuantity: "+quantity+"\nStatus: "+status+"\nDate & Time: "+dateTime);
                    recordFound = true;
                    return true;
                }
                else if (reqIDNotChosen){   // if reqID not in arrayList, how to delete you dumb
                    System.out.println("\nPurchase Requisition is not in the chosen requisition list, please choose another requisition to remove!");
                    return false;
                }
            }
        }
        if (!recordFound){
            if(requisitionMode.equals("display")){
                System.out.println("Available Purchase Requisition has been all selected or is not found.");
                return false;
            }
            else if (requisitionMode.equals("displayChosenReqID")){
                System.out.println("Selected Requisition is empty.");
                return false;
            }
            else if (requisitionMode.equals("input") || requisitionMode.equals("delete")){
                System.out.println("Purchase Requisition ID is unavailable for this supplier or doesnt exists. Please re-input requisition ID.");
                return false;
            }
        }
        return true;
    }
    
    private boolean displayPurchaseOrder(boolean display, boolean checkDateTime, String filterPurchaseOrderID, String filterSubmitterID, String filterSubmitterUsername, String filterApproverID, String filterApproverUsername, String filterSupplierID, String filterSupplierName, String filterStatus, String filterPaymentStatus, String filterDateTime){
        Filter f = new Filter();
        FileHandler fh = new FileHandler();
        ArrayList<String[]> poList = fh.readFile("purchaseOrders.txt");
        LinkedHashMap<String, String[]> userListMap = fh.readFileMap("users.txt");
        LinkedHashMap<String, String[]> supplierListMap = fh.readFileMap("supplier.txt");
        
        if ((display && !checkDateTime) || (display && checkDateTime)){
            System.out.println("\nCurrent Filter: Purchase Order ID: "+filterPurchaseOrderID+" | Submitter ID: "+filterSubmitterID+" | Submitter Username: "+filterSubmitterUsername+" | Approver ID: "+filterApproverID+" | Approver Username: "+filterApproverUsername+" | Supplier ID: "+filterSupplierID+" | Supplier Name: "+filterSupplierName+" | Status: "+filterStatus+" |Payment Status: "+filterPaymentStatus+" | Date: "+filterDateTime);
            System.out.println("----------------------------------------------------------------------- Purchase Orders ----------------------------------------------------------------------------");
            System.out.println("| Purchase Order ID | Submitter ID (Username)      | Approver ID (Username)       | Supplier ID (Username)       | Status   | Payment Status | Date and Time       |");
            System.out.println("--------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        }

        boolean recordFound = false;
        boolean matchesStatus = false;
        boolean matchesPaymentStatus = false;
        boolean matchesDateTime = false;
        
        for(String[] requisition : poList){
            String orderID = requisition[0];
            String submitterID = requisition[1];
            String approverID = requisition[2];
            String supplierID = requisition[3];
            String status = requisition[4];
            String paymentStatus = requisition[5];
            String dateTime = requisition[6];
            
            String submitterName = userListMap.containsKey(submitterID) ? userListMap.get(submitterID)[5] : "Unknown";
            String approverName = userListMap.containsKey(approverID) ? userListMap.get(approverID)[5] : "Unknown";
            String supplierName = supplierListMap.containsKey(supplierID) ? supplierListMap.get(supplierID)[1] : "Unknown";
            
            boolean matchesPOID = f.dataCompare(filterPurchaseOrderID, orderID);
            boolean matchesSubmitterID = f.dataCompare(filterSubmitterID, submitterID);
            boolean matchesSubmitterUsername = f.dataCompare(filterSubmitterUsername, submitterName);
            boolean matchesApproverID = f.dataCompare(filterApproverID, approverID);
            boolean matchesApproverName = f.dataCompare(filterApproverUsername, approverName);
            boolean matchesSupplierID = f.dataCompare(filterSupplierID, supplierID);
            boolean matchesSupplierName = f.dataCompare(filterSupplierName, supplierName);
            
            
            if (!checkDateTime){    // normal checking
                matchesStatus = f.dataCompare(filterStatus, status);
                matchesPaymentStatus = f.dataCompare(filterPaymentStatus, paymentStatus);
                matchesDateTime = f.dateCompare(filterDateTime, dateTime);
            }
            else if (checkDateTime){    // when dlt PO, check date if its within 2 hrs and status is not "Pending"
                matchesStatus = status.equalsIgnoreCase("Pending");
                matchesPaymentStatus = paymentStatus.equalsIgnoreCase("None");
                matchesDateTime = withinTwoHours(dateTime);
            }
            
            if (display && !checkDateTime && matchesPOID && matchesSubmitterID && matchesSubmitterUsername && matchesApproverID && matchesApproverName && matchesSupplierID && matchesSupplierName && matchesStatus && matchesPaymentStatus && matchesDateTime){
                System.out.printf("| %-17s | %-5s (%-20s) | %-5s (%-20s) | %-5s (%-20s) | %-8s | %-14s | %-19s |\n", orderID, submitterID, submitterName, approverID, approverName, supplierID, supplierName, status, paymentStatus, dateTime);
                recordFound = true;
            }
            else if (display && checkDateTime && matchesPOID && matchesSubmitterID && matchesSubmitterUsername && matchesApproverID && matchesApproverName && matchesSupplierID && matchesSupplierName && matchesStatus && matchesPaymentStatus && matchesDateTime){
                System.out.printf("| %-17s | %-5s (%-20s) | %-5s (%-20s) | %-5s (%-20s) | %-8s | %-14s | %-19s |\n", orderID, submitterID, submitterName, approverID, approverName, supplierID, supplierName, status, paymentStatus, dateTime);
                recordFound = true;
            }
            else if (!display && checkDateTime && matchesPOID && matchesSubmitterID && matchesSubmitterUsername && matchesApproverID && matchesApproverName && matchesSupplierID && matchesSupplierName && matchesStatus && matchesPaymentStatus && matchesDateTime){
                System.out.println("Purchase Order ID: "+orderID+"\nSubmitter ID & Name: "+submitterID+" ("+submitterName+")\nApprover ID & Name: "+approverID+" ("+approverName+")\nSupplier ID & Name: "+supplierID+" ("+supplierName+")"
                                    + "\nStatus: "+status+"\nPayment Status: "+paymentStatus+"\nDate & Time: "+dateTime);
                recordFound = true;
                return true;
            }
        }
        if (!recordFound){
            if (display && !checkDateTime){
                System.out.println("Purchase Order not found!");
            }
            else if (display && checkDateTime){
                System.out.println("No purchase order created within 2hrs or purchase order not found!");
            }
            return false;
        }
        return true;
    }
   
    public boolean displayPurchaseOrderItem(boolean checkPOID, String inputOrderID){
        FileHandler fh = new FileHandler();
        ArrayList<String[]> purchaseOrderList  = fh.readFile("purchaseOrders.txt");
        ArrayList<String[]> purchaseOrderItemList = fh.readFile("purchaseOrderItems.txt");
        LinkedHashMap<String,String[]> itemListMap = fh.readFileMap("item.txt");
        LinkedHashMap<String,String[]> userListMap = fh.readFileMap("users.txt");
        LinkedHashMap<String,String[]> supplierListMap = fh.readFileMap("supplier.txt");
        
        int totalPrice = 0;
        boolean recordFound = false;
        for (String[] order : purchaseOrderList){
            String orderID = order[0];
            if (orderID.equals(inputOrderID)){
                if (checkPOID){
                    return true;
                }
                String submitterID = order[1];
                String approverID = order[2];
                String supplierID = order[3];
                String dateTime = order[6];
                
                String submitterName = userListMap.containsKey(submitterID) ? userListMap.get(submitterID)[5] : "Unknown";
                String approverName = userListMap.containsKey(approverID) ? userListMap.get(approverID)[5] : "Unknown";
                String supplierName = supplierListMap.containsKey(supplierID) ? supplierListMap.get(supplierID)[1] : "Unknown";   
                
                System.out.println("\nPurchase Order ID: "+orderID+" | Submitter ID & Name: "+submitterID+" ("+submitterName+") | Approver ID & Name: "+approverID+" ("+approverName+") | Supplier ID & Name: "+supplierID+" ("+supplierName+") | Date and Time: "+dateTime);
                if (!checkPOID){
                    System.out.println("-----------------Purchase Order Item List------------------------------");
                    System.out.println("| ItemID | Item Name            | Quantity | Unit Price  | Total      |");
                    System.out.println("-----------------------------------------------------------------------");
                }
                        
                for (String[] orderItems : purchaseOrderItemList){
                    if (orderItems[0].equals(orderID)){
                        String itemID = orderItems[1];
                        String itemQuantity = orderItems[2];
                        String itemUnitPrice = orderItems[3];
                        
                        String itemName = itemListMap.containsKey(itemID) ? itemListMap.get(itemID)[1] : "Unknown";
                        String totalUnitPrice = Integer.toString(Integer.parseInt(itemQuantity)*Integer.parseInt(itemUnitPrice));
                        totalPrice = totalPrice + Integer.parseInt(totalUnitPrice);
                        
                        System.out.printf("| %-6s | %-20s | %-8s | RM%-9s | RM%-8s |\n", itemID, itemName, itemQuantity, itemUnitPrice, totalUnitPrice);
                        recordFound = true;
                    }
                }
                break;
            }
        }
        if (recordFound){
            System.out.printf("| %-40s | %-11s:  RM%-8s |\n\n", " ", "Total Price", totalPrice);
            return true;
        }
        else if(!recordFound){
            if (!checkPOID){
                System.out.println("Purchase Order Item not found!\n");
            }
            return false;
        }
        return true;
    }
    
    public boolean displaySupplierWithReq(boolean checkSupplier, String filterSupplierID, String filterSupplierName, String filterReqID, String filterSubmitterID, String filterSubmitterUsername, String filterApproverID, String filterApproverName, String filterItemID, String filterItemName, String filterQuantity, String filterStatus, String filterDateTime){
        FileHandler fh = new FileHandler();
        Filter f = new Filter();
        ArrayList<String[]> supplierList = fh.readFile("supplier.txt");
        ArrayList<String[]> purchaseRequisitionList = fh.readFile("purchaseRequisitions.txt");
        LinkedHashMap<String, String[]> itemListMap = fh.readFileMap("item.txt");
        LinkedHashMap<String, String[]> userListMap = fh.readUserPR();
        //String filterReqID = "All", filterUsername = "All", filterApproverName = "All", filterSupplierName = "All", filterItemName = "All", filterQuantity = "All", filterStatus = "All", filterDateTime = "XX-XX-XXXX";
        
        if (!checkSupplier){
            System.out.println("\nCurrent Filter: Supplier ID: "+filterSupplierID+" | Supplier Name: "+filterSupplierName+" | Requisition ID: "+filterReqID+" | Submitter ID: "+filterSubmitterID+" | Submitter Username: "+filterSubmitterUsername+" | Approval ID: "+filterApproverID+" | Approval Username: "+filterApproverName+" | Item ID: "+filterItemID+" | Item Name: "+filterItemName+" | Quantity: "+filterQuantity+" | Status: "+filterStatus+" | Date: "+filterDateTime);
            System.out.println("------------------------------------------------------------------------- Supplier in Requisition List -----------------------------------------------------------------------------------");
            System.out.println("| Supplier ID (Name)            | Requisition ID | Submitter ID (Username)      | Approver ID (Username)       | Item ID (Item Name)         | Quantity | Status   | Date and Time       |");
            System.out.println("------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------");
        }
        boolean recordFound = false;
        String newSupplierID;
        String oldSupplierID = "";
        
        for (String[] supplier : supplierList){
            String supplierID = supplier[0];
            String supplierName = supplier[1];
            newSupplierID = supplierID;
            for (String[] requisition : purchaseRequisitionList){
                if (requisition[3].equals(supplierID)){
                    String requisitionID = requisition[0];  
                    String submitterID = requisition[1];
                    String approverID = requisition[2];
                    String itemID = requisition[4];
                    String quantity = requisition[5];
                    String status = requisition[6];
                    String dateTime = requisition[7];
                    
                    String submitterName = userListMap.containsKey(submitterID) ? userListMap.get(submitterID)[5] : "Unknown";
                    String approverName = userListMap.containsKey(approverID) ? userListMap.get(approverID)[5] : "Unknown";
                    String itemName = itemListMap.containsKey(itemID) ? itemListMap.get(itemID)[1] : "Unknown";
                    
                    boolean matchesReqID = f.dataCompare(filterReqID, requisitionID);
                    boolean matchesSubmitterID = f.dataCompare(filterSubmitterID, submitterID);
                    boolean matchesSubmitterUsername = f.dataCompare(filterSubmitterUsername, submitterName);
                    boolean matchesApproverID = f.dataCompare(filterApproverID, approverID);
                    boolean matchesApproverName = f.dataCompare(filterApproverName, approverName);
                    boolean matchesSupplierID = f.dataCompare(filterSupplierID, supplierID);
                    boolean matchesSupplierName = f.dataCompare(filterSupplierName, supplierName);
                    boolean matchesItemID = f.dataCompare(filterItemID, itemID);
                    boolean matchesItemName = f.dataCompare(filterItemName, itemName);
                    boolean matchesQuantity = f.dataCompare(filterQuantity, quantity);
                    boolean matchesStatus =  status.equals("Approved"); //f.dataCompare(filterStatus, status);
                    boolean matchesDateTime = afterTwoHours(dateTime);
                    

                    if (!checkSupplier && matchesReqID && matchesSubmitterID && matchesSubmitterUsername && matchesApproverID && matchesApproverName && matchesSupplierID && matchesSupplierName && matchesItemID && matchesItemName && matchesQuantity && matchesStatus && matchesDateTime){
                        if (!newSupplierID.equals(oldSupplierID)){
                            System.out.printf("| %-6s (%-20s) | %-14s | %-5s (%-20s) | %-5s (%-20s) | %-4s (%-20s) | %-8s | %-8s | %-19s |\n", supplierID, supplierName, requisitionID, submitterID, submitterName, approverID, approverName, itemID, itemName, quantity, status, dateTime);
                            oldSupplierID = newSupplierID;
                            recordFound = true;
                        }
                        else{
                            System.out.printf("| %-6s %-22s | %-14s | %-5s (%-20s) | %-5s (%-20s) | %-4s (%-20s) | %-8s | %-8s | %-19s |\n", "", "", requisitionID, submitterID, submitterName, approverID, approverName, itemID, itemName, quantity, status, dateTime);
                            oldSupplierID = newSupplierID;
                            recordFound = true;
                        }
                    }
                    else if (checkSupplier && matchesReqID && matchesSubmitterID && matchesSubmitterUsername && matchesApproverID && matchesApproverName && matchesSupplierID && matchesSupplierName && matchesItemID && matchesItemName && matchesQuantity && matchesStatus && matchesDateTime){
                        System.out.println("\n Supplier to create purchase order chosen: "+supplierID+" ("+supplierName+")");
                        recordFound = true;
                        return true;
                    }
                }
            }
        }
        
        if (!recordFound){
            System.out.println("Purchase Requisition is not found.");
            return false;
        }
        
        return true;
    }
}