package geneticAlgorithm;

import thread.CrossoverThread;
import thread.InitializationThread;
import thread.MutationThread;
import utils.Data;
import utils.Solution;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class GeneticAlgorithmImplement {
    GeneticAlgorithm ga;
    Solution bestSolution;
    ArrayList<Solution> population;
    String geneticDir = "bestSolution/GA/";
    String fileName;

    public GeneticAlgorithmImplement(GeneticAlgorithm ga, String fileName) {
        this.ga = ga;
        this.fileName = fileName;
        population = new ArrayList<>();
    }

    public void implement() throws IOException {
        Data data = ga.getData();

        String fitnessFilename = geneticDir + "Fitness"+fileName;

        File myObj = new File(fitnessFilename);

        if (myObj.createNewFile()) {
            System.out.println("File created: " + myObj.getName());
//            bestSolution = Solution.createSolution(data);
        } else {
            System.out.println("File " + myObj.getName() + " already exists.");
//            bestSolution = Solution.readSolution(data, fileName);
        }

        FileWriter fileWriter = new FileWriter(fitnessFilename, false);
        BufferedWriter writer = new BufferedWriter(fileWriter);

        double bestFitness = 1000;
        long startTime = System.currentTimeMillis(); // Start time in ms
        double maxDuration = ga.getMaxRunTimeHours() * 60 * 60 * 1000F; // run time hours in milliseconds
        int i = 0;

        initialization();
        while (System.currentTimeMillis() - startTime <= maxDuration) {
            Collections.sort(population);
            double currentBestFitness = population.getFirst().fitness;
            if (bestFitness > currentBestFitness) {
                bestFitness = currentBestFitness;
                bestSolution = population.getFirst().clone();
                bestSolution.writeSolution(geneticDir+fileName);
            }
            System.out.println("Generation: " + i + " - Best Fitness: " + bestFitness);
            writer.write(bestFitness + "\n");
            generate();
            i+=1;
        }
    }

    public void initialization() {
        ExecutorService executorService = Executors.newFixedThreadPool(ga.getNumberThread());
        for (int i = 0; i < ga.getNumberPopulation(); i++) {
            Runnable thread = new InitializationThread(ga.getData(), population);
            executorService.execute(thread);
        }

        executorService.shutdown();
        while (!executorService.isTerminated()) {

        }
        System.out.println("Finish initiation phase");
    }

    public void generate() throws IOException {
        ArrayList<Solution> newPopulation = new ArrayList<>();
        for (int i = 0; i < ga.getNumberElite(); i++) {
            newPopulation.add(population.get(i).clone());
        }

        ExecutorService executorService = Executors.newFixedThreadPool(ga.getNumberThread());

        for (int i = ga.getNumberElite(); i < ga.getNumberPopulation(); i++) {
            Runnable thread = new CrossoverThread(ga.getData(), population, newPopulation);
            executorService.execute(thread);
        }

        //shutdown thread
        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        executorService = Executors.newFixedThreadPool(ga.getNumberThread());
        for (int i = 0; i < ga.getNumberElite(); i++) {
            Runnable thread = new MutationThread(ga.getData(), ga.getNumberElite(), newPopulation);
            executorService.execute(thread);
        }

        //shutdown thread
        executorService.shutdown();
        try {
            executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        population.clear();
        population.addAll(newPopulation);

        for (Solution s : population) {
            s.writePayoff(geneticDir + "Payoff" + fileName);
        }
    }
}
