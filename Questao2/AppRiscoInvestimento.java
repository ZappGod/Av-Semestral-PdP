package Questao2;

public class AppRiscoInvestimento {
    public static void main(String[] args) {
        // Exemplo de uso: criar clientes e alternar modelos dinamicamente
        Cliente cliente1 = new Cliente(28, 120000, 15, 75); // jovem, boa tolerância
        Cliente cliente2 = new Cliente(55, 80000, 5, 30);   // mais velho, baixa tolerância

        RiskAnalyzer analyzer = new RiskAnalyzer(new ModeradoStrategy());

        System.out.println("Análise inicial (Moderado):");
        System.out.println("Cliente1 -> " + analyzer.analyze(cliente1));
        System.out.println("Cliente2 -> " + analyzer.analyze(cliente2));

        // Consultor decide mudar o modelo para agressivo
        analyzer.setStrategy(new AgressivoStrategy());
        System.out.println("\nApós troca para Agressivo:");
        System.out.println("Cliente1 -> " + analyzer.analyze(cliente1));
        System.out.println("Cliente2 -> " + analyzer.analyze(cliente2));

        // Troca para conservador
        analyzer.setStrategy(new ConservadorStrategy());
        System.out.println("\nApós troca para Conservador:");
        System.out.println("Cliente1 -> " + analyzer.analyze(cliente1));
        System.out.println("Cliente2 -> " + analyzer.analyze(cliente2));
    }
}