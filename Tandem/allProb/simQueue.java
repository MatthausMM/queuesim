package Tandem.allProb;

import java.util.*;

import java.lang.Math;
import java.math.BigInteger;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class simQueue {

    static int count = 100;

    static BigInteger previous = BigInteger.valueOf(1337);
    static BigInteger a = BigInteger.valueOf(3344556677L);
    static BigInteger c = BigInteger.valueOf(17);
    static BigInteger M = BigInteger.valueOf((long) Math.pow(2, 32));

    static ArrayList<fila> filas = new ArrayList<>();

    static double TempoGlobal = 0.0;

    static double[] temposAnteriores;

    static double chegada;
    static double saida;

    static Evento evento;

    static escalonador esc = new escalonador();

    static BufferedWriter writer;

    static {
        try {
            writer = new BufferedWriter(new FileWriter("randoms.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ------------------------------------------------

    static double NextRandom() {
        previous = (a.multiply(previous).add(c)).mod(M);
        double result = previous.doubleValue() / M.doubleValue();
        if (count > 0) {
            try {
                writer.write("- " + result + "\n");
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        count--;
        return result;
    }

    // ------------------------------------------------

    static double tempoChegada(int indice) {
        fila f = filas.get(indice);
        return f.getMinArrival() + (f.getMaxArrival() - f.getMinArrival()) * NextRandom();
    }

    static double tempoAtendimento(int indice) {
        fila f = filas.get(indice);
        return f.getMinService() + (f.getMaxService() - f.getMinService()) * NextRandom();
    }

    // ------------------------------------------------

    static void CHEGADA(Evento evento) {
        TempoGlobal = evento.tempo;
        atualizaEstatisticas();

        fila origem = filas.get(evento.filaDestino);
        if (origem.Status() < origem.Capacity()) {
            origem.In();
            if (origem.Status() <= origem.Server()) {
                double sum = 0.0;
                double[] probs = origem.getProbs();
                for (int i = 0; i < probs.length; i++) {
                    System.out.println(sum + " " + probs[i]);
                    if (NextRandom() < sum + probs[i]) {
                        int iDest = origem.getDestinos()[i];
                        System.out.println(iDest);
                        if (iDest == -1) {
                            saida = TempoGlobal + tempoAtendimento(evento.filaDestino);
                            esc.add(new Evento(Evento.EventType.TIPO_SAIDA, saida, evento.filaDestino, iDest));
                            break;
                        }
                        saida = TempoGlobal + tempoAtendimento(iDest);
                        esc.add(new Evento(Evento.EventType.TIPO_PASSAGEM, saida, evento.filaDestino, iDest));
                        break;
                    }
                    sum += probs[i];
                }
            }
        } else {
            origem.Loss();
        }
        chegada = TempoGlobal + tempoChegada(evento.filaDestino);
        esc.add(new Evento(Evento.EventType.TIPO_CHEGADA, chegada, -1, evento.filaDestino));
    }

    static void SAIDA(Evento evento) {
        TempoGlobal = evento.tempo;
        atualizaEstatisticas();

        fila origem = filas.get(evento.filaOrigem);
        origem.Out();

        if (origem.Status() >= origem.Server()) {
            double sum = 0.0;
            double[] probs = origem.getProbs();
            for (int i = 0; i < probs.length; i++) {
                System.out.println(sum + " " + probs[i]);
                if (NextRandom() < sum + probs[i]) {
                    int iDest = origem.getDestinos()[i];
                    System.out.println(iDest);
                    if (iDest == -1) {
                        saida = TempoGlobal + tempoAtendimento(evento.filaOrigem);
                        esc.add(new Evento(Evento.EventType.TIPO_SAIDA, saida, evento.filaOrigem, iDest));
                        break;
                    }
                    saida = TempoGlobal + tempoAtendimento(iDest);
                    esc.add(new Evento(Evento.EventType.TIPO_PASSAGEM, saida, evento.filaOrigem, iDest));
                    break;
                }
                sum += probs[i];
            }
        }
    }

    static void PASSAGEM(Evento evento) {
        TempoGlobal = evento.tempo;
        atualizaEstatisticas();

        fila origem = filas.get(evento.filaOrigem);
        fila destino = filas.get(evento.filaDestino);

        origem.Out();
        if (origem.Status() >= origem.Server()) {
            double sum = 0.0;
            double[] probs = origem.getProbs();
            for (int i = 0; i < probs.length; i++) {
                System.out.println(sum + " " + probs[i]);
                if (NextRandom() < sum + probs[i]) {
                    int iDest = origem.getDestinos()[i];
                    System.out.println(iDest);
                    if (iDest == -1) {
                        saida = TempoGlobal + tempoAtendimento(evento.filaOrigem);
                        esc.add(new Evento(Evento.EventType.TIPO_SAIDA, saida, evento.filaOrigem, iDest));
                        break;
                    }
                    saida = TempoGlobal + tempoAtendimento(iDest);
                    esc.add(new Evento(Evento.EventType.TIPO_PASSAGEM, saida, evento.filaOrigem, iDest));
                    break;
                }
                sum += probs[i];
            }
        }

        if (destino.Status() < destino.Capacity()) {
            destino.In();
            if (destino.Status() <= destino.Server()) {
                double sum = 0.0;
                double[] probs = destino.getProbs();
                for (int i = 0; i < probs.length; i++) {
                    System.out.println(sum + " " + probs[i]);
                    if (NextRandom() < sum + probs[i]) {
                        int iDest = destino.getDestinos()[i];
                        System.out.println(iDest);
                        if (iDest == -1) {
                            saida = TempoGlobal + tempoAtendimento(evento.filaDestino);
                            esc.add(new Evento(Evento.EventType.TIPO_SAIDA, saida, evento.filaDestino, iDest));
                            break;
                        }
                        saida = TempoGlobal + tempoAtendimento(iDest);
                        esc.add(new Evento(Evento.EventType.TIPO_PASSAGEM, saida, evento.filaDestino, iDest));
                        break;
                    }
                    sum += probs[i];
                }
            }
        } else {
            destino.Loss();
        }
    }

    // ------------------------------------------------

    static void atualizaEstatisticas() {
        for (int indice = 0; indice < filas.size(); indice++) {
            double delta = TempoGlobal - temposAnteriores[indice];
            fila f = filas.get(indice);
            if (delta > 0 && f.Status() >= 0) {
                f.getTimes()[f.Status()] += delta;
            }
            temposAnteriores[indice] = TempoGlobal;
        }
    }

    // ------------------------------------------------

    public static void main(String[] args) {

        Scanner in = new Scanner(System.in);

        /*
         * if (in.next().equals("y")) {
         * for (int i = 0; i < 10; i++) {
         * System.out.println("- " + NextRandom());
         * }
         * previous = 1337; // reset
         * System.out.println("---");
         * }
         */

        /*
         * System.out.println("Escolha o sistema de filas:");
         * System.out.println("1 -> G/G/2/5");
         * System.out.println("2 -> G/G/2/5");
         * int escolha = in.nextInt();
         * if (escolha == 1) {
         * fila1 = new fila(1, 5, 2.0, 5.0, 3.0, 5.0);
         * } else {
         * fila1 = new fila(2, 5, 2.0, 5.0, 3.0, 5.0);
         * }
         */

        System.out.println("Configurando fila 1 (G/G/2/5) chegadas entre 2..3 e atendimento entre 4..7");
        fila fila1 = new fila(2, 4, 2.0, 3.0, 4.0, 7.0, new double[] { 0.7, 0.3 }, new int[] { 1, -1 });
        System.out.println("Configurando fila 2 (G/G/1) atendimento entre 4..8");
        fila fila2 = new fila(1, 1000000, 0.0, 0.0, 4.0, 8.0, new double[] { 1.0 }, new int[] { -1 });

        filas.add(fila1);
        filas.add(fila2);

        temposAnteriores = new double[filas.size()];
        for (int i = 0; i < temposAnteriores.length; i++) {
            temposAnteriores[i] = 0.0;
        }

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
        // fila1 = new fila(servidores1, capacidade1, minChegada1, maxChegada1,
        // minAtendimento1, maxAtendimento1);

        // System.out.println("Insira os parâmetros da fila 2:");
        // System.out.print("Número de servidores: ");
        // int servidores2 = in.nextInt();
        // System.out.print("Capacidade total: ");
        // int capacidade2 = in.nextInt();
        // System.out.print("Tempo mínimo de atendimento: ");
        // double minAtendimento2 = in.nextDouble();
        // System.out.print("Tempo máximo de atendimento: ");
        // double maxAtendimento2 = in.nextDouble();
        // fila2 = new fila(servidores2, capacidade2, 0.0, 0.0, minAtendimento2,
        // maxAtendimento2);

        esc.add(new Evento(Evento.EventType.TIPO_CHEGADA, 2.0, -1, 0));

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
        for (int filaIndex = 0; filaIndex < filas.size(); filaIndex++) {
            fila f = filas.get(filaIndex);
            System.out.println("Fila " + (filaIndex + 1) + ": (G/G/" + f.Server() + "/" + f.Capacity() + ")");
            if (filaIndex == 0) {
                System.out.println("Chegada: " + f.getMinArrival() + " ... " + f.getMaxArrival());
            }
            System.out.println("Serviço: " + f.getMinService() + " ... " + f.getMaxService());

            for (int i = 0; i < f.Capacity() + 1; i++) {
                double[] times = f.getTimes();
                if (times[i] == 0)
                    continue;
                double prob = times[i] / TempoGlobal;
                System.out.printf("Estado %d clientes: Tempo = %.4f, Probabilidade = %.2f%%\n",
                        i, times[i], prob * 100);
            }
            System.out.println("Clientes perdidos: " + f.getLoss() + "\n");
        }
        System.out.printf("Tempo total de simulação: %.4f\n", TempoGlobal);

        try {
            if (writer != null)
                writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
