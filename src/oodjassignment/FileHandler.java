package oodjassignment;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;   

public class FileHandler {
    protected List<Users> readUsers(){
        List<Users> userList = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new FileReader("users.txt"))){
            String line;
            while ((line = br.readLine()) != null){
                String[] userInfo = line.split(",");
                userList.add(new Users(userInfo[0],userInfo[1],userInfo[2],userInfo[3],userInfo[4],userInfo[5],userInfo[6]));
            }
        }catch(IOException e){}
        return userList;
    }
    
    protected ArrayList<String[]> readFile(String fileName){
        ArrayList<String[]> readFile = new ArrayList<>();
        try(BufferedReader br = new BufferedReader(new FileReader(fileName))){
            String line;
            while ((line = br.readLine()) != null){
                String[] fields = line.split(",");
                readFile.add(fields);
            }
        }catch(IOException e){e.printStackTrace();}
        
        return readFile;
    }
    
    protected LinkedHashMap<String, String[]> readFileMap(String fileName){
        LinkedHashMap<String, String[]> itemListMap = new LinkedHashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))){
            String line;
            while ((line = br.readLine()) != null){
                String[] data = line.split(",");
                itemListMap.put(data[0], data);
            }
        }catch(IOException e){e.printStackTrace();}
        
        return itemListMap;
    }
    
    protected LinkedHashMap<String, String[]> readUserPR(){
        LinkedHashMap<String, String[]> userListMap = new LinkedHashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader("users.txt"))){
            String line;
            while ((line = br.readLine()) != null){
                String[] fields = line.split(",");
                String role = fields[4];
                if (role.equals("salesManager") || role.equals("purchaseManager")){
                    String userID = fields[0];
                    userListMap.put(userID, fields);
                }
            }
        }catch(IOException e){e.printStackTrace();}
        
        return userListMap;
    }
    
    protected LinkedHashMap<String, String[]> readSupplierCPR(){
        LinkedHashMap<String, String[]> itemListMap = new LinkedHashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader("supplier.txt"))){
            String line;
            while ((line = br.readLine()) != null){
                String[] data = line.split(",");
                itemListMap.put(data[1], data);
            }
        }catch(IOException e){e.printStackTrace();}
        
        return itemListMap;
    }
    
    protected void updateFile(String fileName, ArrayList<String[]> updateFileInfo){
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))){
            for (String[] line : updateFileInfo){
                bw.write(String.join(",", line));
                bw.newLine();
            }
        }catch(IOException e){System.out.println("Error writing to file: " + e.getMessage());}
    }
    
    protected void appendToFile(String fileName, String lineToAppend){
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName, true))) {
            bw.write(lineToAppend);
            bw.newLine();
        }
        catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }
    
    protected void deleteLineFile(String fileName, String lineToDelete){
        ArrayList<String[]> fileToDelete = readFile(fileName);
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))){
            for (String[] line : fileToDelete){
                if (!line[0].equals(lineToDelete)) {
                    bw.write(String.join(",", line));
                    bw.newLine();
                }
            }
        }
        catch (IOException e){
            System.out.println("Error deleting a line on file: " + e.getMessage());
        }
    }
}
