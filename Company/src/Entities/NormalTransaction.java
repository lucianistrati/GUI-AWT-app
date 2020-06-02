package Entities;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.io.*;
import java.time.ZoneId;
import java.util.*;
import oracle.jdbc.driver.OracleDriver;
public class NormalTransaction extends Transaction implements Serializable {
    static final String DB_URL = "jdbc:oracle:thin:@193.226.51.37:1521:o11g";
    static final String USER = "grupa35";
    static final String PASS = "bazededate";
    private static double NormalTransactionsProfit = 0.0;
    private static java.sql.Date convertUtilToSql(java.util.Date uDate) {
        System.out.println(uDate);
        java.sql.Date sDate = new java.sql.Date(uDate.getTime());
        System.out.println(sDate);
        return sDate;
    }


    public NormalTransaction(double es, String ec, String xc, int day, double conversionRate, double normCommission, double currParity, LocalDateTime local,int id) {
        super(es, ec, xc, day, local,id);
        double x = Transaction.roundAvoid(conversionRate, 4);
        this.TransactionProfit = Transaction.roundAvoid(normCommission * es * x, 4); // profit obtained in exit currency
        this.ExitSum = Transaction.roundAvoid(es * x - this.TransactionProfit, 4); // exit sum in exit currency
        if (xc.equals("dolars") == false) {
            this.TransactionProfit = Transaction.roundAvoid(currParity * this.TransactionProfit, 4);
        }
        //Inserting data in our database
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM NORMALTRANSACTION WHERE CLIENT_ID=? AND TIMP=?");
            preparedStatement.setInt(1,super.ClientID);
            Timestamp timestamp = Timestamp.valueOf(local);
            preparedStatement.setTimestamp(2, timestamp);
            int aux = preparedStatement.executeUpdate();
            if(aux==0) {
                preparedStatement = conn
                        .prepareStatement("INSERT INTO NORMALTRANSACTION(INITIAL_SUM, INITIAL_CURRENCY, FINAL_SUM, FINAL_CURRENCY, TRANSACTION_PROFIT, ZI, TIMP, CLIENT_ID) VALUES(?,?,?,?,?,?,?,?)");
                preparedStatement.setFloat(1, (float) es);
                preparedStatement.setString(2, ec);
                preparedStatement.setFloat(3, (float) this.ExitSum);
                preparedStatement.setString(4, xc);
                preparedStatement.setFloat(5, (float) this.TransactionProfit);
                preparedStatement.setInt(6, day);
                preparedStatement.setTimestamp(7, timestamp);
                preparedStatement.setFloat(8, id);
                preparedStatement.executeUpdate();
            }
            conn.close();
        }catch(Exception e){
            e.printStackTrace();
        }




        NormalTransactionsProfit += this.TransactionProfit;
    }
    double getNormalTransactionsProfit() {
        return NormalTransactionsProfit;
    }
    void setNormalTransactionsProfit(double val) {
        NormalTransactionsProfit = val;
    }
    public String toString() {
        return super.toString();
    }
}