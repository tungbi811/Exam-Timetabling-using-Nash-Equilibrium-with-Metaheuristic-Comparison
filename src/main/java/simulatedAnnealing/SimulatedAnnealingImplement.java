package simulatedAnnealing;

import utils.Data;
import utils.Solution;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class SimulatedAnnealingImplement {
    SimulatedAnnealing SA;
    Solution bestSolution;
    String SADir = "bestSolution/SA/";
    String fileName;

    public SimulatedAnnealingImplement(SimulatedAnnealing SA, String fileName) {
        this.SA = SA;
        this.fileName = fileName;
    }

    public void implement() throws IOException {
        Data data = SA.data;
        Solution curSolution = Solution.createSolution(data);
        curSolution.clone();
        Solution neighbor;
        double acceptanceSA;

        String fitnessFilename = SADir + "Fitness"+fileName;

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

        bestSolution = Solution.createSolution(data);

        long startTime = System.currentTimeMillis(); // Start time in ms
        double maxDuration = SA.maxRunTimeHours * 60 * 60 * 1000F; // run time hours in milliseconds
        int i = 0;

        while (System.currentTimeMillis() - startTime <= maxDuration) {
            neighbor = getNeighbor(curSolution);
            neighbor.writePayoff(SADir + "Payoff" + fileName);

            if (neighbor.fitness < bestSolution.fitness){
                bestSolution = neighbor.clone();
                bestSolution.writeSolution(SADir+fileName);
            }

            if (neighbor.fitness <= curSolution.fitness){
                curSolution = neighbor.clone();
            } else {
                acceptanceSA = 1.0 / (1.0 + Math.exp((neighbor.fitness - bestSolution.fitness) / SA.temperature));
                System.out.println("Distance: " + (neighbor.fitness - bestSolution.fitness) + " - Temperature: " + SA.temperature + " - Acceptance SA: " + acceptanceSA);
                if (Math.random() <= acceptanceSA){
                    curSolution = neighbor.clone();
                }
            }
            SA.temperature *= SA.alpha;
            System.out.println("Iteration: " + i + " - Cur Fitness: " + neighbor.fitness + " - Best Fitness: " + bestSolution.fitness);
            writer.write(bestSolution.fitness + "\n");
            i+=1;
        }
    }

    public Solution getNeighbor(Solution curSolution) {
        Data data = SA.data;
        Solution neighbor;
        ArrayList<ArrayList<Integer>> subjectInvigilator = Solution.getSubjectInvigilator(data, curSolution.H, curSolution.D);
        double random = Math.random();
        while (true){
             neighbor = curSolution.clone();
            try {
                if (random < 0.2) {
                    getSubjectSlotNeighbor(neighbor, subjectInvigilator);
                } else if (random < 0.8) {
                    getSubjectSlotNeighbor(neighbor, subjectInvigilator);
                    getSubjectInvigilatorNeighbor(neighbor, subjectInvigilator);
                } else {
                    getSubjectInvigilatorNeighbor(neighbor, subjectInvigilator);
                }
                neighbor.getFitness();
                if (neighbor.fitness < 100){
                    return neighbor;
                }
            } catch (Exception ignored){
            }
        }
    }

    private void getSubjectInvigilatorNeighbor(Solution neighbor, ArrayList<ArrayList<Integer>> subjectInvigilator) {
        Data data = SA.data;
        Random random = new Random();

        int subject1, subject2, index1, index2, oldInvigilator1, oldInvigilator2, newInvigilator1, newInvigilator2;

        do {
            subject1 = random.nextInt(data.Ns);
            subject2 = random.nextInt(data.Ns);
        } while (subject1 == subject2);

        ArrayList<Integer> invigilatorList1 = subjectInvigilator.get(subject1);
        ArrayList<Integer> invigilatorList2 = subjectInvigilator.get(subject2);

        index1 = random.nextInt(invigilatorList1.size());
        index2 = random.nextInt(invigilatorList2.size());

        oldInvigilator1 = invigilatorList1.get(index1);
        do {
            newInvigilator1 = random.nextInt(data.Ni);
        } while (invigilatorList1.contains(newInvigilator1));

        oldInvigilator2 = invigilatorList2.get(index2);
        do {
            newInvigilator2 = random.nextInt(data.Ni);
        } while (invigilatorList2.contains(newInvigilator2));

        for (int l = 0; l < data.L[subject1]; l++) {
            neighbor.D[subject1][neighbor.X[subject1] + l][oldInvigilator1] = 0;
            neighbor.D[subject1][neighbor.X[subject1] + l][newInvigilator1] = 1;
        }
        for (int l = 0; l < data.L[subject2]; l++) {
            neighbor.D[subject2][neighbor.X[subject2] + l][oldInvigilator2] = 0;
            neighbor.D[subject2][neighbor.X[subject2] + l][newInvigilator2] = 1;
        }
    }

    private void getSubjectSlotNeighbor(Solution neighbor, ArrayList<ArrayList<Integer>> subjectInvigilator) {
        Data data = SA.data;
        Random random = new Random();

        int subject1, subject2;

        do {
            subject1 = random.nextInt(data.Ns);
            subject2 = random.nextInt(data.Ns);
        } while (subject1 == subject2);

        ArrayList<Integer> invigilatorList1 = subjectInvigilator.get(subject1);
        ArrayList<Integer> invigilatorList2 = subjectInvigilator.get(subject2);

        int oldSlotSubject1 = neighbor.X[subject1];
        int oldSlotSubject2 = neighbor.X[subject2];

        int newSlotSubject1;

        do {
            newSlotSubject1 = random.nextInt(data.Nt);

            if (newSlotSubject1 == oldSlotSubject1) continue;

            int mod = newSlotSubject1 % data.beta;

            if (data.L[newSlotSubject1] == 1) {
                break;
            } else if (data.L[newSlotSubject1] == 2 && (mod == 0 || mod == 1 || mod == 3 || mod == 4)) {
                break;
            } else if (data.L[newSlotSubject1] == 3 && (mod == 0 || mod == 3)) {
                break;
            }

        } while (true);

        if (oldSlotSubject1 == oldSlotSubject2 && newSlotSubject1 == oldSlotSubject1) {
            return;
        }

        if (data.G[subject1] > 70) {
            for (int s = 0; s < data.Ns; s++) {
                if (neighbor.X[s] == newSlotSubject1) {
                    for (int l = 0; l < data.L[s]; l++) {
                        for (int i : subjectInvigilator.get(s)) {
                            neighbor.D[s][newSlotSubject1 + l][i] = 0;
                            neighbor.D[s][oldSlotSubject1 + l][i] = 1;
                        }
                        neighbor.H[s][newSlotSubject1 + l] = 0;
                        neighbor.H[s][oldSlotSubject1 + l] = 1;
                    }
                    neighbor.X[s] = oldSlotSubject1;
                }
            }
        }

        for (int l = 0; l < data.L[subject1]; l++) {
            for (int i : invigilatorList1) {
                neighbor.D[subject1][oldSlotSubject1 + l][i] = 0;
                neighbor.D[subject1][newSlotSubject1 + l][i] = 1;
            }
            neighbor.H[subject1][oldSlotSubject1 + l] = 0;
            neighbor.H[subject1][newSlotSubject1 + l] = 1;
        }
        neighbor.X[subject1] = newSlotSubject1;

        if (data.G[subject2] > 70) {
            for (int s = 0; s < data.Ns; s++) {
                if (neighbor.X[s] == oldSlotSubject1) {
                    for (int l = 0; l < data.L[s]; l++) {
                        for (int i : subjectInvigilator.get(s)) {
                            neighbor.D[s][oldSlotSubject1 + l][i] = 0;
                            neighbor.D[s][oldSlotSubject2 + l][i] = 1;
                        }
                        neighbor.H[s][oldSlotSubject1 + l] = 0;
                        neighbor.H[s][oldSlotSubject2 + l] = 1;
                    }
                    neighbor.X[s] = oldSlotSubject2;
                }
            }
        }

        for (int l = 0; l < data.L[subject2]; l++) {
            for (int i : invigilatorList2) {
                neighbor.D[subject2][oldSlotSubject2 + l][i] = 0;
                neighbor.D[subject2][oldSlotSubject1 + l][i] = 1;
            }
            neighbor.H[subject2][oldSlotSubject2 + l] = 0;
            neighbor.H[subject2][oldSlotSubject1 + l] = 1;
        }
        neighbor.X[subject2] = oldSlotSubject1;
    }
}
