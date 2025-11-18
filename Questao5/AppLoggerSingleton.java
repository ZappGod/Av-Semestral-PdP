package Questao5;

import java.time.LocalDateTime;

class SistemaLogger {

    // Instância única (volatile para evitar problemas de visibilidade entre threads)
    private static volatile SistemaLogger instancia;

    // Construtor privado — impede criação fora da classe
    private SistemaLogger() {
        System.out.println(">> SistemaLogger inicializado (instância única criada)");
    }

    // Método estático para acesso global (Double-Checked Locking)
    public static SistemaLogger getInstancia() {

        if (instancia == null) { // primeira verificação (rápida)

            synchronized (SistemaLogger.class) {
                if (instancia == null) { // segunda verificação (segura)
                    instancia = new SistemaLogger();
                }
            }
        }
        return instancia;
    }

    public void logErro(String mensagem) {
        registrar("[ERRO] " + mensagem);
        enviarServidor("[ERRO] " + mensagem);
    }

    public void logEvento(String mensagem) {
        registrar("[EVENTO] " + mensagem);
        enviarServidor("[EVENTO] " + mensagem);
    }

    public void logAuditoria(String mensagem) {
        registrar("[AUDITORIA] " + mensagem);
        enviarServidor("[AUDITORIA] " + mensagem);
    }

    // Simula gravação em arquivo
    private void registrar(String mensagem) {
        System.out.println(LocalDateTime.now() + " -> Gravando em arquivo: " + mensagem);
    }

    // Simula envio ao servidor externo
    private void enviarServidor(String mensagem) {
        System.out.println(LocalDateTime.now() + " -> Enviando ao servidor externo: " + mensagem + "\n");
    }
}

public class AppLoggerSingleton {

    public static void main(String[] args) {

        System.out.println("=== Sistema Centralizado de Logs ===");

        // Obtém a instância única do logger
        SistemaLogger logger = SistemaLogger.getInstancia();

        logger.logEvento("Aplicação iniciada");
        logger.logErro("Falha ao carregar módulo X");
        logger.logAuditoria("Usuário admin autenticado");

        // Teste multi-thread
        System.out.println("\n--- Testando Singleton em ambiente multi-thread ---");

        Runnable tarefa = () -> {
            SistemaLogger log = SistemaLogger.getInstancia();
            log.logEvento("Log vindo da thread " + Thread.currentThread().getName());
        };

        Thread t1 = new Thread(tarefa, "T1");
        Thread t2 = new Thread(tarefa, "T2");
        Thread t3 = new Thread(tarefa, "T3");

        t1.start();
        t2.start();
        t3.start();
    }
}