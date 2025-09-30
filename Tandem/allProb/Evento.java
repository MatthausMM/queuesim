package Tandem.allProb;

public class Evento {

    public enum EventType {
        TIPO_CHEGADA, TIPO_SAIDA, TIPO_PASSAGEM
    }

    public EventType tipo;
    public double tempo;
    public int filaOrigem;
    public int filaDestino;

    public Evento(EventType tipo, double tempo, int fOrigem, int fDestino) {
        this.tipo = tipo;
        this.tempo = tempo;
        this.filaOrigem = fOrigem;
        this.filaDestino = fDestino;
    }

    @Override
    public String toString() {
        return "Evento{" +
                "tipo=" + tipo +
                ", tempo=" + tempo +
                ", filaOrigem=" + filaOrigem +
                ", filaDestino=" + filaDestino +
                '}';
    }
}