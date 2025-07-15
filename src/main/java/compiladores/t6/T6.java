package compiladores.t6;

import java.io.IOException;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.tree.ParseTree;


public class T6 {
    public static void main(String[] args) {
        //1. Validação dos Argumentos de Entrada
        if (args.length < 2) {
            System.err.println("Uso: java -jar seuCompilador.jar <arquivo_receita.txt> <arquivo_saida.html>");
            System.err.println("Exemplo: java -jar ReceitaCompiler.jar receita.txt bolo.html");
            return;
        }

        String inputFile = args[0];
        String outputFile = args[1];

        try {
            // 2.Análise Léxica
            System.out.println("Iniciando compilação de: " + inputFile);
            CharStream input = CharStreams.fromFileName(inputFile);

            ReceitaLexer lexer = new ReceitaLexer(input);
            lexer.removeErrorListeners(); 
            CustomLexerErrorListener lexerErrorListener = new CustomLexerErrorListener();
            lexer.addErrorListener(lexerErrorListener); 

            CommonTokenStream tokens = new CommonTokenStream(lexer);

            tokens.fill();

            if (lexerErrorListener.getLexerErrorCount() > 0) {
                System.err.println("Compilação abortada devido a erros léxicos.");
                return;
            }
            System.out.println("Análise léxica concluída com sucesso.");


            //3. Análise Sintática
            ReceitaParser parser = new ReceitaParser(tokens);
            parser.removeErrorListeners();
            parser.addErrorListener(new BaseErrorListener() {
                @Override
                public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
                    String translatedMsg = msg;
                    if (msg.contains("no viable alternative at input")) {
                        int atIndex = msg.indexOf("at input '");
                        if (atIndex != -1) {
                            String offendingText = msg.substring(atIndex + 10, msg.lastIndexOf("'"));
                            translatedMsg = "Alternativa inviável na entrada: '" + offendingText + "'. Verifique a sintaxe ou se a medida/identificador está correto.";
                        } else {
                            translatedMsg = "Alternativa inviável na entrada. Verifique a sintaxe.";
                        }
                    }
                    else if (msg.contains("mismatched input")) {
                        String found = msg.substring(msg.indexOf("'") + 1, msg.indexOf("'", msg.indexOf("'") + 1));
                        String expected = "";
                        if (msg.contains("expecting ")) {
                           expected = msg.substring(msg.indexOf("expecting ") + 10).replace("'", "");
                           translatedMsg = "Entrada inesperada: Encontrado '" + found + "', mas esperava-se '" + expected + "'.";
                        } else {
                           translatedMsg = "Entrada inesperada: Encontrado '" + found + "'.";
                        }
                    }
                    else if (msg.contains("missing ") && msg.contains(" at ")) {
                        String missingPart = msg.substring(msg.indexOf("missing ") + 8, msg.indexOf(" at "));
                        String atPart = msg.substring(msg.indexOf(" at ") + 4);
                        translatedMsg = "Símbolo esperado: Faltando " + missingPart + " em " + atPart + ".";
                    }
                    else if (msg.contains("extraneous input")) {
                        String extraneous = msg.substring(msg.indexOf("'") + 1, msg.indexOf("'", msg.indexOf("'") + 1));
                        translatedMsg = "Entrada redundante: '" + extraneous + "'. Este token não era esperado aqui.";
                    }

                    System.err.println("Erro de Sintaxe na linha " + line + ":" + charPositionInLine + " -> " + translatedMsg);
                }
            });

            ParseTree tree = parser.receita();

            if (parser.getNumberOfSyntaxErrors() > 0) {
                System.err.println("Compilação abortada devido a erros de sintaxe.");
                return;
            }
            System.out.println("Análise sintática concluída com sucesso.");

            //4. Análise Semântica
            AnalisadorSemantico analisadorSemantico = new AnalisadorSemantico();
            analisadorSemantico.visit(tree);

            if (analisadorSemantico.getErrorCount() > 0) {
                System.err.println("Compilação abortada devido a erros semânticos.");
                return;
            }
            System.out.println("Análise semântica e extração de dados concluídas com sucesso.");

            // 5. Geração de Código HTML
            GeradorHtml geradorHTML = new GeradorHtml(outputFile, analisadorSemantico);
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