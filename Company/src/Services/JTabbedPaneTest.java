package Services;

import Entities.OfficeExchange;
import oracle.jdbc.driver.OracleDriver;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.Spliterator;
import javax.swing.*;

public class JTabbedPaneTest {

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

    public static void main(String args[]) throws IOException {
        OfficeExchange of = OfficeExchange.getInstance();
        of.addCurrency("euros"); of .addCurrency("dolars"); of .addCurrency("yens"); of .setCurrencyAmount("euros", 10000.0); of .setCurrencyAmount("dolars", 10000.0); of .setCurrencyAmount("yens", 100000000.0); of .setCurrencyPair("dolars", "euros", 0.9343); of .setCurrencyPair("dolars", "yens", 111.1095);
        of.loadAllData();
        JButton[] button = new JButton[5];
        button[0] = new JButton("Change the tax for premium clients");
        button[1] = new JButton("Change the commission for premium clients");
        button[2] = new JButton("Change the commission for normal clients");
        button[3] = new JButton("Supplement the amount of money for a certain currency");
        button[4] = new JButton("Obtain total profit");

        JFrame frame = new JFrame("Tabbed Pane Sample");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane tabbedPane = new JTabbedPane();

        String titles[] = { "Change taxes and commisions", "Profit and currency amounts" };
        int mnemonic[] = { KeyEvent.VK_G, KeyEvent.VK_S };
        for (int i = 0, n = titles.length; i < n; i++) {
            add(tabbedPane, titles[i], mnemonic[i],i,button,of);
        }

        frame.add(tabbedPane, BorderLayout.CENTER);
        frame.setSize(400, 150);
        frame.setVisible(true);
    }
}