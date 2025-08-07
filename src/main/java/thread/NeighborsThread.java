package thread;

import utils.Data;
import utils.Solution;

import java.util.ArrayList;
import java.util.Random;

public class NeighborsThread implements Runnable {
    Data data;
    Solution neighbor;
    ArrayList<Solution> neighbors;
    ArrayList<Solution> tabuList;
    ArrayList<ArrayList<Integer>> subjectInvigilator;

    public  NeighborsThread(Data data, Solution solution, ArrayList<Solution>neighbors, ArrayList<Solution> tabuList, ArrayList<ArrayList<Integer>> subjectInvigilator) {
        this.data = data;
        this.neighbor = solution.clone();
        this.neighbors = neighbors;
        this.tabuList = tabuList;
        this.subjectInvigilator = subjectInvigilator;
    }

    @Override
    public void run() {
        try {
            double random = Math.random();
            if (random < 0.2){
                getSubjectSlotNeighbor();
            } else if (random < 0.8) {
                getSubjectSlotNeighbor();
                getSubjectInvigilatorNeighbor();
            }else {
                getSubjectInvigilatorNeighbor();
            }

            if (!tabuList.contains(neighbor)) {
                neighbor.getFitness();
                neighbors.add(neighbor);
            }
        }catch (Exception ignored){
        }
    }

    private void getSubjectInvigilatorNeighbor() {
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
        }while (invigilatorList1.contains(newInvigilator1));

        oldInvigilator2 = invigilatorList2.get(index2);
        do {
            newInvigilator2 = random.nextInt(data.Ni);
        }while (invigilatorList2.contains(newInvigilator2));

        for (int l = 0; l < data.L[subject1]; l++) {
            neighbor.D[subject1][neighbor.X[subject1] + l][oldInvigilator1] = 0;
            neighbor.D[subject1][neighbor.X[subject1] + l][newInvigilator1] = 1;
        }
        for (int l = 0; l < data.L[subject2]; l++) {
            neighbor.D[subject2][neighbor.X[subject2] + l][oldInvigilator2] = 0;
            neighbor.D[subject2][neighbor.X[subject2] + l][newInvigilator2] = 1;
        }
    }

    private void getSubjectSlotNeighbor() {
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
