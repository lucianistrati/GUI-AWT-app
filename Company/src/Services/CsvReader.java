package Services;

import java.io.IOException;
import java.util.ArrayList;

public interface CsvReader {
    public void readFileData(String path, double myComission) throws IOException;
    public void writeFileData(String Path,ArrayList<ArrayList<String>> records) throws IOException;
}
