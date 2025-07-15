package compiladores.t6;

import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;

public class CustomLexerErrorListener extends BaseErrorListener {

    private int lexerErrorCount = 0;

    @Override
    public void syntaxError(Recognizer<?, ?> recognizer, Object offendingSymbol, int line, int charPositionInLine, String msg, RecognitionException e) {
        String translatedMsg = msg;

        if (msg.contains("token recognition error")) {
            int atIndex = msg.indexOf("at: '");
            if (atIndex != -1) {
                String offendingChar = msg.substring(atIndex + 5, msg.indexOf("'", atIndex + 5));
                translatedMsg = "Erro de reconhecimento de token: Caractere inválido ou inesperado '" + offendingChar + "'.";
            } else {
                translatedMsg = "Erro de reconhecimento de token: Caractere inválido ou inesperado.";
            }
        }

        System.err.println("Erro Léxico na linha " + line + ":" + charPositionInLine + " -> " + translatedMsg);
        lexerErrorCount++;
    }

    public int getLexerErrorCount() {
        return lexerErrorCount;
    }
}