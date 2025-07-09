
package compiladores.t6;


public class UtilitariosSemanticos {
    public static class IngredienteInfo {
        double quantidadeTotal;
        String unidadePrincipal;

        public IngredienteInfo(double quantidade, String unidade) {
            this.quantidadeTotal = quantidade;
            this.unidadePrincipal = unidade;
        }

        public void addQuantidade(double quantidade, String unidade) {
            if (this.unidadePrincipal.equals(unidade)) {
                this.quantidadeTotal += quantidade;
            } else {
                System.err.println("Aviso Semântico: Tentativa de somar '" + quantidade + " " + unidade +
                                   "' com '" + this.quantidadeTotal + " " + this.unidadePrincipal +
                                   "'. Unidades incompatíveis ou sem conversão implementada.");
            }
        }

        @Override
        public String toString() {
            if (unidadePrincipal.equals("unidades") || unidadePrincipal.equals("unidade")) {
                 return String.format("%.0f %s", quantidadeTotal, unidadePrincipal);
            }
            if (quantidadeTotal == (long) quantidadeTotal) {
                return String.format("%d%s", (long) quantidadeTotal, unidadePrincipal);
            }
            return String.format("%.2f%s", quantidadeTotal, unidadePrincipal);
        }
    }
}
