package Tandem;
import java.util.*;
import java.lang.Math;
import java.math.BigInteger;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class vMMTandemBigInt {

    static int count = 100000; // números aleatórios a serem utilizados

    static BigInteger previous = BigInteger.valueOf(1337);
    static BigInteger a = BigInteger.valueOf(3344556677L);
    static BigInteger c = BigInteger.valueOf(17);
    static BigInteger M = BigInteger.valueOf((long) Math.pow(2, 32));

    static fila fila1;
    static fila fila2;

    static double TempoGlobal = 0.0;
    static double tempoAnterior1 = 0.0;
    static double tempoAnterior2 = 0.0;

    static double chegada;
    static double saida;

    static int FILA1 = 0; // número de clientes na fila1
    static int FILA2 = 0; // número de clientes na fila2

    static Evento evento;

    static escalonador esc = new escalonador();

    static BufferedWriter writer;

    static {
        try {
            writer = new BufferedWriter(new FileWriter("randoms.txt", true)); // true para append
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ------------------------------------------------

    static double NextRandom() {
        previous = (a.multiply(previous).add(c)).mod(M);
        double result = Math.abs(previous.doubleValue() / M.doubleValue());
        try {
            writer.write("- " + result + "\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        count--;
        return result;
    }

    // ------------------------------------------------

    static double tempoChegada(fila f) {
        return f.getMinArrival() + (f.getMaxArrival() - f.getMinArrival()) * NextRandom();
    }

    static double tempoAtendimento(fila f) {
        return f.getMinService() + (f.getMaxService() - f.getMinService()) * NextRandom();
    }

    // ------------------------------------------------

    static void CHEGADA(Evento evento) {

        TempoGlobal = evento.tempo;
        atualizaEstatisticas1();
        atualizaEstatisticas2();

        if (fila1.Status() < fila1.Capacity()) {
            fila1.In();
            if (fila1.Status() <= fila1.Server()) {
                saida = TempoGlobal + tempoAtendimento(fila1);
                esc.add(new Evento(Evento.EventType.TIPO_PASSAGEM, saida));
            }
        } else {
            fila1.Loss();
        }
        chegada = TempoGlobal + tempoChegada(fila1);
        esc.add(new Evento(Evento.EventType.TIPO_CHEGADA, chegada));
    }

    static void SAIDA(Evento evento) {

        TempoGlobal = evento.tempo;
        atualizaEstatisticas1();
        atualizaEstatisticas2();

        fila2.Out();

        if (fila2.Status() >= fila2.Server()) {
            saida = TempoGlobal + tempoAtendimento(fila2);
            esc.add(new Evento(Evento.EventType.TIPO_SAIDA, saida));
        }
    }

    static void PASSAGEM(Evento evento) {

        TempoGlobal = evento.tempo;
        atualizaEstatisticas1();
        atualizaEstatisticas2();

        fila1.Out();
        if (fila1.Status() >= fila1.Server()) {
            double novaSaida = TempoGlobal + tempoAtendimento(fila1);
            esc.add(new Evento(Evento.EventType.TIPO_PASSAGEM, novaSaida));
        }

        if (fila2.Status() < fila2.Capacity()) {
            fila2.In();
            if (fila2.Status() <= fila2.Server()) {
                double novaSaida = TempoGlobal + tempoAtendimento(fila2);
                esc.add(new Evento(Evento.EventType.TIPO_SAIDA, novaSaida));
            }
        } else {
            fila2.Loss();
        }
    }

    // ------------------------------------------------

    static void atualizaEstatisticas1() {
        double delta = TempoGlobal - tempoAnterior1;
        if (delta > 0 && fila1.Status() >= 0/*  && fila1.Status() <= fila1.Capacity() */) {
            fila1.getTimes()[fila1.Status()] += delta;
        }
        tempoAnterior1 = TempoGlobal;
    }

    static void atualizaEstatisticas2() {
        double delta = TempoGlobal - tempoAnterior2;
        if (delta > 0 && fila2.Status() >= 0/*  && fila2.Status() <= fila2.Capacity() */) {
            fila2.getTimes()[fila2.Status()] += delta;
        }
        tempoAnterior2 = TempoGlobal;
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

        /* System.out.println("Escolha o sistema de filas:");
        System.out.println("1 -> G/G/2/5");
        System.out.println("2 -> G/G/2/5");
        int escolha = in.nextInt();
        if (escolha == 1) {
            fila1 = new fila(1, 5, 2.0, 5.0, 3.0, 5.0);
        } else {
            fila1 = new fila(2, 5, 2.0, 5.0, 3.0, 5.0);
        } */

        System.out.println("Configurando fila 1 (G/G/2/3) chegadas entre 1..4 e atendimento entre 3..4");
        fila1 = new fila(2, 3, 1.0, 4.0, 3.0, 4.0);
        System.out.println("Configurando fila 2 (G/G/1/5) atendimento entre 2..3");
        fila2 = new fila(1, 5, 0.0, 0.0, 2.0, 3.0);

        // System.out.println("Insira os parâmetros da fila 1:");
        // System.out.print("Número de servidores: ");
        // int servidores1 = in.nextInt();
        // System.out.print("Capacidade total: ");
        // int capacidade1 = in.nextInt();
        // System.out.print("Tempo mínimo de chegada: ");
        // double minChegada1 = in.nextDouble();
        // System.out.print("Tempo máximo de chegada: ");
        // double maxChegada1 = in.nextDouble();
        // System.out.print("Tempo mínimo de atendimento: ");
        // double minAtendimento1 = in.nextDouble();
        // System.out.print("Tempo máximo de atendimento: ");
        // double maxAtendimento1 = in.nextDouble();
        // fila1 = new fila(servidores1, capacidade1, minChegada1, maxChegada1, minAtendimento1, maxAtendimento1);

        // System.out.println("Insira os parâmetros da fila 2:");
        // System.out.print("Número de servidores: ");
        // int servidores2 = in.nextInt();
        // System.out.print("Capacidade total: ");
        // int capacidade2 = in.nextInt();
        // System.out.print("Tempo mínimo de atendimento: ");
        // double minAtendimento2 = in.nextDouble();
        // System.out.print("Tempo máximo de atendimento: ");
        // double maxAtendimento2 = in.nextDouble();
        // fila2 = new fila(servidores2, capacidade2, 0.0, 0.0, minAtendimento2, maxAtendimento2);

        
        esc.add(new Evento(Evento.EventType.TIPO_CHEGADA, 1.5));

        while (count > 0) {
            evento = esc.prox();
            if (evento.tipo == Evento.EventType.TIPO_CHEGADA) {
                /* System.out.println("ue"); */
                CHEGADA(evento);
            } else if (evento.tipo == Evento.EventType.TIPO_SAIDA) {
                /* System.out.println("ue2"); */
                SAIDA(evento);
            } else if (evento.tipo == Evento.EventType.TIPO_PASSAGEM) {
                /* System.out.println("ue3"); */
                PASSAGEM(evento);
            }
        }

        System.out.println("\n--- Resultados da Simulação ---");
        System.out.println("Fila 1: (G/G/" + fila1.Server() + "/" + fila1.Capacity() + ")");
        System.out.println("Chegada: " + fila1.getMinArrival() + " ... " + fila1.getMaxArrival());
        System.out.println("Serviço: " + fila1.getMinService() + " ... " + fila1.getMaxService());
        for (int i = 0; i < fila1.Capacity() + 1; i++) {
            double[] timesF1 = fila1.getTimes();
            if (timesF1[i] == 0)
                continue;
            double prob = timesF1[i] / TempoGlobal;
            System.out.printf("Estado %d clientes: Tempo = %.4f, Probabilidade = %.2f%%\n",
                    i, timesF1[i], prob * 100);
        }
        System.out.println("Clientes perdidos: " + fila1.getLoss() + "\n");
        System.out.println("Fila 2: (G/G/" + fila2.Server() + "/" + fila2.Capacity() + ")");
        System.out.println("Chegada: " + fila2.getMinArrival() + " ... " + fila2.getMaxArrival());
        System.out.println("Serviço: " + fila2.getMinService() + " ... " + fila2.getMaxService());
        for (int i = 0; i < fila2.Capacity() + 1; i++) {
            double[] timesF2 = fila2.getTimes();
            if (timesF2[i] == 0)
                continue;
            double prob = timesF2[i] / TempoGlobal;
            System.out.printf("Estado %d clientes: Tempo = %.4f, Probabilidade = %.2f%%\n",
                    i, timesF2[i], prob * 100);
        }
        System.out.println("Clientes perdidos: " + fila2.getLoss());
        System.out.printf("Tempo total de simulação: %.4f\n", TempoGlobal);

        // Fechar o writer ao final da execução
        try {
            if (writer != null) writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
