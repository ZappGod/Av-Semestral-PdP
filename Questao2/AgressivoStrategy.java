package Questao2;

public class AgressivoStrategy implements RiskStrategy {

    @Override
    public double calculateRisk(Cliente c) {
        // Fórmula com maior peso para tolerância ao risco e horizonte longo
        double tol = c.getToleranciaRisco(); // 0..100
        double horizonte = Math.min(c.getHorizonteAnos(), 40) / 40.0 * 100; // normaliza a 0..100
        double renda = Math.min(c.getRendaAnual() / 200_000.0, 1.0) * 100; // normaliza até 200k
        
        // pesos: tolerância 60%, horizonte 25%, renda 15%
        double score = 0.60 * tol + 0.25 * horizonte + 0.15 * renda;
        return clamp(score);
    }

    @Override
    public String getName() {
        return "Agressivo";
    }

    private double clamp(double v) {
        if (v < 0) return 0;
        if (v > 100) return 100;
        return v;
    }
}