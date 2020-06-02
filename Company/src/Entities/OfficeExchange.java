package Entities;
import Services.NormalClientCSVReader;
import Services.NormalTransactionCSVReader;
import Services.PremiumClientCSVReader;
import Services.PremiumTransactionCSVReader;
import Exceptions.MyInputException;
import Services.MyRunnableThread;
import oracle.jdbc.driver.OracleDriver;
import java.io.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.time.LocalDateTime;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
//import au.com.bytecode.opencsv.CSVReader;


public class OfficeExchange { //This class is implemented as a SingleTon Class
    private static OfficeExchange single_instance = new OfficeExchange(); // Used for declaring singleton class
    private static final String DB_URL = "jdbc:oracle:thin:@193.226.51.37:1521:o11g";
    private static final String USER = "grupa35";
    private static final String PASS = "bazededate";

    private int threadCounter = 0;
    private int CurrentDay = 0;
    private double PremiumTax = 100.0; // 100 dolars
    private double PremiumCommission = 0.005; // 0.5% commission for premium clients
    private double NormalCommission = 0.01; //1% commission for normal clients
    private double TotalProfit = 0.0;
    private HashMap < String, Double > CurrenciesAmounts;
    private HashMap < String, Double > CurrenciesParities;
    private ArrayList < HashMap < String, Double >> CurrenciesParitiesHistory;
    private Set < String > AllCurrencies;
    private ArrayList <Client> AllClients;
    private ArrayList <Transaction> AllTransactions;
    private ArrayList<String> t;
    private ArrayList<String> dates;
    private ArrayList<String> threadList;
    private NormalClientCSVReader nc;
    private PremiumClientCSVReader pc;
    private NormalTransactionCSVReader nt;
    private PremiumTransactionCSVReader pt;
    private HashMap<Integer,String> numberToAction;
    //private MyRunnableThread thr;
    private enum TransactionType {
        Normal,
        Premium
    }
    private OfficeExchange() {
        this.AllClients = new ArrayList <Client> ();
        this.AllTransactions = new ArrayList <Transaction> ();
        this.t = new ArrayList<String>();
        this.dates = new ArrayList<String>();
        this.threadList = new ArrayList<String>();
        this.CurrenciesAmounts = new HashMap < String, Double > ();
        this.CurrenciesParities = new HashMap < String, Double > ();
        this.CurrenciesParitiesHistory = new ArrayList < HashMap < String, Double >> ();
        this.AllCurrencies = new HashSet < String > ();
        this.nc = NormalClientCSVReader.getInstance();
        this.pc = PremiumClientCSVReader.getInstance();
        this.nt = NormalTransactionCSVReader.getInstance();
        this.pt = PremiumTransactionCSVReader.getInstance();
        this.numberToAction = new HashMap<Integer, String>();
        //this.thr = new MyRunnableThread();
    }
    public static OfficeExchange getInstance() {
        return single_instance;
    }
    public ArrayList<String> getT(){
        return this.t;
    }
    //public MyRunnableThread getThread() { return this.thr;}
    public HashMap<Integer, String> getNumberToAction(){
        return this.numberToAction;
    }
    public ArrayList<String> getDates(){
        return this.dates;
    }
    public void addToThreadList(String myString){
        this.threadList.add(myString);
    }
    public void incrementThreadCount(){
        this.threadCounter++;
    }
    public int getThreadCount(){
        return this.threadCounter;
    }
    public ArrayList<String> getThreadList(){
        return this.threadList;
    }
    public static double roundAvoid(double value, int places) {
  /*This static method is truncating every double value that is passed to it
  to the first #places decimals, in this case, we will want to truncate to the first 4 decimals because that is the practice
  in the financial sector.
  */
        double scale = Math.pow(10, places);
        return Math.round(value * scale) / scale;
    }
    public void addToTotalProfit(double sum) {
        this.TotalProfit += sum;
    }

    public double getTotalProfit() {
        return this.TotalProfit;
    }
    public void setTotalProfit(double val) {
        this.TotalProfit = val;
    }

    public int getCurrentDay() {
        return this.CurrentDay;
    }
    public void setCurrentDay(int day) {
        this.CurrentDay = day;
    }

    public double getPremiumTax() {
        return PremiumTax;
    }
    public void setPremiumTax(double val) {
        this.PremiumTax = val;
    }

    public double getPremiumCommission() {
        return this.PremiumCommission;
    }
    public void setPremiumCommission(double val) {
        this.PremiumCommission = val;
    }

    public double getNormalCommission() {
        return this.NormalCommission;
    }
    public void setNormalCommission(double val) {
        this.NormalCommission = val;
    }

