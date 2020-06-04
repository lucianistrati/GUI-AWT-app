package Services;

import Entities.NormalTransaction;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NormalTransactionCSVReader implements CsvReader {
    private static NormalTransactionCSVReader instance =  null;
    private ArrayList<NormalTransaction> readNt = new ArrayList<NormalTransaction>();
    private NormalTransactionCSVReader(){

    }
    public static NormalTransactionCSVReader getInstance(){
        if(instance == null){
            instance = new NormalTransactionCSVReader();
        }
        return instance;
    }
    @Override
    public void readFileData(String path, double myComission){
        List<List<String>> records = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(path))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                records.add(Arrays.asList(values));
            }
        }catch(IOException e){
            e.printStackTrace();
        }
        for(List<String> record:records){
            double es = Double.parseDouble(record.get(0));
            double xs = Double.parseDouble(record.get(2));
            String ec = record.get(1);
            String xc = record.get(3);
            int d = Integer.parseInt(record.get(5)); //day
            double pC = myComission; // premium commission
            double cR,cP;
            double initialExitSum =  xs / (1-myComission);
            cR = cP = initialExitSum / es;
            String str = record.get(6);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime l = LocalDateTime.parse(str, formatter);
            int id = Integer.parseInt(record.get(7));
            NormalTransaction p = new NormalTransaction(es,ec,xc,d,cR,pC,cP,l,id);
            readNt.add(p);
        }
    }
    @Override
    public void writeFileData(String Path,ArrayList<ArrayList<String>> records) throws IOException {
        FileWriter csvWriter = new FileWriter(Path);
        for(ArrayList<String> record:records) {
            for (String rec : record) {
                csvWriter.append(rec);
                csvWriter.append(",");
            }
            csvWriter.append("\n");
        }
        csvWriter.flush();
        csvWriter.close();
    }
    public ArrayList<NormalTransaction> getReadNt() {
        return readNt;
    }
}
