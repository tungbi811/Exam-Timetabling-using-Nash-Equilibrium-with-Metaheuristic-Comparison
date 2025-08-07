package thread;

import utils.Data;
import utils.Solution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class GenerateThread implements Runnable {
    Data data;
    private double crossoverRate;
    private double mutationRate;
    private Solution parent1;
    private Solution parent2;
    private Solution children;
    ArrayList<Solution> newPopulation;

    public GenerateThread(Data data, double crossoverRate, double mutationRate, ArrayList<Solution> population, ArrayList<Solution> newPopulation) {
        this.data = data;
        this.crossoverRate = crossoverRate;
        this.mutationRate = mutationRate;
        this.newPopulation = newPopulation;

        Collections.shuffle(population);
        parent1 = population.get(0);
        parent2 = population.get(1);

        children = parent1.clone();
    }

    public void crossover() {
        Random random = new Random();
        int subject = random.nextInt(data.Ns);
        int slot = parent2.X[subject];
        for (int l = 0; l < data.L[subject]; l++) {
            for (int i = 0; i < data.Ni; i++) {
                children.D[subject][slot+l][i] = parent2.D[subject][slot+l][i];
            }
            children.H[subject][slot+l] = parent2.H[subject][slot+l];
        }
        children.X[subject] = parent2.X[subject];
    }

    public void mutation() {
        Random random = new Random();
        int subject = random.nextInt(data.Ns);
        ArrayList<ArrayList<Integer>> subjectInvigilator = Solution.getSubjectInvigilator(data, children.H, children.D);
        int oldSlotSubject = children.X[subject];
        int length = data.L[subject];

        int newSlotSubject;
        do {
            newSlotSubject = random.nextInt(data.Nt);
        } while (!((newSlotSubject + length - 1) < data.Nt));

        for (int l = 0; l < length; l++) {
            for (int i : subjectInvigilator.get(subject)) {
                children.D[subject][oldSlotSubject + l][i] = 0;
                children.D[subject][newSlotSubject + l][i] = 1;

                children.H[subject][oldSlotSubject + l] = 0;
                children.H[subject][newSlotSubject + l] = 1;
            }
        }

        children.X[subject] = newSlotSubject;

        subject = random.nextInt(data.Ns);
        ArrayList<Integer> invigilatorList = subjectInvigilator.get(subject);
        int index = random.nextInt(invigilatorList.size());
        int slot = children.X[subject];
        int oldInvigilator = invigilatorList.get(index);
        int newInvigilator = random.nextInt(data.Ni);
        for (int l = 0; l < data.L[subject]; l++) {
            children.D[subject][slot + l][oldInvigilator] = 0;
            children.D[subject][slot + l][newInvigilator] = 1;
        }
    }

    @Override
    public void run() {
        long start, end;
        if (Math.random() < crossoverRate) {
//            start = System.nanoTime();
            crossover();
//            end = System.nanoTime();
//            System.out.println("Crossover Time: "+(end-start)/1000000+"ms");
        }
        if (Math.random() < mutationRate) {
//            start = System.nanoTime();
            mutation();
//            end = System.nanoTime();
//            System.out.println("Mutation Time: "+(end-start)/1000000+"ms");
        }
//        start = System.nanoTime();
//        end = System.nanoTime();
//        System.out.println("Fitness Time: "+(end-start)/1000000+"ms");
        newPopulation.add(children);
    }
}
