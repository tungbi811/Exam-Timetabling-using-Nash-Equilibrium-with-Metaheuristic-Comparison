package thread;

import utils.Data;
import utils.Solution;

import java.util.ArrayList;

public class InitializationThread implements Runnable{
    Data data;
    ArrayList<Solution> population;

    public InitializationThread(Data data, ArrayList<Solution> population) {
        this.data = data;
        this.population = population;
    }

    @Override
    public void run() {
        population.add(Solution.createSolution(data));
    }
}
