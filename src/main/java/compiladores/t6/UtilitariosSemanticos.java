package compiladores.t6;

import java.util.Objects;

public class UtilitariosSemanticos {

    public static class IngredienteInfo {
        private final String nome;
        private double quantidadeTotal;
        private String unidadePrincipal;

        public IngredienteInfo(String nome, double quantidade, String unidade) {
            this.nome = nome;
            this.quantidadeTotal = quantidade;
            this.unidadePrincipal = unidade;
        }

        public String getNome() {
            return nome;
        }

        public void addQuantidade(double quantidade, String unidade) {
            String unidadeNormalizada = unidade.replaceAll("s de$", "").replaceAll("s$", "");
            String unidadePrincipalNormalizada = this.unidadePrincipal.replaceAll("s de$", "").replaceAll("s$", "");

            if (Objects.equals(unidadePrincipalNormalizada, unidadeNormalizada)) {
                this.quantidadeTotal += quantidade;
            } else {
                System.err.println(
                        "Aviso Semântico: Tentativa de somar ingrediente '" + nome + "' com unidades diferentes. " +
                                "Atual: " + this.quantidadeTotal + " " + this.unidadePrincipal +
                                ", Adicionando: " + quantidade + " " + unidade
                );
                this.quantidadeTotal += quantidade;
            }
        }

        @Override
        public String toString() {
            String quantidadeStr;
            if (quantidadeTotal == (long) quantidadeTotal) {
                quantidadeStr = String.format("%d", (long) quantidadeTotal);
            } else {
                quantidadeStr = String.format("%.2f", quantidadeTotal);
            }

            if (quantidadeTotal == 1 && unidadePrincipal.endsWith("s")) {
                unidadePrincipal = unidadePrincipal.substring(0, unidadePrincipal.length() - 1);
            }

            // Tratamento para não adicionar 'de' para 'unidade'
            if (unidadePrincipal.equalsIgnoreCase("unidade")) {
                return quantidadeStr + " " + nome;
            }

            return quantidadeStr + " " + unidadePrincipal + " de " + nome;
        }
    }
}