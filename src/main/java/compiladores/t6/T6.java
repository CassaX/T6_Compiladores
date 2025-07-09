package compiladores.t6; // Certifique-se de que o pacote está correto

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.ParseTree;

import java.io.IOException;

public class T6 { // ou 'Principal' se você usa esse nome
    public static void main(String[] args) throws IOException {
        if (args.length < 2) { // Agora esperamos pelo menos 2 argumentos
            System.err.println("Uso: java -jar seuCompilador.jar <arquivo_receita.txt> <arquivo_saida.html>");
            return;
        }

        String inputFile = args[0];
        String outputFile = args[1]; // O segundo argumento será o arquivo de saída

        CharStream input = CharStreams.fromFileName(inputFile);

        // 1. Análise Léxica
        ReceitaLexer lexer = new ReceitaLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        // 2. Análise Sintática
        ReceitaParser parser = new ReceitaParser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(new BaseErrorListener() {
            @Override
            public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
                System.err.println("Erro de Sintaxe na linha " + line + ":" + charPositionInLine + " - " + msg);
            }
        });

        ParseTree tree = parser.receita();

        if (parser.getNumberOfSyntaxErrors() > 0) {
            System.err.println("Compilação abortada devido a erros de sintaxe.");
            return;
        }

        // 3. Análise Semântica
        AnalisadorSemantico analisadorSemantico = new AnalisadorSemantico();
        analisadorSemantico.visit(tree);

        if (analisadorSemantico.getErrorCount() > 0) {
            System.err.println("Compilação abortada devido a erros semânticos.");
            return;
        }

        // 4. Geração de Código HTML
        // Agora 'outputFile' já contém o caminho completo do arquivo de saída
        GeradorHtml geradorHTML = new GeradorHtml(outputFile, analisadorSemantico);
        geradorHTML.generate((ReceitaParser.ReceitaContext) tree);

        System.out.println("Compilação concluída! Arquivo HTML gerado em: " + outputFile);
        System.out.println("Abra '" + outputFile + "' no seu navegador para ver a receita.");
    }
}