package Tandem.allProb;

public class fila {
    private int Server;
    private int Capacity;
    private double MinArrival;
    private double MaxArrival;
    private double MinService;
    private double MaxService;
    private int Customers;
    private int Loss;
    private double[] Times;
    private double[] probs;
    private int[] destinos;

    public fila(int server, int capacity, double minArrival, double maxArrival, double minService, double maxService, double[] probs, int[] destinos) {
        this.Server = server;
        this.Capacity = capacity;
        this.MinArrival = minArrival;
        this.MaxArrival = maxArrival;
        this.MinService = minService;
        this.MaxService = maxService;
        this.Customers = 0;
        this.Loss = 0;
        this.Times = new double[Capacity + 1];
        this.probs = probs;
        this.destinos = destinos;
    }

    public int Status() {
        return Customers;
    }
    public int Capacity() {
        return Capacity;
    }
    public int Server() {
        return Server;
    }
    public void Loss() {
        Loss++;
    }
    public int getLoss() {
        return Loss;
    }
    public void In() {
        Customers++;
    }
    public void Out() {
        Customers--;
    }
    public double getMinArrival() {
        return MinArrival;
    }
    public double getMaxArrival() {
        return MaxArrival;
    }
    public double getMinService() {
        return MinService;
    }
    public double getMaxService() {
        return MaxService;
    }
    public double[] getTimes() {
        return Times;
    }
    public double[] getProbs() {
        return probs;
    }
    public int[] getDestinos() {
        return destinos;
    }
}
