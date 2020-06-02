package Audit;

import java.io.*;
import java.util.*;

public class AuditService {
    private static AuditService instance =  null;
    private AuditService(){
    }
    public static AuditService getInstance(){
        if(instance == null){
            instance = new AuditService();
        }
        return instance;
    }
    public void writeInAuditFile(ArrayList<String> t, ArrayList<String> dates, ArrayList<String> threadList) throws Exception{
            FileWriter csvWriter = new FileWriter("Audit.csv");
            int i=0;
            for(String action:t) {
                csvWriter.append(action);
                csvWriter.append(",");
                csvWriter.append(dates.get(i));
                csvWriter.append(",");
                csvWriter.append(threadList.get(i));
                csvWriter.append("\n");
                i++;
            }
            csvWriter.flush();
            csvWriter.close();
    }/*
    public static void main(String[] args){
        FileWriter csvWriter = new FileWriter("Audit.csv");
        csvWriter.append("Name");
        csvWriter.append(",");
        csvWriter.append("Role");
        csvWriter.append(",");
        csvWriter.append("Topic");
        csvWriter.append("\n");

        for (List<String> rowData : rows) {
            csvWriter.append(String.join(",", rowData));
            csvWriter.append("\n");
        }

        csvWriter.flush();
        csvWriter.close();
    }*/
}
