package Entities;
import oracle.jdbc.driver.OracleDriver;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.io.*;
import java.util.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
public class PremiumTransaction extends Transaction implements Serializable {
    static final String DB_URL = "jdbc:oracle:thin:@193.226.51.37:1521:o11g";
    static final String USER = "grupa35";
    static final String PASS = "bazededate";
    private static double PremiumTransactionsProfit = 0.0;
    private static java.sql.Date convertUtilToSql(java.util.Date uDate) {
        System.out.println(uDate);
        java.sql.Date sDate = new java.sql.Date(uDate.getTime());
        System.out.println(sDate);
        return sDate;
    }

    public PremiumTransaction(double es, String ec, String xc, int day, double conversionRate, double premCommission, double currParity, LocalDateTime local,int id) {
        super(es, ec, xc, day, local, id);
        double x = Transaction.roundAvoid(conversionRate, 4);
        this.TransactionProfit = Transaction.roundAvoid(premCommission * es * x, 4); //profit obtained in exit currency
        this.ExitSum = Transaction.roundAvoid(es * x - this.TransactionProfit, 4); //exit sum in exit currency
        if (xc != "dolars") {
            this.TransactionProfit = Transaction.roundAvoid(currParity * this.TransactionProfit, 4);
        }
        //Inserting data in our database
        try {
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM PREMIUMTRANSACTION WHERE CLIENT_ID=? AND TIMP=?");
            preparedStatement.setInt(1,super.ClientID);
            Timestamp timestamp = Timestamp.valueOf(local);
            preparedStatement.setTimestamp(2, timestamp);
            int aux = preparedStatement.executeUpdate();
            if(aux==0) {
                preparedStatement = conn
                        .prepareStatement("INSERT INTO PREMIUMTRANSACTION(INITIAL_SUM, INITIAL_CURRENCY, FINAL_SUM, FINAL_CURRENCY, TRANSACTION_PROFIT, ZI, TIMP, CLIENT_ID) VALUES(?,?,?,?,?,?,?,?)");
                preparedStatement.setFloat(1, (float) es);
                preparedStatement.setString(2, ec);
                preparedStatement.setFloat(3, (float) this.ExitSum);
                preparedStatement.setString(4, xc);
                preparedStatement.setFloat(5, (float) this.TransactionProfit);
                preparedStatement.setInt(6, day);
                preparedStatement.setTimestamp(7, timestamp);
                preparedStatement.setInt(8, id);
                preparedStatement.executeUpdate();
            }
            conn.close();
        }catch(Exception e){
            e.printStackTrace();
        }

        PremiumTransactionsProfit += this.TransactionProfit;
    }
    double getPremiumTransactionsProfit() {
        return PremiumTransactionsProfit;
    }
    void setPremiumTransactionsProfit(double val) {
        PremiumTransactionsProfit = val;
    }
    public String toString() {
        return super.toString();
    }
}