package Questao1;

import java.util.*;


interface Relatorio {
    void preparar();
    void gerar();
}

class RelatorioDiario implements Relatorio {

    @Override
    public void preparar() {
        System.out.println("[Relatório Diário] Selecionando dados do último dia...");
        System.out.println("[Relatório Diário] Calculando métricas de desempenho diário...");
    }

    @Override
    public void gerar() {
        System.out.println("[Relatório Diário] Gerando PDF com tabelas e gráficos simples.\n");
    }
}

class RelatorioSemanal implements Relatorio {

    @Override
    public void preparar() {
        System.out.println("[Relatório Semanal] Coletando dados dos últimos 7 dias...");
        System.out.println("[Relatório Semanal] Priorizando métricas semanais...");
    }

    @Override
    public void gerar() {
        System.out.println("[Relatório Semanal] Gerando relatório em formato XLS com gráficos comparativos.\n");
    }
}

abstract class CriadorRelatorio {

    // O Factory Method
    public abstract Relatorio criarRelatorio();

    // Processo geral — permanece imutável, respeitando SRP e Open/Closed
    public void gerarRelatorio() {
        Relatorio relatorio = criarRelatorio(); // delega para subclasses
        relatorio.preparar();
        relatorio.gerar();
    }
}

class CriadorRelatorioDiario extends CriadorRelatorio {
    @Override
    public Relatorio criarRelatorio() {
        return new RelatorioDiario();
    }
}

class CriadorRelatorioSemanal extends CriadorRelatorio {
    @Override
    public Relatorio criarRelatorio() {
        return new RelatorioSemanal();
    }
}


public class AppRelatorios {

    public static void main(String[] args) {

        System.out.println("=== Sistema Gerador de Relatórios ===\n");

        // Criador de relatório diário
        CriadorRelatorio criadorDiario = new CriadorRelatorioDiario();
        criadorDiario.gerarRelatorio();

        // Criador de relatório semanal
        CriadorRelatorio criadorSemanal = new CriadorRelatorioSemanal();
        criadorSemanal.gerarRelatorio();

        System.out.println("Novos tipos de relatórios podem ser adicionados sem alterar o núcleo!");
    }
}