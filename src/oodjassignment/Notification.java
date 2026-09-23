package oodjassignment;

import java.util.ArrayList;

public class Notification {
    
    public boolean reorderNotification() {
        FileHandler fh = new FileHandler();
        ArrayList<String[]> itemList = fh.readFile("item.txt");

        boolean recordFound = false;
        int stockLevel;
        int reorderLevel;

        for (String[] item : itemList) {
            String itemID = item[0];
            String itemName = item[1];
            try {
                stockLevel = Integer.parseInt(item[3]);
                reorderLevel = Integer.parseInt(item[4]);
            } catch (NumberFormatException e) {
                continue;
            }

            if (stockLevel <= reorderLevel) {
                if (!recordFound) {
                    System.out.println("\n------------ List of Items at Reorder Level-------------------");
                    System.out.println("| Item ID    | Item Name          | Stock Level | Reorder Level |");
                    System.out.println("----------------------------------------------------------------");
                }
                System.out.printf("| %-10s | %-18s | %-11d | %-13d |\n", itemID, itemName, stockLevel, reorderLevel);
                recordFound = true;
            }
        }

        if (!recordFound) {
            System.out.println("\nNo items below reorder level.");
            return false;
        } else {
            System.out.println("\nPlease restock items as soon as possible.");
        }

        return true;
    }

}
