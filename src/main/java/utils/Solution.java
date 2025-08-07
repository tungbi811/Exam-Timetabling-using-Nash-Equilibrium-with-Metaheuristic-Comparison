package utils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class Solution implements Comparable<Solution> {
    Data data;
    public int[][][] D;
    public int[][] H;
    public int[] X;
    public double payoffP;
    public double payoffAllM;
    public double payoffAllI;
    public double fitness;

    public Solution(Data data, int[][][] d, int[][] h, int[] x, double newFitness) {
        this.data = data;
        D = d;
        H = h;
        X = x;
        fitness = newFitness;
    }

    public Solution(Data data, int[][][] d, int[][] h, int[] x) {
        this.data = data;
        D = d;
        H = h;
        X = x;
        getFitness();
    }

    public Solution(Data data, int[][][] d) {
        this.data = data;
        D = d;

        H = new int[data.Ns][data.Nt];
        for (int s = 0; s < data.Ns; s++) {
            for (int t = 0; t < data.Nt; t++) {
                int sumInvigilator = 0;
                for (int i = 0; i < data.Ni; i++) {
                    sumInvigilator += D[s][t][i];
                }
                if (sumInvigilator > 0) {
                    H[s][t] = 1;
                } else {
                    H[s][t] = 0;
                }
            }
        }

        X = new int[data.Ns];
        for (int s = 0; s < data.Ns; s++) {
            for (int t = 0; t < data.Nt; t++) {
                if (H[s][t] == 1) {
                    X[s] = t;
                    break;
                }
            }
        }
        getFitness();
    }

    public static Solution createSolution(Data data) {
        int[][][] D = new int[0][][];
        int[][] H = new int[0][];
        int[] X = new int[0];

        while (true) {
            D = new int[data.Ns][data.Nt][data.Ni];
            int[] numberOfSlotScheduleInvigilator = new int[data.Ni];
            int[][] invigilatorTakeSlot = new int[data.Ni][data.Nt];
            H = new int[data.Ns][data.Nt];
            X = new int[data.Ns];
            Random rand = new Random();

            for (int i = 0; i < data.Ni; i++) {
                numberOfSlotScheduleInvigilator[i] = data.Q[i];
            }

            for (int s = 0; s < data.Ns; s++) {
                int sLength = data.L[s];
                int[] haveChosen = new int[data.Nt - sLength + 1];
                int t = rand.nextInt(data.Nt - sLength + 1);
                boolean flag = false;
                while (true) {
                    int sum = 0;
                    haveChosen[t] = 1;
                    for (int index = 0; index < haveChosen.length; index++) {
                        sum += haveChosen[index];
                    }
                    if (sum == data.Nt - sLength + 1) {
                        flag = true;
                        break;
                    }

                    if ((Solution.passConstraint8(data, s, t)
                            && Solution.passConstraint5(data, H, s, t)
                            && Solution.passConstraint1(data, H, s, t))) {
                        break;
                    }
                    t = rand.nextInt(data.Nt - sLength + 1);
                }
                if (flag) {
                    break;
                }

                DistributedRandom randomInvigilator = new DistributedRandom();
                int countInvigilator = 0;
                int invigilatorNeed = data.G[s];

                for (int i = 0; i < data.Ni; i++) {
                    boolean canAdd = true;
                    for (int eachSlot = t; eachSlot < t + sLength; eachSlot++) {
                        if (data.C[s][i] == 1 && invigilatorTakeSlot[i][eachSlot] == 0 && numberOfSlotScheduleInvigilator[i] > 0) {
                            canAdd = true;
                        } else {
                            canAdd = false;
                            break;
                        }
                    }

                    if (canAdd) {
                        randomInvigilator.add(i, numberOfSlotScheduleInvigilator[i]);
                        countInvigilator++;
                    }
                }

                if (countInvigilator < invigilatorNeed) {
                    System.out.println("Not enough invigilators: " + s);
                }

                for (int i = 0; i < invigilatorNeed; i++) {
                    int currentInvigilator = randomInvigilator.getRandom();

                    if (data.C[s][currentInvigilator] != 1) {
                        System.out.println("Invalid invigilator assigned for subject");
                    }

                    for (int eachSlot = t; eachSlot < t + sLength; eachSlot++) {
                        if (invigilatorTakeSlot[currentInvigilator][eachSlot] != 0) {
                            System.out.println("Invalid slot assignment for invigilator: " + currentInvigilator);
                        }

                        D[s][eachSlot][currentInvigilator] = 1;
                        invigilatorTakeSlot[currentInvigilator][eachSlot] = 1;
                        H[s][eachSlot] = 1;
                    }

                    randomInvigilator.delete(currentInvigilator);
                    numberOfSlotScheduleInvigilator[currentInvigilator]--;
                }
                X[s] = t;
            }
            Solution solution = new Solution(data, D, H, X);
            if (solution.passAllConstraint()) {
                return solution;
            }
        }
    }

    //    1. No student should be required to sit two examinations simultaneously
    public boolean passConstraint1() {
        for (int m = 0; m < data.Nm; m++) {
            for (int t = 0; t < data.Nt; t++) {
                boolean joined = false;
                for (int s = 0; s < data.Ns; s++) {
                    if (data.A[m][s] == 1 && H[s][t] == 1) {
                        if (joined) {
                            return false;
                        } else {
                            joined = true;
                        }
                    }
                }
            }
        }
        return true;
    }

    public static boolean passConstraint1(Data data, int[][] H, int subject, int slot) {
        for (int m = 0; m < data.Nm; m++) {
            for (int l = 0; l < data.L[subject]; l++) {
                for (int s = 0; s < data.Ns; s++) {
                    if (s == subject) {
                        continue;
                    }
                    if (data.A[m][s] == 1 && data.A[m][subject] == 1 && H[s][slot + l] == 1) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    //  2. The number of supervisors must be equal to the number of rooms needed to organize each subject
    public boolean passConstraint2() {
        for (int s = 0; s < data.Ns; s++) {
            for (int t = 0; t < data.Nt; t++) {
                int sum = 0;
                for (int i = 0; i < data.Ni; i++) {
                    sum += D[s][t][i];
                }
                if (0 < sum && sum < data.G[s]) {
                    return false;
                }
            }
        }
        return true;
    }

    //    3. No invigilator should be required to sit two examinations simultaneously
    public boolean passConstraint3() {
        for (int i = 0; i < data.Ni; i++) {
            for (int t = 0; t < data.Nt; t++) {
                int sum = 0;
                for (int s = 0; s < data.Ns; s++) {
                    sum += D[s][t][i];
                }
                if (sum > 1) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean passConstraint3(Data data, int[][][] D, int subject, int slot, int invigilator) {
        for (int l = 0; l < data.L[subject]; l++) {
            for (int s = 0; s < data.Ns; s++) {
                if (D[s][slot + l][invigilator] == 1) {
                    return false;
                }
            }
        }
        return true;
    }

    //  4. Examiners only supervise subjects they are capable of supervising
    public boolean passConstraint4() {
        for (int s = 0; s < data.Ns; s++) {
            for (int i = 0; i < data.Ni; i++) {
                for (int t = 0; t < data.Nt; t++) {
                    if (D[s][t][i] == 1 && data.C[s][i] == 0) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public static boolean passConstraint4(Data data, int subject, int invigilator) {
        return data.C[subject][invigilator] == 1;
    }

    //    5. The number of rooms used in a slot must not exceed the allowed
    public boolean passConstraint5() {
        for (int t = 0; t < data.Nt; t++) {
            int sum = 0;
            for (int s = 0; s < data.Ns; s++) {
                for (int i = 0; i < data.Ni; i++) {
                    sum += D[s][t][i];
                }
            }
            if (sum > data.Nr) {
                return false;
            }
        }
        return true;
    }

    public static boolean passConstraint5(Data data, int[][] H, int subject, int slot) {
        for (int l = 0; l < data.L[subject]; l++) {
            int sum = 0;
            for (int s = 0; s < data.Ns; s++) {
                if (s != subject && H[s][slot + l] == 1) {
                    sum += data.G[s];
                }
            }
            sum += data.G[subject];
            if (sum > data.Nr) {
                return false;
            }
        }
        return true;
    }

    //    6. Each subject can only be held once
    public boolean passConstraint6() {
        for (int s = 0; s < data.Ns; s++) {
            int sum = 0;
            for (int t = 0; t < data.Nt; t++) {
                sum += H[s][t];
            }
            if (sum != data.L[s]) {
                return false;
            }
        }
        return true;
    }

    //    7. For each exam, the assigned invigilator needs to monitor all consecutive slots in which that exam takes place
    public boolean passConstraint7() {
        for (int s = 0; s < data.Ns; s++) {
            for (int i = 0; i < data.Ni; i++) {
                int sum = 0;
                for (int t = X[s]; t < X[s] + data.L[s]; t++) {
                    sum += D[s][t][i];
                }
                if (0 < sum && sum < data.L[s]) {
                    return false;
                }
            }

        }
        return true;
    }

    //    8. For exams with L slot time, it cannot end later than morning or afternoon
    public boolean passConstraint8() {
        for (int s = 0; s < data.Ns; s++) {
            if ((X[s] / (data.beta / 2)) != ((X[s] + data.L[s] - 1) / (data.beta / 2))) {
                return false;
            }
        }
        return true;
    }

    public static boolean passConstraint8(Data data, int subject, int slot) {
        return slot / (data.beta / 2) == (slot + data.L[subject] - 1) / (data.beta / 2);
    }

    public boolean passAllConstraint() {
        return passConstraint1()
                && passConstraint2()
                && passConstraint3()
                && passConstraint4()
                && passConstraint5()
                && passConstraint6()
                && passConstraint7()
                && passConstraint8();
    }

    public void getFitness() {
        if (!passAllConstraint()) {
            fitness *= 100;
        } else {
            double w1 = 1.0 / 3.0;
            double w2 = 1.0 / 3.0;
            double w3 = 1.0 / 3.0;
            getPayoffP();
            getPayoffAllM();
            getPayoffAllI();
            fitness = payoffP + payoffAllM + payoffAllI;
        }
    }

    public void getPayoffP() {
        double mean = 0;
        for (int t = 0; t < data.Nt; t++) {
            for (int s = 0; s < data.Ns; s++) {
                if (H[s][t] == 1) {
                    mean += data.G[s];
                }
            }
        }

        mean /= data.Nt;
        double std = 0;
        for (int t = 0; t < data.Nt; t++) {
            int sum = 0;
            for (int s = 0; s < data.Ns; s++) {
                if (H[s][t] == 1) {
                    sum += data.G[s];
                }
            }
            std += (sum - mean) * (sum - mean);
        }
        std /= (data.Nt - 1);
        payoffP = Math.sqrt(std) / 50;
    }

    public void getPayoffAllM() {
        int[] U = new int[data.Ns];
        double payoffM;
        for (int m = 0; m < data.Nm; m++) {
            if (data.E[m] == 1) {
                continue;
            }
//            payoffAllM += Math.log(getPayoffM(m, U));
            payoffM = getPayoffM(m, U);
            payoffAllM += payoffM;
        }
        payoffAllM /= data.numberStudentMoreThanOneSubject;
    }

    public double getPayoffM(int m, int[] U) {
        for (int s = 0; s < data.Ns; s++) {
            U[s] = data.A[m][s] * X[s];
        }

        Arrays.sort(U);

        double payoffM = 0;
        int meanDistance = data.Nt / data.E[m];
        for (int i = data.Ns - 1; i > (data.Ns - data.E[m]); i--) {
//            payoffM += Math.exp(Math.abs(U[i] - U[i - 1] - meanDistance));
            payoffM += (double) Math.abs(U[i] - U[i - 1] - meanDistance) / Math.abs(data.Nt - 1 - meanDistance);
        }
        return payoffM;
    }

    public void getPayoffAllI() {
        double payoffI;
        for (int i = 0; i < data.Ni; i++) {
            payoffI = getPayoffI(i);
            payoffAllI += payoffI;
        }
        payoffAllI /= data.Ni;
    }

    public double getPayoffI(int i) {
        double w4 = 1.0 / 2.0;
        double w5 = 1.0 / 2.0;
        double numberOfDay = 0;
        int sum;
        for (int d = 0; d < data.Nd; d++) {
            sum = 0;
            for (int t = data.beta * d; t < data.beta * (d + 1); t++) {
                for (int s = 0; s < data.Ns; s++) {
                    sum += D[s][t][i];
                }
            }
            if (sum > 0) {
                numberOfDay += 1;
            }
        }

        double numberOfSlot = 0;
        for (int s = 0; s < data.Ns; s++) {
            for (int t = 0; t < data.Nt; t++) {
                numberOfSlot += D[s][t][i];
            }
        }
//        System.out.println("Number of day: " + numberOfDay + "Number of slot: " + Math.abs(numberOfSlot - data.Q[i]));
        return w4 * numberOfDay / data.Nd + w5 * Math.abs(numberOfSlot - data.Q[i]) / data.Q[i];
    }

    public static ArrayList<ArrayList<Integer>> getSubjectInvigilator(Data data, int[][] H, int[][][] D) {
        ArrayList<ArrayList<Integer>> subjectInvigilator = new ArrayList<>();
        for (int i = 0; i < data.Ns; i++) {
            subjectInvigilator.add(new ArrayList<Integer>());
        }

        for (int s = 0; s < data.Ns; s++) {
            for (int t = 0; t < data.Nt; t++) {
                if (H[s][t] == 1) {
                    for (int i = 0; i < data.Ni; i++) {
                        if (D[s][t][i] == 1) {
                            subjectInvigilator.get(s).add(i);
                        }
                    }
                    break;
                }
            }
        }
        return subjectInvigilator;
    }

    public static Solution readSolution(Data data, String fileName) {
        int[][][] D = new int[data.Ns][data.Nt][data.Ni];
        try {
            File file = new File(fileName);
            Scanner myReader = new Scanner(file);
            while (myReader.hasNextLine()) {
                String line = myReader.nextLine();
                String[] words = line.split("\\D+");
                D[Integer.parseInt(words[1])][Integer.parseInt(words[2])][Integer.parseInt(words[3])] = Integer.parseInt(words[4]);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return new Solution(data, D);
    }

    public void writeSolution(String fileName) {
        try (PrintWriter writer = new PrintWriter(fileName)) {
            for (int x = 0; x < D.length; x++) {
                for (int y = 0; y < D[x].length; y++) {
                    for (int z = 0; z < D[x][y].length; z++) {
                        // Write the values of x, y, z and D[x][y][z] separated by commas
                        writer.println("Subject " + x + "," + "Slot " + y + "," + "Invigilator " + z + ":" + D[x][y][z]);
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file: " + e.getMessage());
        }
    }

    public void writePayoff(String sheetName) throws IOException {
        if (fitness == 1000) {
            return;
        }
        FileWriter fileWriter = new FileWriter(sheetName, true);
        BufferedWriter writer = new BufferedWriter(fileWriter);

        writer.write(1.0 / 20.0 * payoffP + " " + 6.0 / 20.0 * payoffAllM + " " + 13.0 / 20.0 * payoffAllI + "\n");
        writer.close();
    }

    public Solution clone() {
        int[] newX = Arrays.copyOf(X, X.length);
        int[][] newH = Arrays.stream(H).map(int[]::clone).toArray(int[][]::new);
        int[][][] newD = new int[data.Ns][data.Nt][data.Ni];
        for (int s = 0; s < data.Ns; s++) {
            int slotStart = newX[s];
            for (int l = 0; l < data.L[s]; l++) {
                for (int i = 0; i < data.Ni; i++) {
                    newD[s][slotStart + l][i] = D[s][slotStart + l][i];
                }
            }
        }
        double newFitness = fitness;
        return new Solution(data, newD, newH, newX, newFitness);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Solution solution = (Solution) o;
        return Arrays.deepEquals(D, solution.D);
    }

    @Override
    public int compareTo(Solution s) {
        if (this.fitness == s.fitness) {
            return 0;
        } else {
            if (this.fitness > s.fitness) {
                return 1;
            } else {
                return -1;
            }
        }
    }
}
