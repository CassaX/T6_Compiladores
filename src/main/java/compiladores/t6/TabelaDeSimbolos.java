package compiladores.t6;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import compiladores.t6.UtilitariosSemanticos.IngredienteInfo;

public class TabelaDeSimbolos {
    private final Map<String, IngredienteInfo> ingredientes = new HashMap<>();
    private final Set<String> utensilios = new HashSet<>();

  
    public void adicionarIngrediente(String nome, double quantidade, String unidade) {
        String nomeNormalizado = nome.toLowerCase();
        if (ingredientes.containsKey(nomeNormalizado)) {
            ingredientes.get(nomeNormalizado).addQuantidade(quantidade, unidade);
        } else {
            ingredientes.put(nomeNormalizado, new IngredienteInfo(nome, quantidade, unidade));
        }
    }

    public boolean ingredienteExiste(String nome) {
        return ingredientes.containsKey(nome.toLowerCase());
    }
    
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