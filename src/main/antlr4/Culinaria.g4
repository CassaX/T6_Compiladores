grammar Culinaria;

// Tokens

// Palavras-chave
INICIO_RECEITA : 'Início';
FIM_RECEITA    : 'Fim';
REPETIR        : 'Repita';
INGREDIENTES   : 'Ingredientes';
MODO_PREPARO   : 'Modo de Preparo';

// Dispositivos
FORNO          : 'Forno';
BATEDEIRA      : 'Batedeira';
LIQUIDIFICADOR : 'Liquidificador';
GELADEIRA      : 'Geladeira';
PANELA         : 'Panela';

// Acoes
ACAO_ADICIONAR : 'Adicione';
ACAO_MISTURAR  : 'Misture';
ACAO_CORTAR    : 'Corte';
ACAO_BATER     : 'Bata';
ACAO_PENEIRAR  : 'Peneire';
ACAO_ASSAR     : 'Asse';
ACAO_COZINHAR  : 'Cozinhe';
ACAO_QUEBRAR   : 'Quebre';
ACAO_VERIFICAR : 'Verifique';

// Conectores e Operadores
A              : 'A';
ATE            : 'ATE';

// Simbolos
VIRGULA        : ',';
DOIS_PONTOS    : ':';
PONTO_E_VIRGULA: ';';
ABRE_PARENTESES: '(';
FECHA_PARENTESES: ')';

// Tipos de dados e literais
NUMERO         : [0-9]+;
MEDIDA         : 'ml' | 'g' | 'kg' | 'xicaras' | 'colheres' | 'pitadas' | 'unidades';
TEMPO_UNIDADE  : 'minutos' | 'horas';
TEMP_UNIDADE   : 'graus' | 'C';

// Identificadores e Texto
IDENTIFICADOR  : [a-zA-ZçÇãõéÉóÓàÀüÜ_][a-zA-ZçÇãõéÉóÓàÀüÜ0-9_]*;
TEXTO_LITERAL  : '"' (~["\r\n])* '"';

//espaços em branco e quebras de linha
WS             : [ \t\r\n]+ -> skip;

//comentários de linha
COMENTARIO_LINHA : '//' ~[\r\n]* -> skip;

// Regras de Parser

// Regra inicial 
receita : INICIO_RECEITA nome_receita DOIS_PONTOS
          secao_ingredientes
          secao_modo_preparo
          FIM_RECEITA PONTO_E_VIRGULA
        ;

nome_receita : TEXTO_LITERAL | IDENTIFICADOR;

secao_ingredientes : INGREDIENTES DOIS_PONTOS lista_ingredientes;

lista_ingredientes : item_ingrediente (PONTO_E_VIRGULA item_ingrediente)* PONTO_E_VIRGULA?;

item_ingrediente : quantidade_ingrediente? IDENTIFICADOR;

quantidade_ingrediente : NUMERO MEDIDA;

secao_modo_preparo : MODO_PREPARO DOIS_PONTOS lista_passos;

lista_passos : passo+;

passo : acao PONTO_E_VIRGULA
      | REPETIR NUMERO TEXTO_LITERAL PONTO_E_VIRGULA
      ;

acao : acao_simples
     | acao_com_dispositivo
     | acao_com_variavel
     | acao_com_ate
     | acao_adicionar_a_destino
     | acao_misturar_ate_condicao
     ;

acao_simples : ACAO_MISTURAR
             | ACAO_PENEIRAR
             | ACAO_CORTAR
             | ACAO_QUEBRAR
             ;

acao_com_dispositivo : (ACAO_ASSAR | ACAO_BATER | ACAO_COZINHAR) no_dispositivo;

no_dispositivo : FORNO
               | BATEDEIRA
               | LIQUIDIFICADOR
               | GELADEIRA
               | PANELA
               ;

acao_adicionar_a_destino : ACAO_ADICIONAR lista_identificadores A IDENTIFICADOR; // Adicione farinha, ovos A massa

acao_misturar_ate_condicao : ACAO_MISTURAR (IDENTIFICADOR | TEXTO_LITERAL)? ATE TEXTO_LITERAL; // Misture (bem) ATE "homogeneizar"

acao_com_variavel : (ACAO_CORTAR | ACAO_QUEBRAR | ACAO_ADICIONAR) lista_identificadores; // Corte cebola, tomate; Adicione agua;

acao_com_ate : ACAO_VERIFICAR TEXTO_LITERAL ATE (tempo | TEXTO_LITERAL);

lista_identificadores : IDENTIFICADOR (VIRGULA IDENTIFICADOR)*;

tempo : NUMERO TEMPO_UNIDADE;