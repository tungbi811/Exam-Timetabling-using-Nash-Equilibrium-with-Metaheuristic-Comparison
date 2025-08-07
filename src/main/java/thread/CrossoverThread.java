package thread;

import utils.Data;
import utils.Solution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class CrossoverThread implements Runnable{
    public Data data;
    public ArrayList<Solution> population;
    public ArrayList<Solution> newPopulation;
    public CrossoverThread(Data data, ArrayList<Solution> population, ArrayList<Solution> newPopulation) {
        this.data = data;
        this.population = population;
        this.newPopulation = newPopulation;
    }

    @Override
    public void run() {
        Collections.shuffle(population);
        Solution parent1 = population.get(0);
        Solution parent2 = population.get(1);

        Solution children = parent1.clone();

        Random random = new Random();
        int subject = random.nextInt(1, data.Ns-1);
        for (int s = subject; s < data.Ns; s++) {
            int slot = parent2.X[s];
            for (int l = 0; l < data.L[s]; l++) {
                for (int i = 0; i < data.Ni; i++) {
                    children.D[s][slot+l][i] = parent2.D[s][slot+l][i];
                }
                children.H[s][slot+l] = parent2.H[s][slot+l];
            }
            children.X[s] = parent2.X[s];
        }
        children.getFitness();
        newPopulation.add(children);
    }
}
