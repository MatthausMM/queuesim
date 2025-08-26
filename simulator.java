import java.util.*;

public class simulator {

    public static double previous, a, c, M;
    public static EventType evento;
    public static double tempo_simulacao = 0.0;

    public static double NextRandom() {
        previous = ((a * previous) + c) % M;
        return (double) previous/M;
    }

    public static enum EventType {
        tipo_chegada,
        tipo_saida
    }

    public static void main(String[] args) {
        
        // Teste do gerador de numeros aleatorios
        previous = 1337;
        a = 3344556677.0;
        c = 17;
        M = Math.pow(2, 32);

        System.out.println(previous + " " + a + " " + c + " " + M);
        System.out.println("Random Numbers:");
        double n = NextRandom();
        for (int i = 0; i < 10; i++) {
            n = NextRandom();
            System.out.println(n);
        }

        for (int i = 0; i < 2; i++) {
            evento = NextEvent();

            if (evento == tipo_chegada) {
                Chegada(evento);
            } else if (evento == tipo_saida) {
                Saida(evento);
            }
        }

        for int i = 0; i < K+1; i++) {
            System.out.println(i + ": " + times[i] + " (" + (times[i]/tempo_simulacao) + "%)\n");
        }

    }
    
}