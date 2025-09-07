package Tandem;

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

    public fila(int server, int capacity, double minArrival, double maxArrival, double minService, double maxService) {
        Server = server;
        Capacity = capacity;
        MinArrival = minArrival;
        MaxArrival = maxArrival;
        MinService = minService;
        MaxService = maxService;
        Customers = 0;
        Loss = 0;
        Times = new double[Capacity + 1];
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
}
