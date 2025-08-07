package geneticAlgorithm;

import com.opencsv.exceptions.CsvValidationException;
import utils.Data;

import java.io.IOException;

public class MainGeneticAlgorithm {
    public static void main(String[] args) throws CsvValidationException, IOException {
        Data data = new Data();

        float maxRunTimeHours = (float) 1 /60;
        int numberPopulation = 200;
        int numberThread = 10;
        GeneticAlgorithm ga = new GeneticAlgorithm(data, maxRunTimeHours, numberPopulation, numberThread);

        GeneticAlgorithmImplement gaImplement = new GeneticAlgorithmImplement(ga, "SolutionGA_v2.txt");
        gaImplement.implement();

    }
}
