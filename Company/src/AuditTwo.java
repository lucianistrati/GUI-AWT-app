import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AuditTwo {

    public static void main(String[]args){
        LocalDateTime myObj = LocalDateTime.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDate = myObj.format(myFormatObj);
        System.out.println("After formatting: " + formattedDate);
    }
}
