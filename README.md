# Linguagem Para Escrever Receitas e Gerar um Código HTML

## TRABALHO 6 - CONSTRUÇÃO DE COMPILADORES

### Universidade Federal de São Carlos - Prof. Daniel Lucrédio

### Autores:

Guilherme dos Santos Wisniewski

Matheus dos Santos Sousa

Matheus Henrique Cassatti

## INTRODUÇÃO

O trabalho desenvolvido a seguir para a matéria de Compiladores visa a criação de uma linguagem de programação e um compilador para a mesma. A linguagem criada é utilizada para a escrita de receitas de cozinha. O compilador, então, interpreta o programa dado em formato `.txt` e o processa através de análise léxica, sintática e semântica, gerando uma página HTML formatada com a receita. O objetivo principal é fazer o usuário se preocupar apenas em declarar o modo de preparo e o compilador fica encarregado de identificar os ingredientes e utensílios, e depois declará-los no HTML juntamente com o próprio modo de preparo.

## GRAMÁTICA

A gramática da nossa linguagem está definida no arquivo `Receita.g4`.

O seguinte exemplo ilustra o uso da linguagem:

```java
Receita "Bolo de Fubá";

Adicione 3 "ovos", 2 xicaras de "açúcar", 1 xicara de "leite" em "tigela";
Bata na "Batedeira" por 5 minutos;
Adicione 2 xicaras de "fubá", 1 xicara de "farinha de trigo";
Adicione "fermento em pó";
Misture;
Adicione 1 unidade de "Goiabada";
Corte "Goiabada" em "cubinhos";
Asse no "Forno" por 40 minutos a 180 graus;
Fim;
```

Na seção de passos, pode-se utilizar comandos da linguagem para se escrever a receita. Esses comandos são definidos a seguir:

| Comando | Descrição |
| --- | --- |
| `Adicione <quantidade> <medida> "ingrediente" (em "recipiente")` | Adiciona um ou mais ingredientes, opcionalmente especificando a quantidade, medida e o recipiente. |
| `Misture ("item1", "item2", ...) (em "recipiente")` | Mistura itens listados, opcionalmente em um recipiente específico. |
| `Bata ("item1", "item2", ...) (em "recipiente") (por <tempo>)` | Bate itens listados, opcionalmente em um recipiente e por um tempo determinado. |
| `Cozinhe ("item1", "item2", ...) (em "recipiente") (por <tempo>)` | Cozinha itens listados, opcionalmente em um recipiente e por um tempo determinado. |
| `Corte <quantidade> <medida> "ingrediente" (em "formato")` | Corta um ingrediente, opcionalmente especificando a quantidade, medida e o formato do corte. |
| `Asse (em "dispositivo") (por <tempo>) (a <temperatura>)` | Assa a receita, opcionalmente especificando o dispositivo (ex: "Forno"), o tempo e a temperatura. |
| `Reserve ("item")` | Reserva um item, que pode ser um ingrediente ou uma parte da receita. |

## COMPILAR


Após fazer o build do projeto, para executar o programa, abra terminal no diretório raiz do projeto e use o comando:

`java -jar target/T6-1.0-jar-with-dependencies.jar in/<arquivo_receita.txt> out/<arquivo_saida.html>`

Exemplo: `java -jar target/T6-1.0-jar-with-dependencies.jar in/receita.txt out/bolo.html`

## APRESENTAÇÂO

Link para o vídeo de apresentação: [Clique aqui](https://drive.google.com/file/d/1Yy5KTRi8Em5KSvJuWqk7Rs1Zu5Bfe40R/view?usp=sharing)
