# 🎯 QueueSim

> **Simulador de Filas em Java** - Uma ferramenta para simulação de sistemas de filas com múltiplas configurações e probabilidades.

## 🚀 Como Usar

### Execução Principal

Para executar a simulação, use o arquivo principal:

```bash
vFinal.java
```

Ao iniciar, digite o nome do arquivo `.yml` desejado da pasta `allProb`. Este arquivo gerencia toda a lógica de leitura dos modelos YAML e executa a simulação.

### 📋 Arquivos de Configuração Disponíveis

- `model.yml` - Modelo básico (duas filas 70%)
- `model1fila.yml` - Configuração para uma fila
- `model2fila.yml` - Configuração para duas filas (100%)
- `modelprob.yml` - Modelo com probabilidades

---

## 📊 Versões dos Simuladores

| Simulador | Descrição | Compatibilidade |
|-----------|-----------|-----------------|
| `vFinal.java` | 🎯 **Principal** - Suporta qualquer modelo `.yml` | Todos os modelos |
| `vModelProb.java` | Criação manual de filas | Apenas `modelprob.yml` |
| `simuladorTandem.java` | Duas filas em sequência | 100% de probabilidade |
| `simuladorTandem2.java` | Duas filas em sequência | 70% de probabilidade |

---

## ⚙️ Funcionalidades

### 🎲 Geração de Números Aleatórios

- Os valores gerados por `nextRandom()` são automaticamente salvos em `randoms.txt`
- Útil para testes e validação com o simulador do módulo 3

### 🔄 Processamento de Probabilidades

- As probabilidades são automaticamente ordenadas de forma crescente
- O `lerModel` gerencia essa ordenação independente da ordem no arquivo YAML

### 📁 Criação Automática de Filas

- O `lerModel.java` realiza a leitura e criação automática das filas
- Elimina a necessidade de configuração manual para a maioria dos casos

---

## 📂 Estrutura do Projeto

```
queuesim/
├── 📄 randoms.txt          # Números aleatórios gerados automaticamente
├── 📄 README.md           # Este arquivo
└── 📁 Tandem/
    ├── 🔧 Classes Auxiliares
    │   ├── escalonador.java
    │   ├── escalonador2.java
    │   ├── Evento.java
    │   ├── Evento2.java
    │   └── fila.java
    ├── 🎯 Simuladores Principais
    │   ├── simuladorTandem.java
    │   └── simuladorTandem2.java
    └── 📁 allProb/          # 🌟 Versão Principal
        ├── 🎯 vFinal.java           # Arquivo principal
        ├── 🔧 vModelProb.java       # Versão específica
        ├── 📖 lerModel.java         # Leitor de modelos
        └── 📋 Modelos YAML
            ├── model.yml
            ├── model1fila.yml
            ├── model2fila.yml
            └── modelprob.yml
```