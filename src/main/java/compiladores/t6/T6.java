package compiladores.t6;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;
import java.io.IOException;

/**
 * Classe principal do compilador de receitas.
 * Orquestra todas as fases da compilação para a gramática inferencial:
 * análise léxica, sintática, semântica e geração de código HTML.
 */
public class T6 {
    public static void main(String[] args) {
        // --- 1. Validação dos Argumentos de Entrada ---
        if (args.length < 2) {
            System.err.println("Uso: java -jar seuCompilador.jar <arquivo_receita.txt> <arquivo_saida.html>");
            System.err.println("Exemplo: java -jar ReceitaCompiler.jar receita.txt bolo.html");
            return;
        }

        String inputFile = args[0];
        String outputFile = args[1];

        try {
            // --- 2. Leitura e Análise Léxica ---
            System.out.println("Iniciando compilação de: " + inputFile);
            CharStream input = CharStreams.fromFileName(inputFile);
            ReceitaLexer lexer = new ReceitaLexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);

            // --- 3. Análise Sintática ---
            ReceitaParser parser = new ReceitaParser(tokens);
            parser.removeErrorListeners();
            parser.addErrorListener(new BaseErrorListener() {
                @Override
                public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
                    System.err.println("Erro de Sintaxe na linha " + line + ":" + charPositionInLine + " -> " + msg);
                }
            });

            ParseTree tree = parser.receita();

            if (parser.getNumberOfSyntaxErrors() > 0) {
                System.err.println("Compilação abortada devido a erros de sintaxe.");
                return;
            }
            System.out.println("Análise sintática concluída com sucesso.");

            // --- 4. Análise Semântica (usando o novo analisador inteligente) ---
            AnalisadorSemantico analisadorSemantico = new AnalisadorSemantico();
            analisadorSemantico.visit(tree);

            if (analisadorSemantico.getErrorCount() > 0) {
                System.err.println("Compilação abortada devido a erros semânticos.");
                return;
            }
            System.out.println("Análise semântica e extração de dados concluídas com sucesso.");

            // --- 5. Geração de Código HTML (usando o novo gerador) ---
            GeradorHtml geradorHTML = new GeradorHtml(outputFile, analisadorSemantico);
            // O novo gerador recebe a árvore para extrair o título e gerar os passos.
            geradorHTML.generate(tree);

            System.out.println("--------------------------------------------------");
            System.out.println("✅ Compilação concluída com sucesso!");
            System.out.println("Arquivo HTML gerado em: " + outputFile);
            System.out.println("Abra este arquivo no seu navegador para ver a receita.");

        } catch (IOException e) {
            System.err.println("Erro de I/O: Não foi possível ler o arquivo de entrada '" + inputFile + "'.");
        }
    }
}