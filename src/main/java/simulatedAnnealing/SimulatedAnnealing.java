package simulatedAnnealing;

import utils.Data;

public class SimulatedAnnealing {
    public Data data;
    public double maxRunTimeHours;
    public double temperature;
    public double alpha;

    public SimulatedAnnealing(Data data, double maxRunTimeHours, double temperature, double alpha) {
        this.data = data;
        this.maxRunTimeHours = maxRunTimeHours;
        this.temperature = temperature;
        this.alpha = alpha;
    }
}
