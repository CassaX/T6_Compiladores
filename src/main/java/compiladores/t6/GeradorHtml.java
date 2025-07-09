package compiladores.t6; // Ajuste o pacote conforme seu projeto

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.Set;
import org.antlr.v4.runtime.tree.TerminalNode; // Importe TerminalNode

public class GeradorHtml extends ReceitaBaseVisitor<Void> {

    private PrintWriter writer;
    private AnalisadorSemantico semanticAnalyzer;
    private int passoCounter = 0;

    public GeradorHtml(String outputFilePath, AnalisadorSemantico analyzer) throws IOException {
        if (!outputFilePath.toLowerCase().endsWith(".html")) {
            outputFilePath += ".html";
        }
        this.writer = new PrintWriter(new FileWriter(outputFilePath));
        this.semanticAnalyzer = analyzer;
    }

    public void generate(ReceitaParser.ReceitaContext ctx) {
        String nomeReceita = ctx.nome_receita().getText().replace("\"", "");

        writer.println("<!DOCTYPE html>");
        writer.println("<html lang=\"pt-BR\">");
        writer.println("<head>");
        writer.println("    <meta charset=\"UTF-8\">");
        writer.println("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">");
        writer.println("    <title>" + nomeReceita + "</title>");
        writer.println("    <style>");
        writer.println("        body { font-family: Arial, sans-serif; line-height: 1.6; margin: 20px; background-color: #f4f4f4; color: #333; }");
        writer.println("        .container { max-width: 800px; margin: auto; background: #fff; padding: 30px; border-radius: 8px; box-shadow: 0 0 10px rgba(0,0,0,0.1); }");
        writer.println("        h1, h2 { color: #5cb85c; border-bottom: 2px solid #eee; padding-bottom: 10px; margin-bottom: 20px; }");
        writer.println("        ul, ol { list-style-type: none; padding: 0; }");
        writer.println("        ul li, ol li { margin-bottom: 8px; padding-left: 25px; position: relative; }");
        writer.println("        ul li::before { content: '•'; color: #5cb85c; font-weight: bold; display: inline-block; width: 1em; margin-left: -1em; }");
        writer.println("        ol li { counter-increment: step-counter; }");
        writer.println("        ol li::before { content: counter(step-counter) '. '; color: #5cb85c; font-weight: bold; display: inline-block; width: 1em; margin-left: -1.5em; text-align: right; }");
        writer.println("    </style>");
        writer.println("</head>");
        writer.println("<body>");
        writer.println("    <div class=\"container\">");
        writer.println("        <h1>" + nomeReceita + "</h1>");

        writer.println("        <h2>Ingredientes</h2>");
        writer.println("        <ul id=\"ingredientes-list\">");
        for (Map.Entry<String, UtilitariosSemanticos.IngredienteInfo> entry : semanticAnalyzer.getIngredientesDeclarados().entrySet()) {
            writer.println(String.format("            <li>%s %s</li>", entry.getValue().toString(), entry.getKey()));
        }
        writer.println("        </ul>");

        writer.println("        <h2>Utensílios Necessários</h2>");
        writer.println("        <ul id=\"utensilios-list\">");
        for (String utensilio : semanticAnalyzer.getUtensiliosNecessarios()) {
            writer.println(String.format("            <li>%s</li>", capitalize(utensilio)));
        }
        writer.println("        </ul>");

        writer.println("        <h2>Modo de Preparo</h2>");
        writer.println("        <ol id=\"modo-preparo-list\">");
        passoCounter = 0;
        this.visit(ctx);
        writer.println("        </ol>");

        writer.println("    </div>");
        writer.println("</body>");
        writer.println("</html>");
        writer.close();
    }

    @Override
    public Void visitPasso(ReceitaParser.PassoContext ctx) {
        passoCounter++;
        if (ctx.REPETIR() != null) {
            int numRepeticoes = Integer.parseInt(ctx.NUMERO().getText());
            String acaoRepetida = ctx.TEXTO_LITERAL().getText().replace("\"", "");
            writer.println(String.format("            <li>Repita %d vezes: \"%s\".</li>", numRepeticoes, acaoRepetida));
        } else {
            return super.visitPasso(ctx);
        }
        return null;
    }

    @Override
    public Void visitAcao_com_ingredientes(ReceitaParser.Acao_com_ingredientesContext ctx) {
        String acao = ctx.getChild(0).getText();
        StringBuilder ingredientesStr = new StringBuilder();
        for (ReceitaParser.Item_com_quantidadeContext itemCtx : ctx.lista_itens_com_quantidade().item_com_quantidade()) {
            String nome = "";
            if (itemCtx.TEXTO_LITERAL() != null) nome = itemCtx.TEXTO_LITERAL().getText().replace("\"", "");
            else if (itemCtx.IDENTIFICADOR() != null) nome = itemCtx.IDENTIFICADOR().getText();

            ingredientesStr.append(nome).append(", ");
        }
        if (ingredientesStr.length() > 2) {
            ingredientesStr.setLength(ingredientesStr.length() - 2);
        }

        String destino = "";
        if (ctx.IDENTIFICADOR() != null) {
            destino = " à " + ctx.IDENTIFICADOR().getText();
        }

        writer.println(String.format("            <li>%s %s%s.</li>", capitalize(acao), ingredientesStr.toString(), destino));
        return null;
    }

    @Override
    public Void visitAcao_misturar_ate_condicao(ReceitaParser.Acao_misturar_ate_condicaoContext ctx) {
        // Acessa o segundo TEXTO_LITERAL na regra, que é a condição
        String condicao = ctx.TEXTO_LITERAL(1).getText().replace("\"", ""); // Índice 1 para o segundo TEXTO_LITERAL

        String acaoComplemento = "";
        if (ctx.IDENTIFICADOR() != null) {
            acaoComplemento = ctx.IDENTIFICADOR().getText() + " ";
        } else if (ctx.TEXTO_LITERAL().size() > 1 && ctx.TEXTO_LITERAL(0) != null) { // Verifica se há pelo menos 2 TEXTO_LITERALs e se o primeiro não é nulo
            acaoComplemento = ctx.TEXTO_LITERAL(0).getText().replace("\"", "") + " "; // Índice 0 para o primeiro TEXTO_LITERAL opcional
        }

        writer.println(String.format("            <li>%s %s até %s.</li>", capitalize(ctx.ACAO_MISTURAR().getText()), acaoComplemento, condicao));
        return null;
    }

    @Override
    public Void visitAcao_com_dispositivo(ReceitaParser.Acao_com_dispositivoContext ctx) {
        String acao = ctx.getChild(0).getText();
        String dispositivo = ctx.no_dispositivo().getText();
        writer.println(String.format("            <li>%s no/na %s.</li>", capitalize(acao), capitalize(dispositivo)));
        return null;
    }

    @Override
    public Void visitAcao_com_ate(ReceitaParser.Acao_com_ateContext ctx) {
        // Assume que o primeiro TEXTO_LITERAL é a verificação
        String verificacao = ctx.TEXTO_LITERAL(0).getText().replace("\"", "");
        String condicao = "";

        if (ctx.tempo() != null) {
            condicao = ctx.tempo().NUMERO().getText() + " " + ctx.tempo().TEMPO_UNIDADE().getText();
        } else if (ctx.TEXTO_LITERAL().size() > 1 && ctx.TEXTO_LITERAL(1) != null) { // Se há um segundo TEXTO_LITERAL, use-o
            condicao = ctx.TEXTO_LITERAL(1).getText().replace("\"", "");
        }
        writer.println(String.format("            <li>Verifique: \"%s\" até: \"%s\".</li>", verificacao, condicao));
        return null;
    }

    @Override
    public Void visitAcao_simples(ReceitaParser.Acao_simplesContext ctx) {
        writer.println(String.format("            <li>%s.</li>", capitalize(ctx.getText())));
        return null;
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }
}