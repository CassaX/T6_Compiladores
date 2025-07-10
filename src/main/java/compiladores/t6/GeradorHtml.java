package compiladores.t6;

import org.antlr.v4.runtime.tree.ParseTree;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.stream.Collectors;

public class GeradorHtml extends ReceitaBaseVisitor<Void> {

    private final PrintWriter writer;
    private final AnalisadorSemantico semanticAnalyzer;
    private final StringBuilder modoPreparoHtml;

    public GeradorHtml(String outputFilePath, AnalisadorSemantico analyzer) throws IOException {
        if (!outputFilePath.toLowerCase().endsWith(".html")) {
            outputFilePath += ".html";
        }
        this.writer = new PrintWriter(new FileWriter(outputFilePath, false));
        this.semanticAnalyzer = analyzer;
        this.modoPreparoHtml = new StringBuilder();
    }

    public void generate(ParseTree tree) {
        visit(tree);

        String nomeReceita = "Receita";
        try {
            ReceitaParser.ReceitaContext receitaCtx = (ReceitaParser.ReceitaContext) tree;
            nomeReceita = receitaCtx.cabecalho().TEXTO_LITERAL().getText().replace("\"", "");
        } catch (Exception e) { /* Usa o nome padrão */ }

        writer.println("<!DOCTYPE html><html lang=\"pt-BR\"><head><meta charset=\"UTF-8\"><title>" + nomeReceita + "</title>");
        writer.println("<style>@import url('https://fonts.googleapis.com/css2?family=Roboto:wght@400;700&display=swap');");
        writer.println("body { font-family: 'Roboto', sans-serif; line-height: 1.6; margin: 0; padding: 20px; background-color: #f8f9fa; color: #343a40; }");
        writer.println(".container { max-width: 800px; margin: auto; background: #fff; padding: 40px; border-radius: 8px; box-shadow: 0 2px 15px rgba(0,0,0,0.1); }");
        writer.println("h1 { text-align: center; color: #d63384; margin-bottom: 20px; } h2 { color: #0d6efd; border-bottom: 2px solid #dee2e6; padding-bottom: 10px; margin-top: 40px; }");
        writer.println("ul, ol { padding-left: 25px; } li { margin-bottom: 10px; } .utensil { text-transform: capitalize; }</style>");
        writer.println("</head><body><div class=\"container\"><h1>" + nomeReceita + "</h1>");

        writer.println("<h2>Ingredientes</h2><ul>");
        for (UtilitariosSemanticos.IngredienteInfo ingrediente : semanticAnalyzer.getIngredientes().values()) {
            writer.println("<li>" + ingrediente.toString() + "</li>");
        }
        writer.println("</ul>");

        writer.println("<h2>Utensílios Necessários</h2><ul>");
        for (String utensilio : semanticAnalyzer.getUtensilios()) {
            writer.println("<li class=\"utensil\">" + utensilio + "</li>");
        }
        writer.println("</ul>");

        writer.println("<h2>Modo de Preparo</h2><ol>");
        writer.print(modoPreparoHtml.toString());
        writer.println("</ol></div></body></html>");
        writer.close();
    }

    private String formatarItemDeclaracao(ReceitaParser.Item_declaracaoContext ctx) {
        StringBuilder sb = new StringBuilder();
        if (ctx.NUMERO() != null) {
            sb.append(ctx.NUMERO().getText()).append(" ");
        }
        if (ctx.MEDIDA() != null) {
            sb.append(ctx.MEDIDA().getText()).append(" de ");
        }
        // MUDANÇA: Remove as aspas do texto do ingrediente
        sb.append(ctx.TEXTO_LITERAL().getText().replace("\"", ""));
        return sb.toString();
    }

    @Override
    public Void visitAcao_adicionar(ReceitaParser.Acao_adicionarContext ctx) {
        StringBuilder sb = new StringBuilder();
        sb.append(capitalize(ctx.ACAO_ADICIONAR().getText())).append(" ");

        String itens = ctx.item_declaracao().stream()
                .map(this::formatarItemDeclaracao)
                .collect(Collectors.joining(", "));
        sb.append(itens);

        if (ctx.EM() != null) {
            // MUDANÇA: Remove as aspas do texto do recipiente
            sb.append(" em ").append(ctx.TEXTO_LITERAL().getText().replace("\"", ""));
        }
        sb.append(".");
        modoPreparoHtml.append("<li>").append(sb.toString()).append("</li>\n");
        return null;
    }

    @Override
    public Void visitAcao_misturar(ReceitaParser.Acao_misturarContext ctx) {
        StringBuilder sb = new StringBuilder();
        sb.append(capitalize(ctx.ACAO_MISTURAR().getText()));
        if (ctx.lista_itens_uso() != null) {
            sb.append(" ").append(ctx.lista_itens_uso().getText().replace("\"", ""));
        }
        if (ctx.EM() != null) {
            // MUDANÇA: Remove as aspas do texto do recipiente
            sb.append(" em ").append(ctx.TEXTO_LITERAL().getText().replace("\"", ""));
        }
        sb.append(".");
        modoPreparoHtml.append("<li>").append(sb.toString()).append("</li>\n");
        return null;
    }

    @Override
    public Void visitAcao_cortar(ReceitaParser.Acao_cortarContext ctx) {
        String ingredienteParte = formatarItemDeclaracao(ctx.item_declaracao());
        StringBuilder sb = new StringBuilder();
        sb.append(capitalize(ctx.ACAO_CORTAR().getText())).append(" ").append(ingredienteParte);

        if (ctx.EM() != null) {
            // MUDANÇA: Remove as aspas do texto do formato (ex: "cubinhos")
            sb.append(" ").append(ctx.EM().getText()).append(" ").append(ctx.TEXTO_LITERAL().getText().replace("\"", ""));
        }
        sb.append(".");
        modoPreparoHtml.append("<li>").append(sb.toString()).append("</li>\n");
        return null;
    }

    @Override
    public Void visitAcao_assar(ReceitaParser.Acao_assarContext ctx) {
        StringBuilder sb = new StringBuilder();
        sb.append(capitalize(ctx.ACAO_ASSAR().getText()));
        if (ctx.EM() != null) {
            // MUDANÇA: Remove as aspas do texto do dispositivo (ex: "Forno")
            sb.append(" no ").append(ctx.TEXTO_LITERAL().getText().replace("\"", ""));
        }
        if (ctx.tempo() != null) {
            sb.append(" por ").append(ctx.tempo().NUMERO().getText()).append(" ").append(ctx.tempo().UNIDADE_TEMPO().getText());
        }
        if (ctx.temperatura() != null) {
            sb.append(" a ").append(ctx.temperatura().NUMERO().getText()).append(" ").append(ctx.temperatura().UNIDADE_TEMP().getText());
        }
        sb.append(".");
        modoPreparoHtml.append("<li>").append(sb.toString()).append("</li>\n");
        return null;
    }

    @Override
    public Void visitAcao_reservar(ReceitaParser.Acao_reservarContext ctx) {
        // MUDANÇA: Remove as aspas se o item reservado for um texto literal
        String item = ctx.getChild(1).getText().replace("\"", "");
        modoPreparoHtml.append("<li>").append(capitalize(ctx.ACAO_RESERVAR().getText())).append(" ").append(item).append(".</li>\n");
        return null;
    }

    @Override
    public Void visitAcao_generica(ReceitaParser.Acao_genericaContext ctx) {
        modoPreparoHtml.append("<li>").append(capitalize(ctx.getText())).append(".</li>\n");
        return null;
    }

    private String capitalize(String str) {
        if (str == null || str.isEmpty()) return str;
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }
}