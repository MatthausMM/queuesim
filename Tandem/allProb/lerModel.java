package Tandem.allProb;

import java.io.*;
import java.util.*;

public class lerModel {
    private Map<String, Object> yamlData;
    private Map<String, Integer> filaIndices;
    
    // Classe auxiliar para armazenar informações de conexão
    private static class ConnectionInfo {
        double probability;
        int targetIndex;
        
        ConnectionInfo(double probability, int targetIndex) {
            this.probability = probability;
            this.targetIndex = targetIndex;
        }
    }
    
    public lerModel(String filename) throws IOException {
        this.filaIndices = new HashMap<>();
        carregarYaml(filename);
    }
    
    @SuppressWarnings("unchecked")
    private void carregarYaml(String filename) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            yamlData = parseYaml(reader);
        }
    }
    
    @SuppressWarnings("unchecked")
    private Map<String, Object> parseYaml(BufferedReader reader) throws IOException {
        Map<String, Object> data = new HashMap<>();
        String line;
        String currentSection = null;
        String currentQueue = null;
        
        Map<String, Object> pendingNetworkItem = null;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.isEmpty() || line.startsWith("!") || line.startsWith("#")) {
                continue;
            }

            // Detectar seções principais
            if (line.equals("arrivals:")) {
                currentSection = "arrivals";
                data.put("arrivals", new HashMap<String, Double>());
                continue;
            } else if (line.equals("queues:")) {
                currentSection = "queues";
                data.put("queues", new HashMap<String, Map<String, Object>>());
                continue;
            } else if (line.equals("network:")) {
                currentSection = "network";
                data.put("network", new ArrayList<Map<String, Object>>());
                continue;
            }

            // Processar conteúdo das seções
            if (currentSection != null) {
                if (currentSection.equals("arrivals")) {
                    if (line.contains(":")) {
                        String[] parts = line.split(":", 2);
                        String queueName = parts[0].trim();
                        double arrivalRate = Double.parseDouble(parts[1].trim());
                        ((Map<String, Double>) data.get("arrivals")).put(queueName, arrivalRate);
                    }
                } else if (currentSection.equals("queues")) {
                    if (line.endsWith(":") && !line.contains(" ")) {
                        // Nova fila
                        currentQueue = line.substring(0, line.length() - 1).trim();
                        ((Map<String, Map<String, Object>>) data.get("queues")).put(currentQueue, new HashMap<String, Object>());
                    } else if (currentQueue != null && line.contains(":")) {
                        // Propriedade da fila atual
                        String[] parts = line.split(":", 2);
                        String key = parts[0].trim();
                        String value = parts[1].trim();

                        Map<String, Object> queueData = ((Map<String, Map<String, Object>>) data.get("queues")).get(currentQueue);

                        // Converter para o tipo apropriado
                        if (key.equals("servers") || key.equals("capacity")) {
                            queueData.put(key, Integer.parseInt(value));
                        } else {
                            queueData.put(key, Double.parseDouble(value));
                        }
                    }
                } else if (currentSection.equals("network")) {
                    if (line.startsWith("-")) {
                        // Se já existe um item pendente, adiciona à lista
                        if (pendingNetworkItem != null && !pendingNetworkItem.isEmpty()) {
                            ((List<Map<String, Object>>) data.get("network")).add(pendingNetworkItem);
                        }
                        pendingNetworkItem = new HashMap<>();
                        
                        // Processar apenas o resto da linha se houver conteúdo direto após o '-'
                        // Normalmente o '-' indica apenas um novo item, e as propriedades vêm nas linhas seguintes
                        String restOfLine = line.substring(1).trim();
                        if (!restOfLine.isEmpty() && restOfLine.contains(":")) {
                            String[] parts = restOfLine.split(":", 2);
                            String key = parts[0].trim();
                            String value = parts[1].trim();
                            
                            if (key.equals("probability")) {
                                pendingNetworkItem.put(key, Double.parseDouble(value));
                            } else {
                                pendingNetworkItem.put(key, value);
                            }
                        }
                    } else if (line.contains(":")) {
                        String[] parts = line.split(":", 2);
                        String key = parts[0].trim();
                        String value = parts[1].trim();

                        if (pendingNetworkItem == null) {
                            pendingNetworkItem = new HashMap<>();
                        }

                        if (key.equals("probability")) {
                            pendingNetworkItem.put(key, Double.parseDouble(value));
                        } else {
                            pendingNetworkItem.put(key, value);
                        }
                    }
                }
            }
        }
        // Adiciona o último item pendente, se existir
        if (pendingNetworkItem != null && !pendingNetworkItem.isEmpty()) {
            ((List<Map<String, Object>>) data.get("network")).add(pendingNetworkItem);
        }
        
        // Debug removido - dados parseados com sucesso
        
        return data;
    }
    
    @SuppressWarnings("unchecked")
    public ArrayList<fila> criarFilas() {
        ArrayList<fila> filas = new ArrayList<>();
        Map<String, Map<String, Object>> queues = (Map<String, Map<String, Object>>) yamlData.get("queues");
        Map<String, Double> arrivals = (Map<String, Double>) yamlData.get("arrivals");
        List<Map<String, Object>> network = (List<Map<String, Object>>) yamlData.get("network");
        
        // Criar mapeamento de nomes para índices
        int index = 0;
        for (String queueName : queues.keySet()) {
            filaIndices.put(queueName, index++);
        }
        
        // Processar cada fila
        for (String queueName : queues.keySet()) {
            Map<String, Object> queueData = queues.get(queueName);
            
            // Parâmetros básicos da fila
            int servers = (Integer) queueData.get("servers");
            Integer capacity = (Integer) queueData.get("capacity");
            if (capacity == null) {
                capacity = 1000000; // Capacidade infinita se não especificada
            }
            
            // Tempos de chegada (só para filas que recebem chegadas externas)
            double minArrival = 0.0;
            double maxArrival = 0.0;
            if (arrivals != null && arrivals.containsKey(queueName)) {
                Double minArr = (Double) queueData.get("minArrival");
                Double maxArr = (Double) queueData.get("maxArrival");
                if (minArr != null && maxArr != null) {
                    minArrival = minArr;
                    maxArrival = maxArr;
                }
            }
            
            // Tempos de serviço
            double minService = (Double) queueData.get("minService");
            double maxService = (Double) queueData.get("maxService");
            
            // Calcular probabilidades e destinos
            ArrayList<Double> probsList = new ArrayList<>();
            ArrayList<Integer> destinosList = new ArrayList<>();
            
            // Encontrar conexões desta fila na rede
            double totalProb = 0.0;
            List<ConnectionInfo> connections = new ArrayList<>();
            
            if (network != null) {
                for (Map<String, Object> connection : network) {
                    String source = (String) connection.get("source");
                    if (source != null && source.equals(queueName)) {
                        String target = (String) connection.get("target");
                        double probability = (Double) connection.get("probability");
                        
                        int targetIndex;
                        if (filaIndices.containsKey(target)) {
                            targetIndex = filaIndices.get(target);
                        } else {
                            targetIndex = -1; // Saída do sistema
                        }
                        
                        connections.add(new ConnectionInfo(probability, targetIndex));
                        totalProb += probability;
                    }
                }
                
                // Ordenar conexões por probabilidade crescente
                connections.sort((a, b) -> Double.compare(a.probability, b.probability));
                
                // Adicionar às listas na ordem correta
                for (ConnectionInfo conn : connections) {
                    probsList.add(conn.probability);
                    destinosList.add(conn.targetIndex);
                }
            }
            
            // Se não há conexões definidas ou probabilidade total < 1, adicionar saída
            if (probsList.isEmpty() || totalProb < 1.0) {
                probsList.add(1.0 - totalProb);
                destinosList.add(-1);
            }
            
            // Converter para arrays
            double[] probs = probsList.stream().mapToDouble(Double::doubleValue).toArray();
            int[] destinos = destinosList.stream().mapToInt(Integer::intValue).toArray();
            
            // Criar a fila
            fila novaFila = new fila(servers, capacity, minArrival, maxArrival, 
                                   minService, maxService, probs, destinos);
            filas.add(novaFila);
            
            if (capacity == 1000000) {
                // Capacidade infinita
                System.out.println("Fila " + queueName + " criada: G/G/" + servers);
            }
            else System.out.println("Fila " + queueName + " criada: G/G/" + servers + "/" + capacity);
            System.out.println("  Chegada: " + minArrival + " ... " + maxArrival);
            System.out.println("  Serviço: " + minService + " ... " + maxService);
            System.out.print("  Destinos: ");
            for (int i = 0; i < probs.length; i++) {
                String dest;
                if (destinos[i] == -1) {
                    dest = "SAIDA";
                } else {
                    String nomeDestino = getNomeFilaPorIndice(destinos[i]);
                    dest = (nomeDestino != null) ? nomeDestino : "Q" + (destinos[i] + 1);
                }
                System.out.printf("%s(%.1f%%) ", dest, probs[i] * 100);
            }
            System.out.println();
        }
        
        return filas;
    }
    
    @SuppressWarnings("unchecked")
    public Map<String, Double> getChegadasExternas() {
        return (Map<String, Double>) yamlData.get("arrivals");
    }
    
    public int getIndiceFilaPorNome(String nome) {
        return filaIndices.getOrDefault(nome, -1);
    }
    
    public String getNomeFilaPorIndice(int indice) {
        for (Map.Entry<String, Integer> entry : filaIndices.entrySet()) {
            if (entry.getValue() == indice) {
                return entry.getKey();
            }
        }
        return null;
    }
}
