package utils;

import org.moeaframework.core.NondominatedPopulation;
import org.moeaframework.core.Solution;
import org.moeaframework.core.indicator.Hypervolume;
import org.moeaframework.problem.AbstractProblem;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author tungd
 */
public class HypervolumeCalculator {
    public static void main(String[] args) throws IOException {
        int numberObjectives = 3;
        double[] MAXIMUM = new double[numberObjectives];
        double[] MINIMUM = new double[numberObjectives];

        Arrays.fill(MAXIMUM, 1);
        Arrays.fill(MINIMUM, 0);

        ArrayList<double[]> payoffsTabu = load_payoff( "bestSolution/TS/ParetoSolutionTabu.txt");
        ArrayList<double[]> payoffsGA = load_payoff( "bestSolution/GA/ParetoSolutionGA.txt");
        ArrayList<double[]> payoffsSA = load_payoff( "bestSolution/SA/ParetoSolutionSA.txt");
//        ArrayList<double[]> payoffs = load_payoff("bestSolution/TS/ParetoSolutionTabu.txt");
//        System.out.println(payoffs.size());
//        ArrayList<double[]> pareto = findPareto(payoffs);
//        savePareto("bestSolution/SA/ParetoSolutionSA.txt", pareto);
//        System.out.println("Pareto Size: " + pareto.size());
        double hypervolumeTabu = hypervolume_calculator(payoffsTabu, MINIMUM, MAXIMUM);
        double hypervolumeGA = hypervolume_calculator(payoffsGA, MINIMUM, MAXIMUM);
        double hypervolumeSA = hypervolume_calculator(payoffsSA, MINIMUM, MAXIMUM);
        System.out.println("Tabu Search: " + hypervolumeTabu + " - Genetic Algorithm: " + hypervolumeGA + " - Simulated Annealing: " + hypervolumeSA);
    }

    public static ArrayList<double[]> load_payoff(String file_name) throws FileNotFoundException, IOException {
        ArrayList<double[]> payoffs = new ArrayList<>();

        FileReader fileReader = new FileReader(file_name);
        BufferedReader bufferedReader = new BufferedReader(fileReader);

        String line;
        while ((line = bufferedReader.readLine()) != null) {
            String[] values = line.trim().split("\\s+");
            double[] row = new double[values.length];
            for (int i = 0; i < values.length; i++) {
                row[i] = Double.parseDouble(values[i]);
            }
            payoffs.add(row);
        }
        bufferedReader.close();
        return payoffs;
    }

    // Check if there is other solution dominate candidate
    public static boolean dominates(double[] other, double[] candidate) {
        for (int i = 0; i < other.length; i++) {
            if (candidate[i] < other[i]) {
                return false;
            }
        }
        return true;
    }

    public static boolean contain(ArrayList<double[]> list, double[] target) {
        for (double[] array : list) {
            if (Arrays.equals(array, target)) {
                return true;
            }
        }
        return false;
    }

    public static ArrayList<double[]> findPareto(ArrayList<double[]> payoffs) {
        ArrayList<double[]> pareto = new ArrayList<>();
        ArrayList<double[]> tempPayoff;
        boolean isDominated;

        while (!payoffs.isEmpty()){
            double[] candidate = payoffs.getLast();

            isDominated = false;
            tempPayoff = (ArrayList<double[]>) payoffs.clone();

            for (double[] other : tempPayoff) {
                if (Arrays.equals(other, candidate)){
                    payoffs.remove(other);
                    continue;
                }

                if (dominates(other, candidate)) {
                    isDominated = true;
                    payoffs.remove(candidate);
                    break;
                }

                if (dominates(candidate, other)){
                    payoffs.remove(other);
                }
            }
            if (!isDominated) {
                pareto.add(candidate);
            }
            System.out.println(payoffs.size());
        }
        return pareto;
    }

    public static void savePareto(String fileName, ArrayList<double[]> pareto) throws IOException {
        FileWriter fileWriter = new FileWriter(fileName, true);
        BufferedWriter writer = new BufferedWriter(fileWriter);

        for (double[] point : pareto) {
            writer.write(point[0] + " " + point[1] + " " + point[2] + "\n");
        }
        writer.close();
    }

    public static double hypervolume_calculator(ArrayList<double[]> pareto, double[] MINIMUM, double[] MAXIMUM) {
        AbstractProblem problem = new AbstractProblem(0, pareto.getFirst().length) {
            @Override
            public void evaluate(Solution sltn) {
            }

            @Override
            public Solution newSolution() {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        };

        NondominatedPopulation np = new NondominatedPopulation();

        for (double[] point : pareto) {
            Solution solution = new Solution(point);
            for (int j = 0; j < point.length; j++) {
                solution.setObjective(j, point[j]);
            }
            np.add(solution);
        }

        Hypervolume hp = new Hypervolume(problem, MINIMUM, MAXIMUM);
        double result = 0;
        try {
            result = hp.evaluate(np);
        } catch (Exception e) {
            System.out.println(e);
        }
        return result;
    }
}
