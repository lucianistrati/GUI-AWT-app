package Services;

import Audit.AuditService;
import Entities.OfficeExchange;
import Exceptions.MyInputException;
import oracle.jdbc.driver.OracleDriver;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.io.*;
import java.lang.*;
import java.io.IOException;
//The service class
public class Main {
    /**
     *    The dolar is the reference currency for this Office Exchange
     *   The way conversion is done from a currency A to a currency B is by converting A to dolars and then from dolars to B
     *    When we set currency pair it is assumed that The first currency equals to the second currency times the currency factor.
     *    Traditionally, when we are talking about any transactions involving currencies we need to define the quotations for currencies
     *    using the first four decimals in our transactions. This is also a good practice in computer science because we reduce
     *    the overhead for doing calculations with numbers which have an infinite number of digits after the floating point.
     *
     *    The graphical interface of this application is going to implement the actions from 5 to 9 from the menu in
     *    the following manner:
     *    First, a central menu will be displayed and from that menu the user will have two options:
     *
     *    ->The second screen will help us with:
     *    a)Change taxes or commission)
     *
     *    a)1)Prem Comm
     *    a)2)Norm Comm
     *    a)3)Prem Tax
     *
     *    ->The second screen will help us with:
     *    b)Other actions
     *
     *    b)1)Find out the total profit of the Office Exchange
     *    b)2)Supplement the amount for a certain currency present in the database of available currencies
     *
     */
    static void add(JTabbedPane tabbedPane, String label, int mnemonic, int i, JButton[] button, OfficeExchange of) {
        JPanel panel = new JPanel();
        if(i==0) {
            SetActionCommandForJButton s0= new SetActionCommandForJButton(button[0],"5",of);
            panel.add(button[0]);
            SetActionCommandForJButton s1= new SetActionCommandForJButton(button[1],"6",of);
            panel.add(button[1]);
            SetActionCommandForJButton s2= new SetActionCommandForJButton(button[2],"7",of);
            panel.add(button[2]);
        }else{
            SetActionCommandForJButton s3= new SetActionCommandForJButton(button[3],"8",of);
            panel.add(button[3]);
            SetActionCommandForJButton s4= new SetActionCommandForJButton(button[4],"9",of);
            panel.add(button[4]);
        }

        int count = tabbedPane.getTabCount();
        tabbedPane.addTab(label, new ImageIcon("a.gif"), panel, label);
        tabbedPane.setMnemonicAt(count, mnemonic);

    }


    public static void main(String[] args) throws SQLException {
        System.out.println("Welcome to the Office Exchange App");
                String DB_URL = "jdbc:oracle:thin:@193.226.51.37:1521:o11g";
            String USER = "grupa35";
            String PASS = "bazededate";
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
        int checker = 0;
        boolean check = true;


        AuditService c = AuditService.getInstance();

        OfficeExchange of = OfficeExchange.getInstance();
        of.addCurrency("euros"); of .addCurrency("dolars"); of .addCurrency("yens"); of .setCurrencyAmount("euros", 10000.0); of .setCurrencyAmount("dolars", 10000.0); of .setCurrencyAmount("yens", 100000000.0); of .setCurrencyPair("dolars", "euros", 0.9343); of .setCurrencyPair("dolars", "yens", 111.1095);
        of.readNumberToAction();
        //of.loadAllData();
        of.loadDataFromAllDatabases();
        System.out.println(of.getAllClients());
        System.out.println(of.getAllTransactions());

        JButton[] button = new JButton[5];
        button[0] = new JButton("Change the tax for premium clients");
        button[1] = new JButton("Change the commission for premium clients");
        button[2] = new JButton("Change the commission for normal clients");
        button[3] = new JButton("Supplement the amount of money for a certain currency");
        button[4] = new JButton("Obtain total profit");

        JFrame frame = new JFrame("Office Exchange App");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();

        String titles[] = { "Change taxes and commisions", "Profit and currency amounts" };
        int mnemonic[] = { KeyEvent.VK_G, KeyEvent.VK_S };
        for (int i = 0, n = titles.length; i < n; i++) {
            add(tabbedPane, titles[i], mnemonic[i],i,button,of);
        }

        frame.add(tabbedPane, BorderLayout.CENTER);
        frame.setSize(700, 350);
        frame.setVisible(true);

        while (check == true) {
            if (checker == 0) {
                System.out.println("You have the following options:");
                System.out.println("1.Create premium client");
                System.out.println("2.Create normal client");
                System.out.println("3.Make a premium transaction");
                System.out.println("4.Make a normal transaction");
                /*
                System.out.println("5.Change the tax for premium clients");
                System.out.println("6.Change the commission for premium clients");
                System.out.println("7.Change the commission for normal clients");
                System.out.println("8.Supplement the amount of money for a certain currency");
                System.out.println("9.Obtain total profit");
                */
                System.out.println("10.Obtain last transaction profit");
                System.out.println("11.Obtain the profit from a certain day");
                System.out.println("12.Obtain the price of a Currency Pair");
                System.out.println("13.Obtain transaction history of a client");
                System.out.println("14.Obtain all currencies available for transactions");
                System.out.println("15.Obtain last transaction informations");
                System.out.println("16.Obtain informations about all the transactions from a certain day");
                System.out.println("17.Make a day pass");
                System.out.println("18.Modify a premium client");
                System.out.println("19.Modify a normal client");
                System.out.println("20.Modify a normal transaction");
                System.out.println("21.Modify a premium transaction");
                System.out.println("22.Delete a premium client");
                System.out.println("23.Delete a normal client");
                System.out.println("24.Delete a normal transaction");
                System.out.println("25.Delete a premium transaction");
                //System.out.println("26.Draw the history of a parity");
                System.out.println("26.Clear DataBase");
                System.out.println("27.Exit from the program");
                checker = 1;
            }

            Scanner sc = new Scanner(System.in);
            int option = sc.nextInt();
            try {
                if (option < 1 || option > 27) {
                    throw new MyInputException("Not a valid menu option");
                }
            } catch (MyInputException e) {
                e.printStackTrace();
            }
            double x;
            if(option>=1 && option<=27){
                of.getT().add(of.getNumberToAction().get(option));
                LocalDateTime myObj = LocalDateTime.now();
                DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                String formattedDate = myObj.format(myFormatObj);
                of.getDates().add(formattedDate);
            }
            if(check==true){
                String threadName = "Thread no. " + of.getThreadCount();
                of.incrementThreadCount();
                of.addToThreadList(threadName);
                MyRunnableThread thr = new MyRunnableThread(option,check,threadName,of);
                System.out.println(check);
                thr.run();
                of = thr.getOfficeExchange();
                if(option==27)
                    check=false;
            }
        }
        try {
            System.out.println(of.getT());
            System.out.println(of.getDates());
            System.out.println(of.getThreadList());
            c.writeInAuditFile(of.getT(), of.getDates(), of.getThreadList());
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            of.addAllData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        conn.commit();
        System.out.println("Hope you had a good time, see you soon!");
    }
}