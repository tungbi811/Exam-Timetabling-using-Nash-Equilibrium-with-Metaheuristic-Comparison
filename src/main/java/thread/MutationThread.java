package thread;

import utils.Data;
import utils.Solution;

import java.util.ArrayList;
import java.util.Random;

public class MutationThread implements Runnable {
    Data data;
    int numberElite;
    ArrayList<Solution> newPopulation;

    public MutationThread(Data data, int numberElite, ArrayList<Solution> newPopulation) {
        this.data = data;
        this.numberElite = numberElite;
        this.newPopulation = newPopulation;
    }

    @Override
    public void run() {
        Random random = new Random();
        int solutionIndex = random.nextInt(numberElite, newPopulation.size());
        Solution solution = newPopulation.get(solutionIndex);
        ArrayList<ArrayList<Integer>> subjectInvigilator = Solution.getSubjectInvigilator(data, solution.H, solution.D);

        if (random.nextDouble() < 0.7) {
            int subject = random.nextInt(data.Ns);
            int oldSlot = solution.X[subject];
            int newSlot;

            do {
                newSlot = random.nextInt(data.Nt);

                if (newSlot == oldSlot) continue;

                int mod = newSlot % data.beta;

                if (data.L[subject] == 1) {
                    break;
                } else if (data.L[subject] == 2 && (mod == 0 || mod == 1 || mod == 3 || mod == 4)) {
                    break;
                } else if (data.L[subject] == 3 && (mod == 0 || mod == 3)) {
                    break;
                }

            } while (true);


            if (data.G[subject] > 70) {
                for (int s = 0; s < data.Ns; s++) {
                    if (solution.X[s] == newSlot) {
                        for (int l = 0; l < data.L[s]; l++) {
                            for (int i : subjectInvigilator.get(s)) {
                                solution.D[s][newSlot + l][i] = 0;
                                solution.D[s][oldSlot + l][i] = 1;
                            }
                            solution.H[s][newSlot + l] = 0;
                            solution.H[s][oldSlot + l] = 1;
                        }
                        solution.X[s] = oldSlot;
                    }
                }
            }
            for (int l = 0; l < data.L[subject]; l++) {
                for (int i : subjectInvigilator.get(subject)) {
                    solution.D[subject][oldSlot + l][i] = 0;
                    solution.D[subject][newSlot + l][i] = 1;
                }
                solution.H[subject][oldSlot + l] = 0;
                solution.H[subject][newSlot + l] = 1;
            }
            solution.X[subject] = newSlot;
        }

        if (random.nextDouble() < 0.7) {
            int subject = random.nextInt(data.Ns);
            ArrayList<Integer> invigilatorList = subjectInvigilator.get(subject);
            if (!invigilatorList.isEmpty()){
                int index = random.nextInt(invigilatorList.size());

                int oldInvigilator = invigilatorList.get(index);
                int newInvigilator;
                do {
                    newInvigilator = random.nextInt(data.Ni);
                } while (invigilatorList.contains(newInvigilator));

                for (int l = 0; l < data.L[subject]; l++) {
                    solution.D[subject][solution.X[subject] + l][oldInvigilator] = 0;
                    solution.D[subject][solution.X[subject] + l][newInvigilator] = 1;
                }
            }
        }
        solution.getFitness();
        newPopulation.set(solutionIndex, solution);
    }
}
