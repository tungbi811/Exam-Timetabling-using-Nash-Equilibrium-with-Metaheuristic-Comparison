package tabuSearch;

import com.opencsv.exceptions.CsvValidationException;
import utils.Data;

import java.io.IOException;

public class MainTabu {
    public static void main(String[] args) throws CsvValidationException, IOException, CloneNotSupportedException {
        Data data = new Data();
        float maxRunTimeHours = 12F;
        int maxTabuSize = 500;
        int neighborsSize = 300;
        int numberThread = 10;

        TabuSearch tabuSearch = new TabuSearch(data, maxRunTimeHours, maxTabuSize, neighborsSize, numberThread);
        TabuSearchImplement tabu = new TabuSearchImplement(tabuSearch, "SolutionTabu_v2.txt");
        tabu.implement();
    }

}

