package Services;

import Entities.NormalClient;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class NormalClientCSVReader implements CsvReader {
    private static NormalClientCSVReader instance =  null;
    private ArrayList<NormalClient> readNc = new ArrayList<NormalClient>();
    private NormalClientCSVReader(){
    }
    public static NormalClientCSVReader getInstance(){
        if(instance == null){
            instance = new NormalClientCSVReader();
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
            String name = record.get(1);
            int d = Integer.parseInt(record.get(2));
            String str = record.get(3);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime l = LocalDateTime.parse(str, formatter);
            int id = Integer.parseInt(record.get(0));
            NormalClient p = new NormalClient(name, d, l);
            p.setClientID(id);
            readNc.add(p);
        }
    }
    @Override
    public void writeFileData(String Path,ArrayList<ArrayList<String>> records) throws IOException{
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
    public ArrayList<NormalClient> getReadNc() {
        return readNc;
    }
}
