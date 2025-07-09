package compiladores.t6;

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

import java.util.Map;
import java.util.Set;

public class AnalisadorSemantico extends ReceitaBaseVisitor<Void> {

    private TabelaDeSimbolos tabelaDeSimbolos = new TabelaDeSimbolos();
    private int errorCount = 0;

    public Map<String, UtilitariosSemanticos.IngredienteInfo> getIngredientesDeclarados() {
        return tabelaDeSimbolos.getIngredientes();
    }

    public Set<String> getUtensiliosNecessarios() {
        return tabelaDeSimbolos.getUtensilios();
    }

    public int getErrorCount() {
        return errorCount;
    }

    private void addError(String message, org.antlr.v4.runtime.Token token) {
        System.err.println("Erro Semântico na linha " + token.getLine() + ":" + token.getCharPositionInLine() + " - " + message);
        errorCount++;
    }

    @Override
    public Void visitItem_com_quantidade(ReceitaParser.Item_com_quantidadeContext ctx) {
        String nomeIngrediente = null;
        double quantidade = 0.0;
        String unidade = "";

        if (ctx.NUMERO() != null) {
            quantidade = Double.parseDouble(ctx.NUMERO().getText());
            if (ctx.MEDIDA() != null) {
                unidade = ctx.MEDIDA().getText();
            } else if (ctx.IDENTIFICADOR() != null && ctx.IDENTIFICADOR().getText().equalsIgnoreCase("unidades")) {
                unidade = "unidades";
            }
        }

        if (ctx.TEXTO_LITERAL() != null) {
            nomeIngrediente = ctx.TEXTO_LITERAL().getText().replace("\"", "");
        } else if (ctx.IDENTIFICADOR() != null) {
            nomeIngrediente = ctx.IDENTIFICADOR().getText();
        }

        if (nomeIngrediente != null && !nomeIngrediente.isEmpty()) {
            if (quantidade == 0.0 && unidade.isEmpty()) {
                quantidade = 1.0;
                unidade = "unidade";
            }
            tabelaDeSimbolos.adicionarOuAtualizarIngrediente(nomeIngrediente, quantidade, unidade);
        } else {
             addError("Nome de ingrediente inválido ou faltando.", ctx.getStart());
        }

        return super.visitItem_com_quantidade(ctx);
    }

    @Override
    public Void visitNo_dispositivo(ReceitaParser.No_dispositivoContext ctx) {
        tabelaDeSimbolos.adicionarUtensilio(ctx.getText());
        return super.visitNo_dispositivo(ctx);
    }

    @Override
    public Void visitAcao_com_ingredientes(ReceitaParser.Acao_com_ingredientesContext ctx) {
        if (ctx.IDENTIFICADOR() != null) {
            String recipiente = ctx.IDENTIFICADOR().getText();
            tabelaDeSimbolos.adicionarUtensilio(recipiente);

            if (!tabelaDeSimbolos.isUtensilioConhecido(recipiente)) {
                 addError("Recipiente '" + recipiente + "' não reconhecido como um utensílio válido.", ctx.IDENTIFICADOR().getSymbol());
            }
        }
        return super.visitAcao_com_ingredientes(ctx);
    }

    @Override
    public Void visitAcao_com_dispositivo(ReceitaParser.Acao_com_dispositivoContext ctx) {
        String acao = ctx.getChild(0).getText();
        String dispositivo = ctx.no_dispositivo().getText();

        tabelaDeSimbolos.adicionarUtensilio(dispositivo);

        switch (acao) {
            case "Asse":
                if (!dispositivo.equalsIgnoreCase("Forno")) {
                    addError("Ação 'Asse' só pode ser usada com 'Forno'. Encontrado: " + dispositivo, ctx.getStart());
                }
                break;
            case "Bata":
                if (!dispositivo.equalsIgnoreCase("Batedeira") && !dispositivo.equalsIgnoreCase("Liquidificador")) {
                    addError("Ação 'Bata' só pode ser usada com 'Batedeira' ou 'Liquidificador'. Encontrado: " + dispositivo, ctx.getStart());
                }
                break;
            case "Cozinhe":
                if (!dispositivo.equalsIgnoreCase("Panela")) {
                    addError("Ação 'Cozinhe' só pode ser usada com 'Panela'. Encontrado: " + dispositivo, ctx.getStart());
                }
                break;
        }
        return super.visitAcao_com_dispositivo(ctx);
    }

    @Override
    public Void visitPasso(ReceitaParser.PassoContext ctx) {
        if (ctx.REPETIR() != null) {
            int numeroRepeticoes = Integer.parseInt(ctx.NUMERO().getText());
            if (numeroRepeticoes <= 0) {
                addError("O número de repetições em 'Repita' deve ser um inteiro positivo.", ctx.NUMERO().getSymbol());
            }
        }
        return super.visitPasso(ctx);
    }
}
