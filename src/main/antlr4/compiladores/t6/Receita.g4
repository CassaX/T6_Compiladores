grammar Receita;

// Tokens 
TITULO_RECEITA   : 'Receita';
TAG_FIM          : 'Fim';

ACAO_ADICIONAR : 'Adicione';
ACAO_MISTURAR  : 'Misture';
ACAO_CORTAR    : 'Corte';
ACAO_ASSAR     : 'Asse';
ACAO_BATER     : 'Bata';
ACAO_COZINHAR  : 'Cozinhe';
ACAO_RESERVAR  : 'Reserve';

EM             : 'em' | 'no' | 'na';
POR            : 'por';
A              : 'a';

VIRGULA        : ',';
PONTO_E_VIRGULA: ';';

NUMERO         : [0-9]+ ('.' [0-9]+)?;
MEDIDA         : 'ml' | 'g' | 'kg' | 'litros' | 'xicaras' | 'xicara' | 'colher' | 'colheres' | 'pitada' | 'unidades' | 'unidade' | 'latas' | 'lata';
UNIDADE_TEMPO  : 'minutos' | 'horas' | 'segundos';
UNIDADE_TEMP   : 'graus';

IDENTIFICADOR  : [a-zA-ZçÇãõáéíóúÁÉÍÓÚâêôÂÊÔàÀ_][a-zA-Z0-9çÇãõáéíóúÁÉÍÓÚâêôÂÊÔàÀ_]*;
TEXTO_LITERAL  : '"' (~["\r\n])*? '"';

WS             : [ \t\r\n]+ -> skip;
COMENTARIO_LINHA : '//' ~[\r\n]* -> skip;

// Regras de Parser 
receita: cabecalho passo+ TAG_FIM;
cabecalho: TITULO_RECEITA TEXTO_LITERAL PONTO_E_VIRGULA;
passo: acao PONTO_E_VIRGULA;

acao:
    acao_adicionar | acao_misturar | acao_assar | acao_bater |
    acao_cozinhar | acao_cortar | acao_reservar | acao_generica;

// Regras de ação 
acao_adicionar: ACAO_ADICIONAR item_declaracao (VIRGULA item_declaracao)* (EM TEXTO_LITERAL)?;
acao_misturar:  ACAO_MISTURAR (lista_itens_uso)? (EM TEXTO_LITERAL)?;
acao_bater:     ACAO_BATER (lista_itens_uso)? (EM TEXTO_LITERAL)? (POR tempo)?;
acao_cozinhar:  ACAO_COZINHAR (lista_itens_uso)? (EM TEXTO_LITERAL)? (POR tempo)?;
acao_assar:     ACAO_ASSAR (EM TEXTO_LITERAL)? (POR tempo)? (A temperatura)?;
acao_cortar:    ACAO_CORTAR item_declaracao (EM TEXTO_LITERAL)?;
acao_reservar:  ACAO_RESERVAR (TEXTO_LITERAL | IDENTIFICADOR);
acao_generica:  ACAO_MISTURAR | ACAO_BATER;
item_declaracao: (NUMERO MEDIDA 'de' | NUMERO)? TEXTO_LITERAL;

// Elementos 
lista_itens_uso: (TEXTO_LITERAL | IDENTIFICADOR) (VIRGULA (TEXTO_LITERAL | IDENTIFICADOR))*;
tempo:           NUMERO UNIDADE_TEMPO;
temperatura:     NUMERO UNIDADE_TEMP;
