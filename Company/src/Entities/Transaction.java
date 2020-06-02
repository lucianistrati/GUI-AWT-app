package Entities;

import java.time.LocalDateTime;
import java.io.*;
public class Transaction implements Serializable {
    private static final long serialVersionUID = 1L;
    protected double EntrySum, ExitSum = 0;
    protected String EntryCurrency, ExitCurrency;
    protected double TransactionProfit = 0.0; // profit per transaction is calculated in dolars
    protected int TransactionDay;
    protected int ClientID;
    protected LocalDateTime localTime;
    int getClientID(){
        return this.ClientID;
    }
    protected static double roundAvoid(double value, int places) {
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;

    }
    //Gained money by the House is the entrySum and lost money by the House is the Exit Sum
    Transaction(double es, String ec, String xc, int day, LocalDateTime local,int id) {
        this.EntrySum = es;
        this.EntryCurrency = ec;
        this.ExitCurrency = xc;
        this.TransactionDay = day;
        this.localTime = local;
        this.ClientID = id;
    }

    int getTransactionDay() {
        return this.TransactionDay;
    }
    double getEntrySum() {
        return this.EntrySum;
    }
    double getExitSum() {
        return this.ExitSum;
    }
    String getEntryCurrency() { return this.EntryCurrency;}
    String getExitCurrency() { return this.ExitCurrency;}
    LocalDateTime getLocalTime() { return this.localTime;}
    @Override
    public String toString() {
        return EntrySum + " " + EntryCurrency + " were converted to " + ExitSum + " " + ExitCurrency + " with a transaction profit of " + TransactionProfit + " on the day: " + TransactionDay + " at the current local date and time " + localTime + " by the client with the ID " + ClientID;
    }

    double getTransactionProfit() {
        return this.TransactionProfit;
    }






}