package Tandem;
import java.util.*;

public class testeUmaFila {

    static int count = 10; // números aleatórios a serem utilizados

    static long previous = 1337;
    static long a = 3344556677L;
    static long c = 17;
    static long M = (long) Math.pow(2, 32);

    static fila fila1;

    static double TempoGlobal = 0.0;
    static double tempoAnterior = 0.0;

    static double chegada;
    static double saida;

    static int FILA = 0; // número de clientes na fila

    static int perdas = 0;

    static Evento evento;

    static escalonador esc = new escalonador();

    // ------------------------------------------------

    static double NextRandom() {
        previous = ((a * previous) + c) % M;
        System.out.println((double)previous/M);
        count--;
        return (double) previous / M;
    }

    // ------------------------------------------------

    static double tempoChegada() {
        return 2 + (5 - 2) * NextRandom();
    }

    static double tempoAtendimento() {
        return 3 + (5 - 3) * NextRandom();
    }

    // ------------------------------------------------

    static void CHEGADA(Evento evento) {

        TempoGlobal = evento.tempo;
        atualizaEstatisticas();

        if (FILA < fila1.Capacity()) {
            FILA++;
            if (FILA <= fila1.Server()) {
                saida = TempoGlobal + tempoAtendimento();
                esc.add(new Evento(Evento.EventType.TIPO_SAIDA, saida));
            }
        } else {
            perdas++;
        }
        chegada = TempoGlobal + tempoChegada();
        esc.add(new Evento(Evento.EventType.TIPO_CHEGADA, chegada));
    }

    static void SAIDA(Evento evento) {

        TempoGlobal = evento.tempo;
        atualizaEstatisticas();

        FILA--;

        if (FILA >= fila1.Server()) {
            saida = TempoGlobal + tempoAtendimento();
            esc.add(new Evento(Evento.EventType.TIPO_SAIDA, saida));
        }
    }

    // ------------------------------------------------

    static void atualizaEstatisticas() {
        double delta = TempoGlobal - tempoAnterior;
        if (delta > 0 && FILA >= 0 && FILA <= fila1.Capacity()) {
            fila1.getTimes()[FILA] += delta;
        }
        tempoAnterior = TempoGlobal;
    }

    // ------------------------------------------------

    public static void main(String[] args) {

        Scanner in = new Scanner(System.in);

        /* if (in.next().equals("y")) {
            for (int i = 0; i < 10; i++) {
                System.out.println("- " + NextRandom());
            }
            previous = 1337; // reset
            System.out.println("---");
        } */

        System.out.println("Escolha o sistema de filas:");
        System.out.println("1 -> G/G/1/5");
        System.out.println("2 -> G/G/2/5");
        int escolha = in.nextInt();
        if (escolha == 1) {
            fila1 = new fila(1, 5, 2.0, 5.0, 3.0, 5.0);
        } else {
            fila1 = new fila(2, 5, 2.0, 5.0, 3.0, 5.0);
        }
        
        esc.add(new Evento(Evento.EventType.TIPO_CHEGADA, 2.0));

        while (count > 0) {
            evento = esc.prox();
            if (evento.tipo == Evento.EventType.TIPO_CHEGADA) {
                /* System.out.println("ue"); */
                CHEGADA(evento);
            } else {
                /* System.out.println("ue2"); */
                SAIDA(evento);
            }
        }

        System.out.println("\n--- Resultados da Simulação ---");
        for (int i = 0; i < fila1.Capacity() + 1; i++) {
            double[] timesF1 = fila1.getTimes();
            if (timesF1[i] == 0)
                continue;
            double prob = timesF1[i] / TempoGlobal;
            System.out.printf("Estado %d clientes: Tempo = %.4f, Probabilidade = %.2f%%\n",
                    i, timesF1[i], prob * 100);
        }
        System.out.printf("Tempo total de simulação: %.4f\n", TempoGlobal);
        System.out.println("Clientes perdidos: " + perdas);
    }
}
