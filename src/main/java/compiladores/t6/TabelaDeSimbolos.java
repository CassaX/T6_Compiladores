package compiladores.t6;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
// Importa a classe IngredienteInfo do outro arquivo
import compiladores.t6.UtilitariosSemanticos.IngredienteInfo;

/**
 * Gerencia os símbolos da receita (ingredientes e utensílios).
 * Mantém o estado da receita conforme ela é analisada.
 */
public class TabelaDeSimbolos {
    private final Map<String, IngredienteInfo> ingredientes = new HashMap<>();
    private final Set<String> utensilios = new HashSet<>();

    /**
     * Adiciona um novo ingrediente ou atualiza a quantidade de um existente.
     * @param nome O nome do ingrediente (ex: "Farinha de Trigo").
     * @param quantidade A quantidade a ser adicionada.
     * @param unidade A unidade de medida.
     */
    public void adicionarIngrediente(String nome, double quantidade, String unidade) {
        String nomeNormalizado = nome.toLowerCase();
        if (ingredientes.containsKey(nomeNormalizado)) {
            ingredientes.get(nomeNormalizado).addQuantidade(quantidade, unidade);
        } else {
            // Usa a classe IngredienteInfo importada
            ingredientes.put(nomeNormalizado, new IngredienteInfo(nome, quantidade, unidade));
        }
    }

    /**
     * Verifica se um ingrediente já foi declarado na tabela.
     * @param nome O nome do ingrediente.
     * @return true se o ingrediente existe, false caso contrário.
     */
    public boolean ingredienteExiste(String nome) {
        return ingredientes.containsKey(nome.toLowerCase());
    }

    /**
     * Adiciona um utensílio (panela, tigela, Forno, etc.) ao conjunto de utensílios.
     * @param nome O nome do utensílio.
     */
    public void adicionarUtensilio(String nome) {
        utensilios.add(nome);
    }

    public Map<String, IngredienteInfo> getIngredientes() {
        return ingredientes;
    }

    public Set<String> getUtensilios() {
        return utensilios;
    }
}