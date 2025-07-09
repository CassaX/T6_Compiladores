grammar Receita;
// --- Tokens (Lexer Rules) ---
INICIO_RECEITA : 'Início';
FIM_RECEITA    : 'Fim';
REPETIR        : 'Repita';
MODO_PREPARO   : 'Modo de Preparo';

FORNO          : 'Forno';
BATEDEIRA      : 'Batedeira';
LIQUIDIFICADOR : 'Liquidificador';
GELADEIRA      : 'Geladeira';
PANELA         : 'Panela';

ACAO_ADICIONAR : 'Adicione';
ACAO_MISTURAR  : 'Misture';
ACAO_CORTAR    : 'Corte';
ACAO_BATER     : 'Bata';
ACAO_PENEIRAR  : 'Peneire';
ACAO_ASSAR     : 'Asse';
ACAO_COZINHAR  : 'Cozinhe';
ACAO_QUEBRAR   : 'Quebre';
ACAO_VERIFICAR : 'Verifique';

A              : 'A';
ATE            : 'ATE';
DE             : 'de';
EM             : 'em';   // NOVO TOKEN para "em"
NA             : 'na';   // NOVO TOKEN para "na"
NO             : 'no';   // NOVO TOKEN para "no"

VIRGULA        : ',';
DOIS_PONTOS    : ':';
PONTO_E_VIRGULA: ';';
ABRE_PARENTESES: '(';
FECHA_PARENTESES: ')';

NUMERO         : [0-9]+;
MEDIDA         : 'ml' | 'g' | 'kg' | 'litro' | 'xicaras' | 'colher de sopa' | 'colheres' | 'pitadas' | 'unidades';
TEMPO_UNIDADE  : 'minutos' | 'horas';
TEMP_UNIDADE   : 'graus' | 'C';

IDENTIFICADOR  : [a-zA-ZçÇãõéÉóÓàÀÜÜ_][a-zA-ZçÇãõéÉóÓàÀÜÜ0-9_]*;
TEXTO_LITERAL  : '"' (~["\r\n])* '"';

WS             : [ \t\r\n]+ -> skip;
COMENTARIO_LINHA : '//' ~[\r\n]* -> skip;

// --- Regras de Parser (Parser Rules) ---
receita : INICIO_RECEITA nome_receita DOIS_PONTOS
          secao_modo_preparo
          FIM_RECEITA PONTO_E_VIRGULA
        ;

nome_receita : TEXTO_LITERAL | IDENTIFICADOR;

secao_modo_preparo : (MODO_PREPARO DOIS_PONTOS)? lista_passos;

lista_passos : passo+;

passo : acao PONTO_E_VIRGULA
      | REPETIR NUMERO TEXTO_LITERAL PONTO_E_VIRGULA
      ;

// Regra 'acao' agora inclui as novas ações personalizadas
acao : acao_simples
      | acao_com_dispositivo
      | acao_com_ingredientes_geral // Substitui a antiga acao_com_ingredientes
      | acao_com_ate
      | acao_misturar_ate_condicao
      | acao_cortar // Nova ação específica para Corte
      | acao_misturar_destino // Nova ação para Misture com destino
      ;

// Regra que define as preposições de destino (A, na, no)
destino_recipiente : A | NA | NO; // NOVO

// acao_com_ingredientes_geral agora não inclui ACAO_CORTAR
acao_com_ingredientes_geral : (ACAO_ADICIONAR | ACAO_QUEBRAR | ACAO_PENEIRAR) lista_itens_com_quantidade (destino_recipiente IDENTIFICADOR)?; // MODIFICADO

lista_itens_com_quantidade : item_com_quantidade (VIRGULA item_com_quantidade)*;

item_com_quantidade : NUMERO MEDIDA DE? TEXTO_LITERAL
                    | NUMERO IDENTIFICADOR
                    | TEXTO_LITERAL
                    | IDENTIFICADOR
                    ;

// acao_simples agora permite um descritivo opcional (para "Misture suavemente")
acao_simples : ACAO_MISTURAR (IDENTIFICADOR | TEXTO_LITERAL)? ; // MODIFICADO

acao_com_dispositivo : (ACAO_ASSAR | ACAO_BATER | ACAO_COZINHAR) no_dispositivo;

no_dispositivo : FORNO
                | BATEDEIRA
                | LIQUIDIFICADOR
                | GELADEIRA
                | PANELA
                ;

acao_misturar_ate_condicao : ACAO_MISTURAR (IDENTIFICADOR | TEXTO_LITERAL)? ATE TEXTO_LITERAL;

acao_com_ate : ACAO_VERIFICAR TEXTO_LITERAL ATE (tempo | TEXTO_LITERAL);

tempo : NUMERO TEMPO_UNIDADE;

// --- Novas regras adicionadas para a entrada específica ---

// Nova regra para a ação de 'Corte' com 'em'
acao_cortar : ACAO_CORTAR TEXTO_LITERAL (EM TEXTO_LITERAL)? (A IDENTIFICADOR)?; // NOVO

// Nova regra para 'Misture na Massa' (ou similar)
acao_misturar_destino : ACAO_MISTURAR destino_recipiente IDENTIFICADOR; // NOVOE;