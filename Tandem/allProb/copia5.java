package Tandem.allProb;

import java.util.*;

import java.lang.Math;
import java.math.BigInteger;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class copia5 {

    static int count = 100000;

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
        //System.out.println("random - " + result);
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
        //System.out.println("tempoChegada:");
        return f.getMinArrival() + (f.getMaxArrival() - f.getMinArrival()) * NextRandom();
    }

    static double tempoAtendimento(int indice) {
        fila f = filas.get(indice);
        //System.out.println("tempoAtendimento:");
        return f.getMinService() + (f.getMaxService() - f.getMinService()) * NextRandom();
    }

    // ------------------------------------------------

    static void agendarProximoAtendimento(int indiceFilaOrigem) {
        int destino = selecionarDestino(indiceFilaOrigem);
        double tempoAtend = TempoGlobal + tempoAtendimento(indiceFilaOrigem);

        //System.out.println("Destino selecionado: " + destino);

        if (destino == -1) {
            // Saída do sistema
            esc.add(new Evento(Evento.EventType.TIPO_SAIDA, tempoAtend, indiceFilaOrigem, -1));
        } else {
            // Passagem para outra fila
            esc.add(new Evento(Evento.EventType.TIPO_PASSAGEM, tempoAtend, indiceFilaOrigem, destino));
        }
    }

    static int selecionarDestino(int indiceFila) {
        fila f = filas.get(indiceFila);
        double[] probs = f.getProbs();
        int[] destinos = f.getDestinos();

        //System.out.println("selecionarDestino:");
        double randomValue = NextRandom();
        double sum = 0.0;

        for (int i = 0; i < probs.length; i++) {
            sum += probs[i];
            //System.out.println("Comparando " + randomValue + " com " + sum);
            if (randomValue <= sum) {
                return destinos[i];
            }
        }

        // Fallback - retorna o último destino
        return destinos[destinos.length - 1];
    }

    // ------------------------------------------------

    static void CHEGADA(Evento evento) {
        //System.out.println("chegada");
        TempoGlobal = evento.tempo;
        atualizaEstatisticas();

        fila origem = filas.get(evento.filaDestino);
        if (origem.Status() < origem.Capacity()) {
            origem.In();

            // Agendar próximo atendimento apenas se houver servidores disponíveis
            if (origem.Status() <= origem.Server()) {
                agendarProximoAtendimento(evento.filaDestino);
            }
        } else {
            origem.Loss();
        }
        chegada = TempoGlobal + tempoChegada(evento.filaDestino);
        esc.add(new Evento(Evento.EventType.TIPO_CHEGADA, chegada, -1, evento.filaDestino));
    }

    static void SAIDA(Evento evento) {
        //System.out.println("saida");
        TempoGlobal = evento.tempo;
        atualizaEstatisticas();

        fila origem = filas.get(evento.filaOrigem);
        origem.Out();

        // Agendar próximo atendimento se ainda há clientes esperando
        if (origem.Status() >= origem.Server()) {
            if (origem.getProbs().length == 1 && origem.getProbs()[0] == 1.0 && origem.getDestinos()[0] == -1) {
                saida = TempoGlobal + tempoAtendimento(evento.filaOrigem);
                esc.add(new Evento(Evento.EventType.TIPO_SAIDA, saida, evento.filaOrigem, -1));
            } else {
                agendarProximoAtendimento(evento.filaOrigem);
            }
        }
    }

    static void PASSAGEM(Evento evento) {
        //System.out.println("passagem");
        TempoGlobal = evento.tempo;
        atualizaEstatisticas();

        fila origem = filas.get(evento.filaOrigem);
        fila destino = filas.get(evento.filaDestino);

        origem.Out();

        // Agendar próximo atendimento se ainda há clientes esperando na origem
        if (origem.Status() >= origem.Server()) {
            agendarProximoAtendimento(evento.filaOrigem);
        }

        if (destino.Status() < destino.Capacity()) {
            destino.In();

            // Agendar próximo atendimento apenas se houver servidores disponíveis no
            // destino

            if (destino.Status() <= destino.Server()) {
                if (destino.getProbs().length == 1 && destino.getProbs()[0] == 1.0 && destino.getDestinos()[0] == -1) {
                    saida = TempoGlobal + tempoAtendimento(evento.filaDestino);
                    esc.add(new Evento(Evento.EventType.TIPO_SAIDA, saida, evento.filaDestino, -1));
                } else {
                    agendarProximoAtendimento(evento.filaDestino);
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
        try {
            // Carregar configuração do arquivo YAML
            String modelFile = args.length > 0 ? args[0] : "/workspaces/queuesim/Tandem/allProb/model2.yml";
            System.out.println("Carregando modelo de: " + modelFile);
            
            lerModel modelo = new lerModel(modelFile);
            filas = modelo.criarFilas();
            
            // Inicializar array de tempos anteriores
            temposAnteriores = new double[filas.size()];
            for (int i = 0; i < temposAnteriores.length; i++) {
                temposAnteriores[i] = 0.0;
            }
            
            // Agendar chegadas externas
            Map<String, Double> chegadasExternas = modelo.getChegadasExternas();
            if (chegadasExternas != null) {
                for (Map.Entry<String, Double> entry : chegadasExternas.entrySet()) {
                    String nomeFilaChegada = entry.getKey();
                    double tempoChegadaInicial = entry.getValue();
                    int indiceFilaChegada = modelo.getIndiceFilaPorNome(nomeFilaChegada);
                    
                    if (indiceFilaChegada >= 0) {
                        esc.add(new Evento(Evento.EventType.TIPO_CHEGADA, tempoChegadaInicial, -1, indiceFilaChegada));
                        System.out.println("Primeira chegada em " + nomeFilaChegada + " agendada para t=" + tempoChegadaInicial);
                    }
                }
            }
            
            System.out.println("\n--- Iniciando Simulação ---");
            
            // Executar simulação
            while (count > 0) {
                evento = esc.prox();
                // System.out.println("\nPróximo evento: " + evento);
                // System.out.println("Estados das filas:");
                // for (int i = 0; i < filas.size(); i++) {
                //     System.out.println("Fila " + (i + 1) + ": " + filas.get(i).Status() + " clientes");
                // }
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

            // Mostrar resultados
            System.out.println("\n--- Resultados da Simulação ---");
            for (int filaIndex = 0; filaIndex < filas.size(); filaIndex++) {
                fila f = filas.get(filaIndex);
                System.out.println("Fila " + (filaIndex + 1) + ": (G/G/" + f.Server() + "/" + f.Capacity() + ")");
                
                // Mostrar chegada apenas se a fila recebe chegadas externas
                if (f.getMinArrival() > 0 || f.getMaxArrival() > 0) {
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

        } catch (IOException e) {
            System.err.println("Erro ao carregar arquivo de modelo: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Erro durante a simulação: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (writer != null)
                    writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
