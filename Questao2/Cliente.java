package Questao2;

public class Cliente {
    private final int idade;             // anos
    private final double rendaAnual;     // em moeda local
    private final int horizonteAnos;     // horizonte de investimento em anos
    private final double toleranciaRisco; // 0..100 (auto-relatada)

    public Cliente(int idade, double rendaAnual, int horizonteAnos, double toleranciaRisco) {
        this.idade = idade;
        this.rendaAnual = rendaAnual;
        this.horizonteAnos = horizonteAnos;
        this.toleranciaRisco = Math.max(0, Math.min(100, toleranciaRisco));
    }

    public int getIdade() { return idade; }
    public double getRendaAnual() { return rendaAnual; }
    public int getHorizonteAnos() { return horizonteAnos; }
    public double getToleranciaRisco() { return toleranciaRisco; }
}