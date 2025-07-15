package compiladores.t6;

import java.util.Map;
import java.util.Set;

import org.antlr.v4.runtime.Token;

public class AnalisadorSemantico extends ReceitaBaseVisitor<Void> {

    private final TabelaDeSimbolos tabela;
    private int errorCount = 0;

    public AnalisadorSemantico() {
        this.tabela = new TabelaDeSimbolos();
    }

    public Map<String, UtilitariosSemanticos.IngredienteInfo> getIngredientes() { return tabela.getIngredientes(); }
    public Set<String> getUtensilios() { return tabela.getUtensilios(); }
    public int getErrorCount() { return errorCount; }

    private void addError(String message, Token token) {
        System.err.println("Erro Semântico na linha " + token.getLine() + ":" + token.getCharPositionInLine() + " - " + message);
        errorCount++;
    }

    @Override
    public Void visitAcao_adicionar(ReceitaParser.Acao_adicionarContext ctx) {
        if (ctx.EM() != null) {
            tabela.adicionarUtensilio(ctx.TEXTO_LITERAL().getText().replace("\"", ""));
        }

        for (ReceitaParser.Item_declaracaoContext item : ctx.item_declaracao()) {
            String nome = item.TEXTO_LITERAL().getText().replace("\"", "");
            double quantidade = 1.0;
            String unidade = "unidade";

            if (item.NUMERO() != null) {
                quantidade = Double.parseDouble(item.NUMERO().getText());
            }
            if (item.MEDIDA() != null) {
                unidade = item.MEDIDA().getText();
            }
            tabela.adicionarIngrediente(nome, quantidade, unidade);
        }
        return null;
    }

    private void validarUsoDeIngredientes(ReceitaParser.Lista_itens_usoContext lista) {
        if (lista == null) return;
        for (var item : lista.TEXTO_LITERAL()) {
            String nomeIngrediente = item.getText().replace("\"", "");
            if (!tabela.ingredienteExiste(nomeIngrediente)) {
                addError("Tentativa de usar o ingrediente '" + nomeIngrediente + "' antes de ser adicionado.", item.getSymbol());
            }
        }
    }

    @Override
    public Void visitAcao_misturar(ReceitaParser.Acao_misturarContext ctx) {
        validarUsoDeIngredientes(ctx.lista_itens_uso());
        if (ctx.EM() != null) {
            tabela.adicionarUtensilio(ctx.TEXTO_LITERAL().getText().replace("\"", ""));
        }
        return null;
    }

    @Override
    public Void visitAcao_bater(ReceitaParser.Acao_baterContext ctx) {
        validarUsoDeIngredientes(ctx.lista_itens_uso());
        if (ctx.EM() != null) {
            tabela.adicionarUtensilio(ctx.TEXTO_LITERAL().getText().replace("\"", ""));
        }
        return null;
    }

    @Override
    public Void visitAcao_cozinhar(ReceitaParser.Acao_cozinharContext ctx) {
        validarUsoDeIngredientes(ctx.lista_itens_uso());
        if (ctx.EM() != null) {
            tabela.adicionarUtensilio(ctx.TEXTO_LITERAL().getText().replace("\"", ""));
        }
        return null;
    }

    @Override
    public Void visitAcao_cortar(ReceitaParser.Acao_cortarContext ctx) {
        ReceitaParser.Item_declaracaoContext itemCtx = ctx.item_declaracao();
        String nomeIngrediente = itemCtx.TEXTO_LITERAL().getText().replace("\"", "");

        if (!tabela.ingredienteExiste(nomeIngrediente)) {
            addError("Tentativa de cortar o ingrediente '" + nomeIngrediente + "' antes de ser adicionado.", itemCtx.TEXTO_LITERAL().getSymbol());
        }
        return null;
    }

    @Override
    public Void visitAcao_assar(ReceitaParser.Acao_assarContext ctx) {
        if (ctx.EM() != null) {
            tabela.adicionarUtensilio(ctx.TEXTO_LITERAL().getText().replace("\"", ""));
        } else {
            tabela.adicionarUtensilio("Forno");
        }
        return null;
    }

    @Override
    public Void visitAcao_reservar(ReceitaParser.Acao_reservarContext ctx) {
        return null;
    }
}