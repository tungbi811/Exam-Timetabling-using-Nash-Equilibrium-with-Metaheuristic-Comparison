package geneticAlgorithm;

import utils.Data;

public class GeneticAlgorithm {

    private Data data;
    private float maxRunTimeHours;
    private int numberPopulation;
    private int numberElite;
    private int numberThread;

    public GeneticAlgorithm(Data data, float maxRunTimeHours, int numberPopulation, int numberThread) {
        this.data = data;
        this.maxRunTimeHours = maxRunTimeHours;
        this.numberPopulation = numberPopulation;
        this.numberElite = (int) (numberPopulation*0.1);
        this.numberThread = numberThread;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public int getNumberElite() {
        return numberElite;
    }

    public void setNumberElite(int numberElite) {
        this.numberElite = numberElite;
    }

    public float getMaxRunTimeHours() {
        return maxRunTimeHours;
    }

    public void setMaxRunTimeHours(int maxRunTimeHours) {
        this.maxRunTimeHours = maxRunTimeHours;
    }

    public int getNumberPopulation() {
        return numberPopulation;
    }

    public void setNumberPopulation(int numberPopulation) {
        this.numberPopulation = numberPopulation;
    }

    public int getNumberThread() {
        return numberThread;
    }

    public void setNumberThread(int numberThread) {
        this.numberThread = numberThread;
    }
}
