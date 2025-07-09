/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package compiladores.t6;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import compiladores.t6.UtilitariosSemanticos;

public class TabelaDeSimbolos {

    private Map<String, UtilitariosSemanticos.IngredienteInfo> ingredientes = new HashMap<>();
    private Set<String> utensilios = new HashSet<>();

    public TabelaDeSimbolos() {
        utensilios.add("tigela");
        utensilios.add("panela");
        utensilios.add("forma");
        utensilios.add("copo");
        utensilios.add("prato");
        utensilios.add("massa");
    }

    public boolean adicionarOuAtualizarIngrediente(String nome, double quantidade, String unidade) {
        String nomeNormalizado = nome.toLowerCase();
        if (ingredientes.containsKey(nomeNormalizado)) {
            UtilitariosSemanticos.IngredienteInfo infoExistente = ingredientes.get(nomeNormalizado);
            infoExistente.addQuantidade(quantidade, unidade);
            return true;
        } else {
            ingredientes.put(nomeNormalizado, new UtilitariosSemanticos.IngredienteInfo(quantidade, unidade));
            return true;
        }
    }

    public Map<String, UtilitariosSemanticos.IngredienteInfo> getIngredientes() {
        return ingredientes;
    }

    public void adicionarUtensilio(String nomeUtensilio) {
        utensilios.add(nomeUtensilio.toLowerCase());
    }

    public boolean isUtensilioConhecido(String nomeUtensilio) {
        return utensilios.contains(nomeUtensilio.toLowerCase()) ||
               nomeUtensilio.equalsIgnoreCase("Forno") ||
               nomeUtensilio.equalsIgnoreCase("Batedeira") ||
               nomeUtensilio.equalsIgnoreCase("Liquidificador") ||
               nomeUtensilio.equalsIgnoreCase("Geladeira") ||
               nomeUtensilio.equalsIgnoreCase("Panela");
    }

    public Set<String> getUtensilios() {
        return utensilios;
    }
}