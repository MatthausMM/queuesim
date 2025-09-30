package Tandem;

public class Evento2 {

    public enum EventType {
        TIPO_CHEGADA, TIPO_SAIDA1, TIPO_SAIDA2, TIPO_PASSAGEM
    }

    public EventType tipo;
    public double tempo;

    public Evento2(EventType tipo, double tempo) {
        this.tipo = tipo;
        this.tempo = tempo;
    }
    
    @Override
    public String toString() {
        return "Evento2{" +
                "tipo=" + tipo +
                ", tempo=" + tempo +
                '}';
    }
}