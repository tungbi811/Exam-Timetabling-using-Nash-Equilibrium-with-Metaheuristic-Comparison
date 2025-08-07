package simulatedAnnealing;

import com.opencsv.exceptions.CsvValidationException;
import utils.Data;

import java.io.IOException;

public class MainSA {
    public static void main(String[] args) throws CsvValidationException, IOException {
        Data data = new Data();
        double maxRunTimeHours = (float) 1/30;
        double temperature = 1000;
        double alpha = 0.99;
        SimulatedAnnealing SA = new SimulatedAnnealing(data, maxRunTimeHours, temperature, alpha);
        SimulatedAnnealingImplement SAImplement = new SimulatedAnnealingImplement(SA, "SolutionSA_v2.txt");
        SAImplement.implement();
    }
}
