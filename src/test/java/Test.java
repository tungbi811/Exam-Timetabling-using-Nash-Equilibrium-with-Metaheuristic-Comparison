import com.opencsv.exceptions.CsvValidationException;
import utils.Data;
import utils.Solution;

import java.io.IOException;

public class Test{
    public static void main(String[] args) throws IOException, CsvValidationException {
        Data data = new Data();
        Solution solution = Solution.readSolution(data, "bestSolution/SolutionTabu.txt");
        System.out.println(solution.fitness);
        System.out.println(solution.payoffP);
        System.out.println(solution.payoffAllM);
        System.out.println(solution.payoffAllI);
    }
}