    public void loadDataFromAllDatabases(){
        /**
         * This function is reading all the data from the SQL Tables and loading that data in the
         * ArrayList structures that contain the necessary informations about clients and transactions.
         */
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            ResultSet rs;
            PreparedStatement preparedStatement;
            preparedStatement = conn
                    .prepareStatement("SELECT * FROM PREMIUMCLIENT");
            rs = preparedStatement.executeQuery();

            while (rs.next()) {
                String id = rs.getString(1);
                String n = rs.getString(2);
                String z = rs.getString(3);
                String d = rs.getString(4);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                Date parsedDate = dateFormat.parse(d);
                Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
                PremiumClient p = new PremiumClient(n,Integer.parseInt(z), timestamp.toLocalDateTime());
                //p.setClientID(Integer.parseInt(id));
                this.AllClients.add(p);
            }


            preparedStatement = conn
                    .prepareStatement("SELECT * FROM NORMALCLIENT");
            rs = preparedStatement.executeQuery();

            while (rs.next()) {
                String id = rs.getString(1);
                String n = rs.getString(2);
                String z = rs.getString(3);
                String d = rs.getString(4);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                Date parsedDate = dateFormat.parse(d);
                Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
                NormalClient p = new NormalClient(n,Integer.parseInt(z), timestamp.toLocalDateTime());
               // p.setClientID(Integer.parseInt(id));
                this.AllClients.add(p);
            }

            preparedStatement = conn
                    .prepareStatement("SELECT * FROM PREMIUMTRANSACTION");
            rs = preparedStatement.executeQuery();

            while (rs.next()) {
                double es = Double.parseDouble(rs.getString(1));
                String ec = rs.getString(2);
                String xc = rs.getString(4);
                int z = Integer.parseInt(rs.getString(6));
                String d = rs.getString(7);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                Date parsedDate = dateFormat.parse(d);
                Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
                int id = Integer.parseInt(rs.getString(8));
                double conv = this.obtainConversionRate(ec, xc);
                PremiumTransaction p = new PremiumTransaction(es,ec,xc,z,conv,this.PremiumCommission,conv, timestamp.toLocalDateTime(),id);
                this.AllTransactions.add(p);
            }

            preparedStatement = conn
                    .prepareStatement("SELECT * FROM NORMALTRANSACTION");
            rs = preparedStatement.executeQuery();

            while (rs.next()) {
                double es = Double.parseDouble(rs.getString(1));
                String ec = rs.getString(2);
                String xc = rs.getString(4);
                int z = Integer.parseInt(rs.getString(6));
                String d = rs.getString(7);
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                Date parsedDate = dateFormat.parse(d);
                Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());
                int id = Integer.parseInt(rs.getString(8));
                double conv = this.obtainConversionRate(ec, xc);
                NormalTransaction p = new NormalTransaction(es,ec,xc,z,conv,this.PremiumCommission,conv, timestamp.toLocalDateTime(),id);
                this.AllTransactions.add(p);
            }
            conn.close();

        }catch(Exception e){
            e.printStackTrace();
        }

    }
    public void loadAllData() throws IOException {
        ArrayList<String> v=new ArrayList<>();
        try {
            v.add("NormalClient.csv");
            v.add("PremiumClient.csv");
            v.add("NormalTransaction.csv");
            v.add("PremiumTransaction.csv");
        } catch (Exception e){
            e.printStackTrace();
        }
        for(String filename: v){
            if(filename.equals("NormalClient.csv")){
                nc.readFileData(filename,this.getNormalCommission());
                for(NormalClient x:nc.getReadNc()){
                    Client c = x;
                    AllClients.add(c);
                }
            }
            if(filename.equals("PremiumClient.csv")){
                pc.readFileData(filename,this.getPremiumCommission());
                for(PremiumClient x:pc.getReadPc()){
                    Client c = x;
                    AllClients.add(c);
                }
            }
            if(filename.equals("NormalTransaction.csv")){
                nt.readFileData(filename,this.getNormalCommission());
                for(NormalTransaction x:nt.getReadNt()){
                    Transaction c = x;
                    AllTransactions.add(c);
                    this.addToTotalProfit(c.TransactionProfit);
                }
            }
            if(filename.equals("PremiumTransaction.csv")){
                pt.readFileData(filename, this.getPremiumCommission());
                for(PremiumTransaction x:pt.getReadPt()){
                    Transaction c = x;
                    AllTransactions.add(c);
                    this.addToTotalProfit(c.TransactionProfit);
                }
            }
        }
        linkTransactionsToClients();
        System.out.println("All data was loaded from our database");
  }
    public void linkTransactionsToClients(){
        /*
        In this function we are going to iterate through all the normal transactions, for their id we will find the normal client
        with that id and then perform "Entities.Client.get(id).addTransaction(t)" the same operation will be performed for premium transactions
        and premium clients, after this operation is performed the all our csv data will be loaded
         */
        for(int i=0;i<AllClients.size();i++){
            Client c = AllClients.get(i);
            if(c instanceof PremiumClient){
                for(Transaction t:AllTransactions) {
                    if(c.getClientID()==t.getClientID()){
                        Transaction d = t;
                        AllClients.get(i).addTransaction(d,t.getTransactionDay(),this.getCurrenciesParities(t.getEntryCurrency(),t.getExitCurrency()),this.PremiumCommission,this.getCurrenciesParities(t.getEntryCurrency(),t.getExitCurrency()),t.getLocalTime(),this.AllClients.get(i).getClientID());
                    }
                }
            }else if(c instanceof NormalClient) {
                for(Transaction t:AllTransactions) {
                    if(c.getClientID()==t.getClientID()){
                        Transaction d = t;
                        AllClients.get(i).addTransaction(d,t.getTransactionDay(),this.getCurrenciesParities(t.getEntryCurrency(),t.getExitCurrency()),this.PremiumCommission,this.getCurrenciesParities(t.getEntryCurrency(),t.getExitCurrency()),t.getLocalTime(),this.AllClients.get(i).getClientID());
                    }
                }
            }
        }
    }
    public ArrayList<Client> getAllClients(){
        System.out.println(this.AllClients.size());
        return this.AllClients;
    }
    public ArrayList<Transaction> getAllTransactions(){
        System.out.println(this.AllTransactions.size());
        return this.AllTransactions;
    }
    public void addPremiumClient(){
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the name of the premium client");
        String myName = sc.nextLine();
        LocalDateTime myObj = LocalDateTime.now();
        PremiumClient c = new PremiumClient(myName, this.CurrentDay, myObj);
        this.addToTotalProfit(this.PremiumTax);
        this.AllClients.add(c);
        System.out.println(this.AllClients.get(AllClients.size() - 1));
    }
    public void addAllData(){
        ArrayList< ArrayList<String> > premiumRecords = new ArrayList< ArrayList<String> >();
        ArrayList< ArrayList<String> > normalRecords = new ArrayList< ArrayList<String> >();
        String premiumPath = "Entities.PremiumClient.csv",normalPath="Entities.NormalClient.csv";
        try {
        for(Client c:AllClients) {
            if(c instanceof PremiumClient) {
                    ArrayList<String> rec = new ArrayList<>();
                    String[] record = new String[4];
                    record[0] = Integer.toString(c.ClientID);
                    record[1] = c.name;
                    record[2] = Integer.toString(c.ClientDay);
                    DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    String formattedDate = c.localTime.format(myFormatObj);
                    record[3] = formattedDate;
                    for(String r:record){
                        rec.add(r);
                    }
                    premiumRecords.add(rec);

            }else if(c instanceof NormalClient){
                ArrayList<String> rec = new ArrayList<>();
                String[] record = new String[4];
                record[0] = Integer.toString(c.ClientID);
                record[1] = c.name;
                record[2] = Integer.toString(c.ClientDay);
                DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String formattedDate = c.localTime.format(myFormatObj);
                record[3] = formattedDate;
                for(String r:record){
                    rec.add(r);
                }
                normalRecords.add(rec);
            }
        }
        pc.writeFileData(premiumPath, premiumRecords);
        nc.writeFileData(normalPath, normalRecords);
        } catch (IOException e) {
            e.printStackTrace();
        }
        premiumRecords.clear();
        normalRecords.clear();
        premiumRecords = new ArrayList< ArrayList<String> >();
        normalRecords = new ArrayList< ArrayList<String> >();
        premiumPath = "Entities.PremiumTransaction.csv";
        normalPath="Entities.NormalTransaction.csv";
        try {
            for(Transaction c:AllTransactions) {
                boolean isItPremium = false;
                for(Client f: AllClients){
                    if(c.ClientID==f.ClientID){
                        if(f instanceof PremiumClient){
                            isItPremium = true;
                        }
                        if(f instanceof NormalClient){
                            isItPremium = false;
                        }
                    }
                }
                if(isItPremium==true) {
                    ArrayList<String> rec = new ArrayList<>();
                    rec.add(Double.toString(c.EntrySum));
                    rec.add(c.EntryCurrency);
                    rec.add(Double.toString(c.ExitSum));
                    rec.add(c.ExitCurrency);
                    rec.add(Double.toString(c.TransactionProfit));
                    rec.add(Integer.toString(c.TransactionDay));
                    DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    String formattedDate = c.localTime.format(myFormatObj);
                    rec.add(formattedDate);
                    rec.add(Integer.toString(c.ClientID));
                    premiumRecords.add(rec);
                }else
                    {
                    ArrayList<String> rec = new ArrayList<>();
                    rec.add(Double.toString(c.EntrySum));
                    rec.add(c.EntryCurrency);
                    rec.add(Double.toString(c.ExitSum));
                    rec.add(c.ExitCurrency);
                    rec.add(Double.toString(c.TransactionProfit));
                    rec.add(Integer.toString(c.TransactionDay));
                    DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    String formattedDate = c.localTime.format(myFormatObj);
                    rec.add(formattedDate);
                    rec.add(Integer.toString(c.ClientID));
                    normalRecords.add(rec);
                }
            }
            pt.writeFileData(premiumPath, premiumRecords);
            nt.writeFileData(normalPath, normalRecords);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("All data was loaded back in our database");
    }
    public void addNormalClient() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the name of the normal client");
        String myName = sc.nextLine();
        LocalDateTime myObj = LocalDateTime.now();
        NormalClient d = new NormalClient(myName, this.CurrentDay, myObj);
        this.AllClients.add(d);
        System.out.println(this.AllClients.get(AllClients.size() - 1));
    }
    public void proceedWithPremiumTransaction() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the name of the premium client");
        String myName = sc.nextLine();
        System.out.println("Enter the initial currency");
        String myInitialCurrency = sc.nextLine();
        System.out.println("Enter the final currency");
        String myFinalCurrency = sc.nextLine();
        try {
            if (AllCurrencies.contains(myInitialCurrency) == false || AllCurrencies.contains(myFinalCurrency) == false) {
                throw new MyInputException("One of your selected currencies does not exist in our DataBase");
            }
        } catch (MyInputException e) {
            e.printStackTrace();
        }
        System.out.println("Enter the sum of money");
        double mySumOfMoney = Double.parseDouble(sc.nextLine());
        boolean foundClient = false;
        int positionFound = -1;
        for (int i = 0; i < this.AllClients.size(); i++) {
            if (this.AllClients.get(i).getName().equals(myName)) {
                foundClient = true;
                positionFound = i;
                break;
            }
        }
        try {
            if (foundClient == false) {
                throw new MyInputException("The normal client does not exist try to create it");
            }
        } catch (MyInputException e) {
            e.printStackTrace();
        }
        double x, conv = this.obtainConversionRate(myInitialCurrency, myFinalCurrency);
        LocalDateTime myObj = LocalDateTime.now();
        if (this.CurrenciesAmounts.get(myFinalCurrency) > conv * mySumOfMoney) {
            PremiumTransaction t = new PremiumTransaction(mySumOfMoney, myInitialCurrency, myFinalCurrency, this.getCurrentDay(), conv, this.PremiumCommission, this.getCurrenciesParities(myInitialCurrency, myFinalCurrency), myObj,  this.AllClients.get(positionFound).getClientID());
            this.TotalProfit += t.getTransactionProfit();
            Transaction u = new Transaction(mySumOfMoney, myInitialCurrency, myFinalCurrency, this.getCurrentDay(), myObj, this.AllClients.get(positionFound).getClientID());
            this.AllClients.get(positionFound).addTransaction(u, this.getCurrentDay(), conv, this.PremiumCommission, this.getCurrenciesParities(myInitialCurrency, myFinalCurrency), myObj, this.AllClients.get(positionFound).getClientID());
            this.AllTransactions.add(t);
            x = t.getExitSum();
            System.out.println("Your money are " + x + " " + myFinalCurrency);
            this.setCurrencyAmount(myInitialCurrency, this.CurrenciesAmounts.get(myInitialCurrency) + mySumOfMoney); //change the volume on entry currency
            this.setCurrencyAmount(myFinalCurrency, this.CurrenciesAmounts.get(myFinalCurrency) - t.getExitSum()); //change the volume on exit currency
           // your_string= your_string.replaceAll("\\s*,\\s*", ",");
        } else {
            System.out.println("Insufficient currency in the HOUSE for your transaction, sorry");
        }

    }


    public void proceedWithNormalTransaction() {
        System.out.println(this.AllClients);
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the name of the normal client");
        String myName = sc.nextLine();
        System.out.println("Enter the initial currency");
        String myInitialCurrency = sc.nextLine();
        System.out.println("Enter the final currency");
        String myFinalCurrency = sc.nextLine();
        try {
            if (AllCurrencies.contains(myInitialCurrency) == false || AllCurrencies.contains(myFinalCurrency) == false) {
                throw new MyInputException("One of your selected currencies does not exist in our DataBase");
            }
        } catch (MyInputException e) {
            e.printStackTrace();
        }
        System.out.println("Enter the sum of money");
        double mySumOfMoney = Double.parseDouble(sc.nextLine());
        boolean foundClient = false;
        int positionFound = -1;
        for (int i = 0; i < this.AllClients.size(); i++) {
            if (AllClients.get(i).getName().equals(myName)) {
                foundClient = true;
                positionFound = i;
                break;
            }
        }
        try {
            if (foundClient == false) {
                throw new MyInputException("The normal client does not exist try to create it");
            }
        } catch (MyInputException e) {
            e.printStackTrace();
        }
        double x, conv = this.obtainConversionRate(myInitialCurrency, myFinalCurrency);
        LocalDateTime myObj = LocalDateTime.now();
        if (this.CurrenciesAmounts.get(myFinalCurrency) > conv * mySumOfMoney) {
            NormalTransaction t = new NormalTransaction(mySumOfMoney, myInitialCurrency, myFinalCurrency, this.getCurrentDay(), conv, this.NormalCommission, this.getCurrenciesParities(myInitialCurrency, myFinalCurrency), myObj,  this.AllClients.get(positionFound).getClientID());
            this.TotalProfit += t.getTransactionProfit();
            Transaction u = new Transaction(mySumOfMoney, myInitialCurrency, myFinalCurrency, this.getCurrentDay(), myObj, this.AllClients.get(positionFound).getClientID());
            this.AllClients.get(positionFound).addTransaction(u, this.getCurrentDay(), conv, this.NormalCommission, this.getCurrenciesParities(myInitialCurrency, myFinalCurrency), myObj, this.AllClients.get(positionFound).getClientID());
            this.AllTransactions.add(t);
            x = t.getExitSum();
            System.out.println("Your money are " + x + " " + myFinalCurrency);
            this.setCurrencyAmount(myInitialCurrency, this.CurrenciesAmounts.get(myInitialCurrency) + mySumOfMoney); //change the volume on entry currency
            this.setCurrencyAmount(myFinalCurrency, this.CurrenciesAmounts.get(myFinalCurrency) - t.getExitSum()); //change the volume on exit currency
            //your_string= your_string.replaceAll("\\s*,\\s*", ",");
        } else {
            System.out.println("Insufficient currency in the HOUSE for your transaction, sorry");
        }

    }
    public void supplementCurrency() {
        Scanner sc = new Scanner(System.in);
        System.out.println("What is the currency?");
        String entry = sc.nextLine();
        try {
            if (AllCurrencies.contains(entry) == false) {
                throw new MyInputException("The currency is not registered in our Database");
            }
        } catch (MyInputException e) {
            e.printStackTrace();
        }
        System.out.println("What is the amount you would like to deposit?");
        double x = Double.parseDouble(sc.nextLine()), u = this.CurrenciesAmounts.get(entry);
        try {
            if (x <= 0.0) {
                throw new MyInputException("The amount of money to deposit can not be negative or zero");
            }
        } catch (MyInputException e) {
            e.printStackTrace();
        }
        this.CurrenciesAmounts.put(entry, u + x);

    }

    public double getLastTransactionProfit() {
        return this.AllTransactions.get(AllTransactions.size() - 1).TransactionProfit;
    }
    public void incrementCurrentDay() {
        this.CurrentDay += 1;
        CurrenciesParitiesHistory.add(CurrenciesParities);
        this.actualizeCurrencies();
        System.out.println("One day passed. The new exchange is:");
        for (String s: this.CurrenciesParities.keySet()) {
            System.out.println(s + ":" + this.CurrenciesParities.get(s));
        }
    }
    public void addCurrency(String s) {
        this.AllCurrencies.add(s);
    }

    public double obtainConversionRate(String entry, String exit) {
        double first, second;
        String a, b;
        if (entry.equals("dolars") || exit.equals("dolars")) {
            return this.CurrenciesParities.get(entry + "-->" + exit);
        } else {
            a = new String(entry + "-->dolars");
            b = new String("dolars-->" + exit);
        }
        first = this.CurrenciesParities.get(a);
        second = this.CurrenciesParities.get(b);
        return roundAvoid(first * second, 4);
    }

    public void modifyPremiumTax() {
        double x;
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the new tax for premium clients");
        x = Double.parseDouble(sc.nextLine());
        try {
            if (x <= 0.0) {
                throw new MyInputException("The tax for premimum clients cannot be zero or less than zero");
            }
        } catch (MyInputException e) {
            e.printStackTrace();
        }
        this.setPremiumTax(x);
        System.out.println("The new tax for premium clients is " + x + "$");
    }
    public void modifyNormalCommission() {
        double x;
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the new commission for premium clients");
        x = Double.parseDouble(sc.nextLine());
        try {
            if (x < 0.0 || x >= 1.0) {
                throw new MyInputException("The normal commision has to be between 0.01 and 1.00");
            }
        } catch (MyInputException e) {
            e.printStackTrace();
        }
        this.setPremiumCommission(x);
        System.out.println("The new commission for normal clients is " + x * 100 + "%");
    }
    public void modifyPremiumCommission() {
        double x;
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter the new commission for normal clients");
        x = Double.parseDouble(sc.nextLine());
        try {
            if (x < 0.0 || x >= 1.0) {
                throw new MyInputException("The premium commision has to be between 0.01 and 1.00");
            }
        } catch (MyInputException e) {
            e.printStackTrace();
        }
        this.setPremiumCommission(x);
        System.out.println("The new commission for premium clients is " + x * 100 + "%");
    }
    public void getTotalProfitPerDay() {
        double total = 0;
        System.out.println("What is the day for which you want to know the profit?");
        Scanner sc = new Scanner(System.in);
        int day = sc.nextInt();
        for (int i = 0; i < AllTransactions.size(); i++) {
            if (AllTransactions.get(i).getTransactionDay() == day) {
                total += AllTransactions.get(i).getTransactionProfit();
            }
        }
        System.out.println("The total profit in the day  " + day + " is " + roundAvoid(total, 4));
    }
    public void getTransactionHistoryOfAClient() {
        Scanner sc = new Scanner(System.in);
        System.out.println("What is the name of the client?");
        String myName = sc.nextLine();
        for (int i = 0; i < AllClients.size(); i++) {
            if (AllClients.get(i).name.equals((myName))) {
                if (AllClients.get(i) instanceof NormalClient) {
                    NormalClient A = ((NormalClient) AllClients.get(i));
                    System.out.println(A);
                    System.out.println(myName + " transactions are: ");
                    ArrayList <NormalTransaction> B = A.getMyNormalTransactions();
                    for (int j = 0; j < B.size(); j++) {
                        System.out.println(B.get(j));
                    }
                } else if(AllClients.get(i) instanceof PremiumClient){
                    PremiumClient A = ((PremiumClient) AllClients.get(i));
                    System.out.println(A);
                    System.out.println(myName + " transactions are: ");
                    ArrayList <PremiumTransaction> B = A.getMyPremiumTransactions();
                    for (int j = 0; j < B.size(); j++) {
                        System.out.println(B.get(j));
                    }
                }
    /*
    for(int j=0;j<AllClients.get(i). ;j++){

    }*/
                break;
            }
        }
    }
    public void getAllCurrencies() {
        System.out.println("All currencies are: ");
        for (String key: this.AllCurrencies) {
            System.out.println(key + " with the quantity: " + this.CurrenciesAmounts.get(key));
        }
    }
    public void getLastTransactionInformations() {
        System.out.println(this.AllTransactions.get(AllTransactions.size() - 1));
    }
    public void getInformationsAboutAllTransactionsInACertainDay() {
        Scanner sc = new Scanner(System.in);
        System.out.println("What is the day for which you want to know the informations?");
        int day = sc.nextInt();
        for (int i = 0; i < AllTransactions.size(); i++) {
            if (AllTransactions.get(i).getTransactionDay() == day) {
                System.out.println(AllTransactions.get(i));
            }
        }
    }
    public double getCurrenciesParities(String xc, String s) {
        if (xc.equals("dolars"))
            return CurrenciesParities.get(s + "-->dolars");
        return CurrenciesParities.get(xc + "-->dolars");
    }

    public double getCurrencyAmount(String currency) {
        return this.CurrenciesAmounts.get(currency);
    }
    public void setCurrencyAmount(String currency, Double amount) {
        CurrenciesAmounts.put(currency, amount);
    }

    public void setCurrencyPair(String firstCurrency, String secondCurrency, Double exchangeCoefficient) {
        String oneWay = firstCurrency + "-->" + secondCurrency;
        this.CurrenciesParities.put(oneWay, roundAvoid(exchangeCoefficient, 4));
        String reverseWay = secondCurrency + "-->" + firstCurrency;
        this.CurrenciesParities.put(reverseWay, roundAvoid(1 / exchangeCoefficient, 4));
    }

    static double generateRandomModificationCoefficient(double number) {
        double min = number - number * 0.05, max = number + number * 0.05;
        Random r = new Random();
        double randomValue;
        do {
            randomValue = min + (max - min) * r.nextDouble();
        } while (randomValue <= 0);
        return randomValue;
    }

    public void actualizeCurrencies() {
  /*
  This function generates for a currency pair a fluctuation of at most 5%(either on plus or on minus) in order to make
  the currencies flow. This happens after each day and a day passes after 3 made operations, any operations are considered
  to have the same length of time, from adding a new client to a new transaction for a client we already have.
   */
        Set < String > keys = CurrenciesParities.keySet();
        String prefix = "dolars-->";
        for (String key: keys) {
            if (key.startsWith(prefix) == true) {
                double newCoefficient = generateRandomModificationCoefficient(CurrenciesParities.get(key));
                CurrenciesParities.put(key, roundAvoid(newCoefficient, 4));
                int firstDelimitation = key.indexOf("-");
                int lastDelimitation = key.indexOf(">");
                String newKey = key.substring(lastDelimitation + 1) + "-->" + key.substring(0, firstDelimitation);
                CurrenciesParities.put(newKey, roundAvoid(1 / newCoefficient, 4));
            }
        }
    }
    /*
    void drawHistoryParity(){
        String entry,exit;
        Scanner sc=new Scanner(System.in);
        System.out.println("What is your first currency?");
        entry=sc.nextLine();
        System.out.println("What is your second currency?");
        exit=sc.nextLine();
    }
    */
    public void modifyPremiumClient(){
        System.out.println("Please enter the id of the premium client");
        Scanner sc = new Scanner(System.in);
        int id = sc.nextInt();
        System.out.println("Please enter the new name of the premium client with the id "+id);
        String newName = sc.nextLine();
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            PreparedStatement preparedStatement = conn
                    .prepareStatement("UPDATE PREMIUMCLIENT SET NUME=? WHERE CLIENT_ID =?");
            preparedStatement.setString(1,newName);
            preparedStatement.setInt(2,id);
            preparedStatement.executeUpdate();
            conn.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        for(int i=0;i<AllClients.size();i++){
            if(AllClients.get(i).ClientID == id){
                AllClients.get(i).setName(newName);
            }
        }
        System.out.println("The name of the client with the id "+id+" was changed");
    }
    public void modifyNormalClient(){
        System.out.println("Please enter the id of the normal client");
        Scanner sc = new Scanner(System.in);
        int id = sc.nextInt();
        System.out.println("Please enter the new name of the normal client with the id "+id);
        String newName = sc.nextLine();
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            PreparedStatement preparedStatement = conn
                    .prepareStatement("UPDATE NORMALCLIENT SET NUME=? WHERE CLIENT_ID =?");
            preparedStatement.setString(1,newName);
            preparedStatement.setInt(2,id);
            preparedStatement.executeUpdate();
            conn.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        for(int i=0;i<AllClients.size();i++){
            if(AllClients.get(i).ClientID == id){
                AllClients.get(i).setName(newName);
            }
        }
        System.out.println("The name of the client with the id "+id+" was changed");
    }
    public void modifyPremiumTransaction(){
        System.out.println("Please enter the id of the premium client whose transactions you want to delete ");
        Scanner sc = new Scanner(System.in);
        int id = sc.nextInt();
        System.out.println("Please enter the new id for the client with the id "+id);
        int nid = sc.nextInt();
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            PreparedStatement preparedStatement = conn
                    .prepareStatement("UPDATE PREMIUMTRANSACTION SET CLIENT_ID=? WHERE CLIENT_ID =?");
            preparedStatement.setInt(1,nid);
            preparedStatement.setInt(2,id);
            preparedStatement.executeUpdate();
            conn.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        for(int i=0;i<AllTransactions.size();i++){
            if(AllTransactions.get(i).ClientID == id){
                AllTransactions.get(i).ClientID = id;
            }
        }
        for(int i=0;i<AllClients.size();i++){
            if(AllClients.get(i).ClientID == id){
                AllClients.get(i).setClientID(id);
            }
        }
        System.out.println("The transactions of the client with the id "+id+" were deleted");
    }
    public void modifyNormalTransaction(){
        System.out.println("Please enter the id of the normal client whose transactions you want to delete ");
        Scanner sc = new Scanner(System.in);
        int id = sc.nextInt();
        System.out.println("Please enter the new id for the client with the id "+id);
        int nid = sc.nextInt();
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            PreparedStatement preparedStatement = conn
                    .prepareStatement("UPDATE NORMALTRANSACTION SET CLIENT_ID=? WHERE CLIENT_ID =?");
            preparedStatement.setInt(1,nid);
            preparedStatement.setInt(2,id);
            preparedStatement.executeUpdate();
            conn.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        for(int i=0;i<AllTransactions.size();i++){
            if(AllTransactions.get(i).ClientID == id){
                AllTransactions.get(i).ClientID = id;
            }
        }
        for(int i=0;i<AllClients.size();i++){
            if(AllClients.get(i).ClientID == id){
                AllClients.get(i).setClientID(id);
            }
        }
        System.out.println("The transactions of the client with the id "+id+" were deleted");
    }

    public void deletePremiumClient(){
        System.out.println("Please enter the id of the premium client");
        Scanner sc = new Scanner(System.in);
        int id = sc.nextInt();
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            PreparedStatement preparedStatement = conn
                    .prepareStatement("DELETE FROM PREMIUMCLIENT WHERE CLIENT_ID =?");
            preparedStatement.setInt(1,id);
            preparedStatement.executeUpdate();
            conn.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        for(int i=0;i<AllClients.size();i++){
            if(AllClients.get(i).ClientID == id){
                AllClients.remove(AllClients.get(i));
            }
        }
        System.out.println("The client with the id "+id+" was deleted");
    }
    public void deleteNormalClient(){
        System.out.println("Please enter the id of the normal client");
        Scanner sc = new Scanner(System.in);
        int id = sc.nextInt();
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            PreparedStatement preparedStatement = conn
                    .prepareStatement("DELETE FROM NORMALCLIENT WHERE CLIENT_ID =?");
            preparedStatement.setInt(1,id);
            preparedStatement.executeUpdate();
            conn.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        for(int i=0;i<AllClients.size();i++){
            if(AllClients.get(i).ClientID == id){
                AllClients.remove(AllClients.get(i));
            }
        }
        System.out.println("The client with the id "+id+" was deleted");
    }
    public void deletePremiumTransaction(){
        System.out.println("Please enter the id of the premium client whose transactions you want to delete ");
        Scanner sc = new Scanner(System.in);
        int id = sc.nextInt();
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            PreparedStatement preparedStatement = conn
                    .prepareStatement("DELETE FROM PREMIUMTRANSACTION WHERE CLIENT_ID =?");
            preparedStatement.setInt(1,id);
            preparedStatement.executeUpdate();
            conn.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        for(int i=0;i<AllTransactions.size();i++){
            if(AllTransactions.get(i).ClientID == id){
                AllTransactions.remove(AllTransactions.get(i));
            }
        }
        for(int i=0;i<AllClients.size();i++){
            if(AllClients.get(i).ClientID == id){
                AllClients.remove(AllClients.get(i));
            }
        }
        System.out.println("The transactions of the client with the id "+id+" were deleted");
    }
    public void deleteNormalTransaction(){
        System.out.println("Please enter the id of the normal client whose transactions you want to delete ");
        Scanner sc = new Scanner(System.in);
        int id = sc.nextInt();
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            PreparedStatement preparedStatement = conn
                    .prepareStatement("DELETE FROM NORMALTRANSACTION WHERE CLIENT_ID =?");
            preparedStatement.setInt(1,id);
            preparedStatement.executeUpdate();
            conn.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        for(int i=0;i<AllTransactions.size();i++){
            if(AllTransactions.get(i).ClientID == id){
                AllTransactions.remove(AllTransactions.get(i));
            }
        }
        for(int i=0;i<AllClients.size();i++){
            if(AllClients.get(i).ClientID == id){
                AllClients.remove(AllClients.get(i));
            }
        }
        System.out.println("The transactions of the client with the id "+id+" were deleted");
    }
    public void saveAllDataToFile() {
        try {
            FileOutputStream f = new FileOutputStream("HouseHistory.txt");
            ObjectOutputStream o = new ObjectOutputStream(f);
            for (int i = 0; i < AllClients.size(); i++) {
                if (AllClients.get(i) instanceof NormalClient) {
                    NormalClient A = ((NormalClient) AllClients.get(i));
                    o.writeObject(A);
                    //o.writeObject(AllClients.get(i).getName() + " transactions are: ");
                    ArrayList <NormalTransaction> B = A.getMyNormalTransactions();
                    for (int j = 0; j < B.size(); j++) {
                        o.writeObject(B.get(i));
                    }
                } else {
                    PremiumClient A = ((PremiumClient) AllClients.get(i));
                    o.writeObject(A);
                    //o.writeObject(AllClients.get(i).getName() + " transactions are: ");
                    ArrayList <PremiumTransaction> B = A.getMyPremiumTransactions();
                    for (int j = 0; j < B.size(); j++) {
                        o.writeObject(B.get(i));
                    }
                }
            }
            o.close();
            f.close();
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        } catch (IOException e) {
            System.out.println("Error initializing stream");
        } finally {
            System.out.println("All the details were written successfully in the HouseHistory.txt file");
        }
    }
    public void readNumberToAction(){
            try
            {
                File file=new File("Actions.txt");    //creates a new file instance
                FileReader fr=new FileReader(file);   //reads the file
                BufferedReader br=new BufferedReader(fr);  //creates a buffering character input stream
                //StringBuffer sb=new StringBuffer();    //constructs a string buffer with no characters
                String line;
                while((line=br.readLine())!=null)
                {
                    for(int i=0;i<line.length();i++){
                        if(line.charAt(i)=='.'){
                            int num = Integer.parseInt(line.substring(0,i));
                            String act = line.substring(i+1,line.length());
                            this.numberToAction.put(num,act);
                            break;
                        }
                    }
                }
                fr.close();    //closes the stream and release the resources
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
    }
    public void clearDataBase(){
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            PreparedStatement preparedStatement;
            preparedStatement = conn.prepareStatement("DELETE FROM PREMIUMCLIENT");
            preparedStatement.executeUpdate();
            /*preparedStatement = conn.prepareStatement("DELETE FROM PREMIUMTRANSACTION");
            preparedStatement.executeUpdate();
            preparedStatement = conn.prepareStatement("DELETE FROM NORMALCLIENT");
            preparedStatement.executeUpdate();
            preparedStatement = conn.prepareStatement("DELETE FROM NORMALTRANSACTION");
            preparedStatement.executeUpdate();*/
            preparedStatement = conn.prepareStatement("COMMIT");
            preparedStatement.executeUpdate();
            conn.commit();
            conn.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}