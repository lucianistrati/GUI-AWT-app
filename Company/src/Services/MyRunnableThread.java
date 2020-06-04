package Services;

import Entities.OfficeExchange;
import oracle.jdbc.driver.OracleDriver;
import java.util.Optional;
import java.util.Scanner;

public class MyRunnableThread implements Runnable{
    private int option;
    private boolean check;
    private String threadName;
    OfficeExchange of;
    MyRunnableThread(){

    }
    MyRunnableThread( int option, boolean check,String threadName,OfficeExchange of){
        this.option =option;
        this.check = check;
        this.threadName = threadName;
        this.of = of;
    }
    public void setThread(int option,boolean check, String threadName){
        this.option =option;
        this.check = check;
        this.threadName = threadName;
    }
    public OfficeExchange getOfficeExchange(){
        return this.of;
    }
    @Override
    public void run() {
        switch (option) {
            case 1:
                of.addPremiumClient();
                break;
            case 2:
                of.addNormalClient();
                break;
            case 3:
                of.proceedWithPremiumTransaction();
                break;
            case 4:
                of.proceedWithNormalTransaction();
                break;
                /*
            case 5:
                //covered by the graphical interface
                //from here   |
                //            v
                of.modifyPremiumTax();
                break;
            case 6:
                of.modifyPremiumCommission();
                break;
            case 7:
                of.modifyNormalCommission();
                break;
            case 8:
                of.supplementCurrency();
                break;
            case 9:
                System.out.println("The total profit is " + of .getTotalProfit());
                break;
                */
                //            ^
                //till here   |
            case 10:
                System.out.println("The last transaction profit is " + of .getLastTransactionProfit());
                break;
            case 11:
                of.getTotalProfitPerDay();
                break;
            case 12:
                Scanner sca = new Scanner(System.in);
                String entry, exit;
                System.out.println("What is the first currency?");
                entry = sca.nextLine();
                System.out.println("What is the second currency?");
                exit = sca.nextLine();
                System.out.println("One " + entry.substring(0, entry.length() - 1) + " is " + exit.substring(0, exit.length() - 1) + " " + of .obtainConversionRate(entry, exit));
                break;
            case 13:
                of.getTransactionHistoryOfAClient();
                break;
            case 14:
                of.getAllCurrencies();
                break;
            case 15:
                of.getLastTransactionInformations();
                break;
            case 16:
                of.getInformationsAboutAllTransactionsInACertainDay();
                break;
            case 17:
                of.incrementCurrentDay();
                break;
            case 18:
                of.modifyPremiumClient();
                break;
            case 19:
                of.modifyNormalClient();
                break;
            case 20:
                of.modifyPremiumTransaction();
                break;
            case 21:
                of.modifyNormalTransaction();
                break;
            case 22:
                of.deletePremiumClient();
                break;
            case 23:
                of.deleteNormalClient();
                break;
            case 24:
                of.deletePremiumTransaction();
                break;
            case 25:
                of.deleteNormalTransaction();
                break;
            case 26:
                System.out.println("You cleared the DataBase");
                of.clearDataBase();
                break;
            case 27:
                System.out.println("You exited the application");
                check = false;
                break;
            //case 19:
            //  of.drawHistoryParity();
            //break;
        }
    }
}
