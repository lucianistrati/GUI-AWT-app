package Entities;

import Entities.Client;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.io.*;
import oracle.jdbc.driver.OracleDriver;
import java.util.Date;
public class NormalClient extends Client implements Serializable {
    private ArrayList <NormalTransaction> MyNormalTransactions;
    static final String DB_URL = "jdbc:oracle:thin:@193.226.51.37:1521:o11g";
    static final String USER = "grupa35";
    static final String PASS = "bazededate";
    public NormalClient(String myName, int day, LocalDateTime local) {
        super(myName, day, local);
        //Inserting data in our database

        try {
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM NORMALCLIENT WHERE CLIENT_ID=?");
            preparedStatement.setInt(1,super.ClientID);
            int aux = preparedStatement.executeUpdate();
            if(aux==0) {
                preparedStatement = conn
                        .prepareStatement("INSERT INTO NORMALCLIENT(CLIENT_ID,NUME,ZI,TIMP) VALUES(?,?,?,?)");
                preparedStatement.setInt(1, super.ClientID);
                preparedStatement.setString(2, myName);
                preparedStatement.setInt(3, day);
                Timestamp timestamp = Timestamp.valueOf(local);
                System.out.println(local);
                preparedStatement.setTimestamp(4, timestamp);
                preparedStatement.executeUpdate();
            }
            conn.close();
        }catch(Exception e){
            e.printStackTrace();
        }

        MyNormalTransactions = new ArrayList <NormalTransaction> ();
    }
    ArrayList <NormalTransaction> getMyNormalTransactions() {
        return MyNormalTransactions;
    }
    @Override
    void addTransaction(Transaction x, int a, double b, double c, double d, LocalDateTime l, int f) {
        NormalTransaction y = new NormalTransaction(x.EntrySum, x.EntryCurrency, x.ExitCurrency, a, b, c, d, l,f);
        this.MyNormalTransactions.add(y);
    }


}