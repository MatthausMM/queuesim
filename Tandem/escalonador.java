package Tandem;

import java.util.PriorityQueue;

public class escalonador {
    public PriorityQueue<Evento> filaEventos;

    public escalonador() {
        filaEventos = new PriorityQueue<>((e1, e2) -> Double.compare(e1.tempo, e2.tempo));
    }

    public void add(Evento e) {
        filaEventos.add(e);
    }

    public Evento prox() {
        return filaEventos.poll();
    }

    public boolean vazio() {
        return filaEventos.isEmpty();
    }
}