package Services;

import Entities.OfficeExchange;
import oracle.jdbc.driver.OracleDriver;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SetActionCommandForJButton extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1L;
    private String idx;
    private OfficeExchange of;
    public SetActionCommandForJButton(JButton button, String idx, OfficeExchange of) {
        button.addActionListener(this);
        button.setActionCommand(idx);
        this.idx = idx;
        this.of = of;
    }
    public OfficeExchange getOfficeExchange(){
        return this.of;
    }
    @Override
    public void actionPerformed(ActionEvent ae) {
        String action = ae.getActionCommand();
        if (action.equals("5")) {//void action
            Runnable runnable = () -> {
                        of.getT().add(of.getNumberToAction().get(5));
                        LocalDateTime myObj = LocalDateTime.now();
                        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        String formattedDate = myObj.format(myFormatObj);
                        of.getDates().add(formattedDate);
                        String threadName = "Thread no. " + of.getThreadCount();
                        of.addToThreadList(threadName);
                        of.incrementThreadCount();
                        String num1;
                        double number1;
                        num1 = JOptionPane.showInputDialog("Please insert the new tax for premium clients");
                        number1 = Double.parseDouble(num1);
                        this.of.setPremiumTax(number1);

                        JOptionPane.showMessageDialog(null, "The tax for premium clients was set to " + this.of.getPremiumTax() + "$");
                    };
            Thread thread = new Thread(runnable);
            thread.start();
        }
        if (action.equals("6")) {//void action
            Runnable runnable = () -> {
            of.getT().add(of.getNumberToAction().get(6));
            LocalDateTime myObj = LocalDateTime.now();
            DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDate = myObj.format(myFormatObj);
            of.getDates().add(formattedDate);
            String threadName = "Thread no. " + of.getThreadCount();
            of.addToThreadList(threadName);
                of.incrementThreadCount();
            String num1;
            double number1;
            num1 = JOptionPane.showInputDialog("Please insert the new commission for premium clients");
            number1 = Double.parseDouble(num1);
            this.of.setPremiumCommission(number1);

            JOptionPane.showMessageDialog(null, "The comission for premium clients was set to " + this.of.getPremiumCommission()*100 + "%");
            };
            Thread thread = new Thread(runnable);
            thread.start();
        }
        if (action.equals("7")) {//void action
            Runnable runnable = () -> {
            of.getT().add(of.getNumberToAction().get(7));
            LocalDateTime myObj = LocalDateTime.now();
            DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDate = myObj.format(myFormatObj);
            of.getDates().add(formattedDate);
            String threadName = "Thread no. " + of.getThreadCount();
            of.addToThreadList(threadName);
                of.incrementThreadCount();
            String num1;
            double number1;
            num1 = JOptionPane.showInputDialog("Please insert the new commission for normal clients");
            number1 = Double.parseDouble(num1);

            this.of.setNormalCommission(number1);

            JOptionPane.showMessageDialog(null, "The comission for normal clients was set to " + this.of.getNormalCommission()*100 + "%");
            };
            Thread thread = new Thread(runnable);
            thread.start();
        }
        if (action.equals("8")) {//void action with 2 questions
            Runnable runnable = () -> {
            of.getT().add(of.getNumberToAction().get(8));
            LocalDateTime myObj = LocalDateTime.now();
            DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDate = myObj.format(myFormatObj);
            of.getDates().add(formattedDate);
            String threadName = "Thread no. " + of.getThreadCount();
            of.addToThreadList(threadName);
                of.incrementThreadCount();
            String curr,num1;
            double number1;

            curr = JOptionPane.showInputDialog("What is the currency you would like to supplement?");

            num1 = JOptionPane.showInputDialog("What is the amount you would like to add?");

            number1 = Double.parseDouble(num1);

            this.of.setCurrencyAmount(curr,this.of.getCurrencyAmount(curr) + number1);

            JOptionPane.showMessageDialog(null, "The amount of " + curr + " was suplemented with " + number1 + " units");
            };
            Thread thread = new Thread(runnable);
            thread.start();

            }
        if (action.equals("9")) {
            Runnable runnable = () -> {
            of.getT().add(of.getNumberToAction().get(9));
            LocalDateTime myObj = LocalDateTime.now();
            DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            String formattedDate = myObj.format(myFormatObj);
            String threadName = "Thread no. " + of.getThreadCount();
            of.addToThreadList(threadName);
                of.incrementThreadCount();
            of.getDates().add(formattedDate);
            JOptionPane.showMessageDialog(null, "The total profit is " + this.of.getTotalProfit() + "$");
            };
            Thread thread = new Thread(runnable);
            thread.start();
        }
    }
}
