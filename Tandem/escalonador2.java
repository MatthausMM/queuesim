package Tandem;

import java.util.PriorityQueue;

public class escalonador2 {
    public PriorityQueue<Evento2> filaEventos;

    public escalonador2() {
        filaEventos = new PriorityQueue<>((e1, e2) -> Double.compare(e1.tempo, e2.tempo));
    }

    public void add(Evento2 e) {
        filaEventos.add(e);
    }

    public Evento2 prox() {
        return filaEventos.poll();
    }

    public boolean vazio() {
        return filaEventos.isEmpty();
    }
}