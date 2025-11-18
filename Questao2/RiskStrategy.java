package Questao2;

public interface RiskStrategy {
    
    // Calcula um score de risco entre 0 e 100 para o cliente.
    
    double calculateRisk(Cliente cliente);

    // Nome do modelo para exibição/diagnóstico.
    
    String getName();
}