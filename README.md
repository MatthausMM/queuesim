# queuesim

## Instruções

Para rodar a simulação, utilize o arquivo principal `simuladorTandem.java`.  
Esse arquivo executa toda a lógica do sistema tandem de filas, utilizando as classes auxiliares implementadas separadamente para organização e clareza do código.

### Sobre os números aleatórios para teste

Gravamos os valores gerados pelo nextRandom() no arquivo `randoms.txt` durante a execução do programa.  
Assim, podemos colar no model.yml e testar no simulador disponibilizado no módulo 3.

### Estrutura do projeto

- `simuladorTandem.java`: arquivo principal da simulação.
- Classes auxiliares (`fila`, `Evento`, `escalonador`) implementadas separadamente para modularidade.
- `randoms.txt`: arquivo gerado automaticamente com todos os números aleatórios utilizados na simulação.