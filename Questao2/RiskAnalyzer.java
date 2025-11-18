package Questao2;

public class RiskAnalyzer {
    private RiskStrategy strategy;

    public RiskAnalyzer(RiskStrategy initial) {
        this.strategy = initial;
    }

    public void setStrategy(RiskStrategy strategy) {
        this.strategy = strategy;
    }

    public Result analyze(Cliente cliente) {
        double score = strategy.calculateRisk(cliente);
        String categoria = categorize(score);
        return new Result(strategy.getName(), score, categoria);
    }

    private String categorize(double score) {
        if (score >= 66) return "Alto";
        if (score >= 33) return "Moderado";
        return "Baixo";
    }

    public static class Result {
        public final String modelo;
        public final double score;
        public final String categoria;

        public Result(String modelo, double score, String categoria) {
            this.modelo = modelo;
            this.score = score;
            this.categoria = categoria;
        }

        @Override
        public String toString() {
            return String.format("Modelo=%s | Score=%.2f | Categoria=%s", modelo, score, categoria);
        }
    }
}