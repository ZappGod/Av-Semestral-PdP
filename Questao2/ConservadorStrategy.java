package Questao2;

public class ConservadorStrategy implements RiskStrategy {

    @Override
    public double calculateRisk(Cliente c) {
        // Fórmula que penaliza idade alta e baixa tolerância; renda e horizonte têm pouco peso
        double tol = c.getToleranciaRisco();
        double idadeFactor = (1.0 - Math.min(c.getIdade(), 100) / 100.0) * 100; // menor idade => ligeiramente mais risco
        double horizonte = Math.min(c.getHorizonteAnos(), 20) / 20.0 * 100;
        double renda = Math.min(c.getRendaAnual() / 100_000.0, 1.0) * 100;
        
        // pesos: tolerância 30%, idade 40%, horizonte 20%, renda 10% (conservador reduz score geral)
        double score = 0.30 * tol + 0.40 * idadeFactor + 0.20 * horizonte + 0.10 * renda;
        // aplicar redução conservadora
        score *= 0.85;
        return clamp(score);
    }

    @Override
    public String getName() {
        return "Conservador";
    }

    private double clamp(double v) {
        if (v < 0) return 0;
        if (v > 100) return 100;
        return v;
    }
}