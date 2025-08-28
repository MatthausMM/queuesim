import java.util.*;

public class simulator {

    static long previous, a, c, M;

    static double tempoAtual = 0.0;
    static int numClientesNaFila = 0;
    static int capacidadeFila;

    static double[] tempoEmCadaEstado;
    static double ultimoEvento = 0.0;

    static double proximaChegada = 2.0;
    static double[] proximaSaida;

    static int numServidores;
    static int perdas = 0;

    static int maxAleatorios = 100000;
    static int usados = 0;

    static double NextRandom() {
        previous = ((a * previous) + c) % M;
        usados++;
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

    static void chegada() {
        tempoAtual = proximaChegada;
        atualizaEstatisticas();

        if (numClientesNaFila < capacidadeFila) {

            numClientesNaFila++;

            for (int i = 0; i < numServidores; i++) {
                if (proximaSaida[i] == Double.MAX_VALUE) {
                    proximaSaida[i] = tempoAtual + tempoAtendimento();
                    break;
                }
            }

        } else {
            perdas++;
        }

        if (usados < maxAleatorios) {
            proximaChegada = tempoAtual + tempoChegada();
        } else {
            proximaChegada = Double.MAX_VALUE;
        }
    }

    static void saida(int servidor) {
        tempoAtual = proximaSaida[servidor];
        atualizaEstatisticas();

        numClientesNaFila--;
        if (numClientesNaFila >= numServidores) {
            proximaSaida[servidor] = tempoAtual + tempoAtendimento();
        } else {
            proximaSaida[servidor] = Double.MAX_VALUE;
        }
    }

    // ------------------------------------------------

    static void atualizaEstatisticas() {
        double delta = tempoAtual - ultimoEvento;
        if (delta > 0) {
            tempoEmCadaEstado[numClientesNaFila] += delta;
        }
        ultimoEvento = tempoAtual;
    }

    // ------------------------------------------------

    static void mostraResultados() {
        double tempoTotal = tempoAtual;
        System.out.println("\n--- Resultados da Simulação ---");
        for (int i = 0; i < tempoEmCadaEstado.length; i++) {
            if (tempoEmCadaEstado[i] == 0)
                continue;
            double prob = tempoEmCadaEstado[i] / tempoTotal;
            System.out.printf("Estado %d clientes: Tempo = %.4f, Probabilidade = %.2f%%\n",
                    i, tempoEmCadaEstado[i], prob * 100);
        }
        System.out.printf("Tempo total de simulação: %.4f\n", tempoTotal);
        System.out.println("Clientes perdidos: " + perdas);
    }

    // ------------------------------------------------

    public static void main(String[] args) {

        // Teste do gerador de numeros aleatorios
        previous = 1337;
        a = 3344556677L;
        c = 17;
        M = (long) Math.pow(2, 32);

        System.out.println("Random Numbers:");
        double n;
        System.out.println("Previous: " + previous + " a: " + a + " c: " + c + " M: " + M);
        for (int i = 0; i < 10; i++) {
            n = NextRandom();
            System.out.println(n);
        }

        previous = 1337; // reset
        usados = 0;

        Scanner in = new Scanner(System.in);

        System.out.println("Escolha o sistema de filas:");
        System.out.println("1 -> G/G/1/5");
        System.out.println("2 -> G/G/2/5");
        int escolha = in.nextInt();

        if (escolha == 1) {
            numServidores = 1;
            capacidadeFila = 5;
        } else {
            numServidores = 2;
            capacidadeFila = 5;
        }

        tempoEmCadaEstado = new double[capacidadeFila + 1];
        proximaSaida = new double[numServidores];
        for (int i = 0; i < numServidores; i++) {
            proximaSaida[i] = Double.MAX_VALUE;
        }

        while (usados < maxAleatorios) {

            int servidorSaida = -1;
            double menorSaida = Double.MAX_VALUE;
            for (int i = 0; i < numServidores; i++) {
                if (proximaSaida[i] < menorSaida) {
                    menorSaida = proximaSaida[i];
                    servidorSaida = i;
                }
            }

            if (proximaChegada < menorSaida) {
                chegada();
            } else {
                saida(servidorSaida);
            }
        }

        atualizaEstatisticas();
        mostraResultados();

    }

}