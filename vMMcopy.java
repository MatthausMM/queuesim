import java.util.*;

public class vMMcopy {

    static int count = 10; // números aleatórios a serem utilizados

    static long previous = 1337;
    static long a = 3344556677L;
    static long c = 17;
    static long M = (long) Math.pow(2, 32);

    static int K = 5; // capacidade da fila
    static int nServ; // número de servidores

    static double TempoGlobal = 0.0;
    static double[] times = new double[K + 1];
    static double tempoAnterior = 0.0;

    static double chegada = 2.0;
    static double saida = Double.MAX_VALUE;

    static int FILA = 0; // número de clientes na fila

    static int perdas = 0;

    static EventType evento;

    enum EventType {
        TIPO_CHEGADA, TIPO_SAIDA
    }

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

    static void CHEGADA(EventType evento) {

        TempoGlobal = chegada;
        atualizaEstatisticas();

        if (FILA < K) {
            FILA++;
            if (FILA <= nServ) {
                saida = TempoGlobal + tempoAtendimento();
            }
        } else {
            perdas++;
        }
        chegada = TempoGlobal + tempoChegada();
    }

    static void SAIDA(EventType evento) {

        TempoGlobal = saida;
        atualizaEstatisticas();

        FILA--;

        if (FILA >= nServ) {
            saida = TempoGlobal + tempoAtendimento();
        }
    }

    // ------------------------------------------------

    static EventType NextEvent() {
        if (chegada < saida) {
            return EventType.TIPO_CHEGADA;
        } else {
            return EventType.TIPO_SAIDA;
        }
    }

    // ------------------------------------------------

    static void atualizaEstatisticas() {
        double delta = TempoGlobal - tempoAnterior;
        if (delta > 0 && FILA >= 0 && FILA <= K) {
            times[FILA] += delta;
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
            nServ = 1;
        } else {
            nServ = 2;
            System.out.println("nServ = " + nServ);
        }
       
        while (count > 0) {
            evento = NextEvent();
            if (evento == EventType.TIPO_CHEGADA) {
                // Processar chegada
                /* System.out.println("ue"); */
                CHEGADA(evento);
            } else {
                // Processar saída
                /* System.out.println("ue2"); */
                SAIDA(evento);
            }
        }

        System.out.println("\n--- Resultados da Simulação ---");
        for (int i = 0; i < K + 1; i++) {
            if (times[i] == 0)
                continue;
            double prob = times[i] / TempoGlobal;
            System.out.printf("Estado %d clientes: Tempo = %.4f, Probabilidade = %.2f%%\n",
                    i, times[i], prob * 100);
        }
        System.out.printf("Tempo total de simulação: %.4f\n", TempoGlobal);
        System.out.println("Clientes perdidos: " + perdas);
    }
}
