package Questao2;

public class ModeradoStrategy implements RiskStrategy {

    @Override
    public double calculateRisk(Cliente c) {
        // Fórmula balanceada entre tolerância, idade (inversamente) e horizonte
        double tol = c.getToleranciaRisco();
        double idadeFactor = (1.0 - Math.min(c.getIdade(), 100) / 100.0) * 100; // quanto menor a idade, maior o factor
        double horizonte = Math.min(c.getHorizonteAnos(), 30) / 30.0 * 100;
        
        // pesos: tolerância 40%, idade 30%, horizonte 30%
        double score = 0.40 * tol + 0.30 * idadeFactor + 0.30 * horizonte;
        return clamp(score);
    }

    @Override
    public String getName() {
        return "Moderado";
    }

    private double clamp(double v) {
        if (v < 0) return 0;
        if (v > 100) return 100;
        return v;
    }
}