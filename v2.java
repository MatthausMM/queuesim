import java.util.*;

public class v2 {

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

    static int maxEventos = 10; // agora controlado por contador
    static int usados = 0;

    enum EventType {
        TIPO_CHEGADA,
        TIPO_SAIDA
    }

    // ------------------------------------------------
    static double NextRandom() {
        previous = ((a * previous) + c) % M;
        usados++;
        return (double) previous / M;
    }

    static double tempoChegada() {
        return 2 + (5 - 2) * NextRandom();
    }

    static double tempoAtendimento() {
        return 3 + (5 - 3) * NextRandom();
    }

    // ------------------------------------------------
    static EventType NextEvent() {
        int servidorSaida = -1;
        double menorSaida = Double.MAX_VALUE;

        for (int i = 0; i < numServidores; i++) {
            if (proximaSaida[i] < menorSaida) {
                menorSaida = proximaSaida[i];
                servidorSaida = i;
            }
        }

        if (proximaChegada < menorSaida) {
            return EventType.TIPO_CHEGADA;
        } else {
            return EventType.TIPO_SAIDA;
        }
    }

    // ------------------------------------------------
    static void chegada() {
        tempoAtual = proximaChegada;
        atualizaEstatisticas();

        if (numClientesNaFila < capacidadeFila) {
            numClientesNaFila++;

            // se algum servidor estiver livre, agenda atendimento
            for (int i = 0; i < numServidores; i++) {
                if (proximaSaida[i] == Double.MAX_VALUE) {
                    proximaSaida[i] = tempoAtual + tempoAtendimento();
                    break;
                }
            }
        } else {
            perdas++; // cliente perdido
        }

        proximaChegada = tempoAtual + tempoChegada();
    }

    static void saida() {
        // encontra qual servidor disparou a saída
        int servidorSaida = -1;
        double menorSaida = Double.MAX_VALUE;

        for (int i = 0; i < numServidores; i++) {
            if (proximaSaida[i] < menorSaida) {
                menorSaida = proximaSaida[i];
                servidorSaida = i;
            }
        }

        tempoAtual = proximaSaida[servidorSaida];
        atualizaEstatisticas();

        numClientesNaFila--;
        if (numClientesNaFila >= numServidores) {
            proximaSaida[servidorSaida] = tempoAtual + tempoAtendimento();
        } else {
            proximaSaida[servidorSaida] = Double.MAX_VALUE;
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
            System.out.printf("%d: %.4f (%.2f%%)\n",
                    i, tempoEmCadaEstado[i], prob * 100);
        }
        System.out.printf("Tempo total de simulação: %.4f\n", tempoTotal);
        System.out.println("Clientes perdidos: " + perdas);
    }

    // ------------------------------------------------
    public static void main(String[] args) {

        // inicializa gerador
        previous = 1337;
        a = 3344556677L;
        c = 17;
        M = (long) Math.pow(2, 32);

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

        // laço principal -> segue o material do Moodle
        int count = maxEventos;
        while (count > 0) {
            EventType evento = NextEvent();
            if (evento == EventType.TIPO_CHEGADA) {
                chegada();
            } else {
                saida();
            }
            count--;
        }

        atualizaEstatisticas();
        mostraResultados();
    }
}
