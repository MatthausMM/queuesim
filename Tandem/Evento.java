package Tandem;

public class Evento {

    public enum EventType {
        TIPO_CHEGADA, TIPO_SAIDA, TIPO_PASSAGEM
    }

    public EventType tipo;
    public double tempo;

    public Evento(EventType tipo, double tempo) {
        this.tipo = tipo;
        this.tempo = tempo;
    }
    
}