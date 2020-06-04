package Services;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.*;
import java.lang.*;
import java.time.LocalDateTime; // Import the LocalDateTime class
import java.time.format.DateTimeFormatter; // Import the DateTimeFormatter class


public class CallableStatementTest {

    static final String DB_URL = "jdbc:oracle:thin:@193.226.51.37:1521:o11g";

    static final String USER = "grupa35";
    static final String PASS = "bazededate";

    public static void main(String[] args) {



        try {

            //System.out.println("Connecting to a selected database...");
            Connection conn = DriverManager.getConnection(DB_URL, USER, PASS);
            //System.out.println("Connected database successfully...");
            LocalDateTime myDateObj = LocalDateTime.now();
            System.out.println("Before formatting: " + myDateObj);
            DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
            String formattedDate = myDateObj.format(myFormatObj);
            System.out.println("After formatting: " + formattedDate);
            PreparedStatement preparedStatement = conn
                    .prepareStatement("SELECT * FROM NORMALCLIENT;" + "SELECT * FROM NORMALTRANSACTION;");
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                String lastName = rs.getString(1);
                System.out.println(lastName + "\n");
            }


            //int i = preparedStatement.executeUpdate();
            //System.out.println(i);
            //System.out.println("Connected database successfully...");


            //System.out.println(i + " records updated");


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}