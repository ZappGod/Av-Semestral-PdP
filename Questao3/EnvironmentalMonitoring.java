package Questao3;

import java.util.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EnvironmentalMonitoring {

    // Tipos de sensor disponíveis
    public enum SensorType {
        TEMPERATURE, HUMIDITY, POLLUTION
    }

    // Interface do observador (baixo acoplamento)
    public interface SensorObserver {
        void update(Sensor sensor, double newValue);
    }

    // Classe Sensor (Sujeito) - notifica observadores quando há mudança
    public static class Sensor {
        private final String id;
        private final String region;
        private final SensorType type;
        private double value;
        private final List<SensorObserver> observers = new ArrayList<>();

        public Sensor(String id, String region, SensorType type) {
            this.id = id;
            this.region = region;
            this.type = type;
        }

        public String getId() { return id; }
        public String getRegion() { return region; }
        public SensorType getType() { return type; }
        public double getValue() { return value; }

        // Inscrição
        public void addObserver(SensorObserver o) {
            synchronized(observers) {
                if (!observers.contains(o)) observers.add(o);
            }
        }

        // Cancelamento
        public void removeObserver(SensorObserver o) {
            synchronized(observers) {
                observers.remove(o);
            }
        }

        // Atualiza valor e notifica observadores
        public void setValue(double newValue) {
            this.value = newValue;
            notifyObservers();
        }

        private void notifyObservers() {
            // Fazemos uma cópia para evitar ConcurrentModification se um observador se desinscrever durante a notificação
            List<SensorObserver> snapshot;
            synchronized(observers) {
                snapshot = new ArrayList<>(observers);
            }
            for (SensorObserver o : snapshot) {
                try {
                    o.update(this, value);
                } catch (Exception e) {
                    System.err.println("Erro ao notificar observador: " + e.getMessage());
                }
            }
        }

        @Override
        public String toString() {
            return String.format("Sensor[%s] (%s) - %s", id, region, type);
        }
    }

    // Observador: Painel de Controle - exibe atualizações em tempo real
    public static class ControlPanel implements SensorObserver {
        private final String name;
        public ControlPanel(String name) { this.name = name; }

        @Override
        public void update(Sensor sensor, double newValue) {
            System.out.printf("[Painel %s] %s atualizou para %.2f\n", name, sensor, newValue);
        }
    }

    // Observador: Módulo de Alertas - dispara alertas quando ultrapassa limites
    public static class AlertModule implements SensorObserver {
        private final String name;
        private final Map<SensorType, Double> thresholds = new EnumMap<>(SensorType.class);

        // Limiares podem ser passados ou usamos alguns defaults
        public AlertModule(String name) {
            this.name = name;
            // valores default razoáveis
            thresholds.put(SensorType.TEMPERATURE, 35.0); // ºC
            thresholds.put(SensorType.HUMIDITY, 20.0); // % (exemplo: alerta quando muito seco)
            thresholds.put(SensorType.POLLUTION, 100.0); // índice de poluição (qualquer unidade)
        }

        public void setThreshold(SensorType type, double value) {
            thresholds.put(type, value);
        }

        @Override
        public void update(Sensor sensor, double newValue) {
            Double threshold = thresholds.get(sensor.getType());
            boolean alert = false;
            String reason = "";
            if (threshold != null) {
                switch (sensor.getType()) {
                    case TEMPERATURE:
                        if (newValue >= threshold) { alert = true; reason = String.format("Temperatura >= %.1f", threshold); }
                        break;
                    case HUMIDITY:
                        if (newValue <= threshold) { alert = true; reason = String.format("Umidade <= %.1f", threshold); }
                        break;
                    case POLLUTION:
                        if (newValue >= threshold) { alert = true; reason = String.format("Poluição >= %.1f", threshold); }
                        break;
                }
            }
            if (alert) {
                System.out.printf("[ALERTA %s] %s: valor=%.2f -> %s\n", name, sensor, newValue, reason);
            } else {
                // para não poluir a saída, apenas logamos de nível baixo
                System.out.printf("[AlertModule %s] %s dentro dos limites (%.2f)\n", name, sensor, newValue);
            }
        }
    }

    // Observador: Logger de Histórico - registra as leituras para posterior análise
    public static class HistoryLogger implements SensorObserver {
        private final String name;
        private final List<String> history = new ArrayList<>();
        private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        public HistoryLogger(String name) { this.name = name; }

        @Override
        public void update(Sensor sensor, double newValue) {
            String entry = String.format("%s | %s | %.2f | %s", LocalDateTime.now().format(dtf), sensor, newValue, sensor.getRegion());
            synchronized(history) { history.add(entry); }
            System.out.printf("[History %s] gravado: %s\n", name, entry);
        }

        public void printHistory() {
            System.out.println("--- Histórico (" + name + ") ---");
            synchronized(history) {
                if (history.isEmpty()) System.out.println("(vazio)");
                for (String e : history) System.out.println(e);
            }
            System.out.println("-------------------------");
        }
    }

    // Demo: criar sensores, inscrever observadores e simular atualizações
    public static void main(String[] args) throws InterruptedException {
        // Cria sensores
        Sensor s1 = new Sensor("S-100", "Centro", SensorType.TEMPERATURE);
        Sensor s2 = new Sensor("S-200", "Zona Norte", SensorType.HUMIDITY);
        Sensor s3 = new Sensor("S-300", "Vale", SensorType.POLLUTION);

        // Cria observadores
        ControlPanel panel = new ControlPanel("Principal");
        AlertModule alerts = new AlertModule("Central");
        HistoryLogger logger = new HistoryLogger("DB-01");

        // Configura threshold customizado (opcional)
        alerts.setThreshold(SensorType.POLLUTION, 80.0);

        // Inscreve observadores aos sensores (baixo acoplamento: sensores não conhecem implementações concretas)
        s1.addObserver(panel);
        s1.addObserver(alerts);
        s1.addObserver(logger);

        s2.addObserver(panel);
        s2.addObserver(alerts);
        s2.addObserver(logger);

        s3.addObserver(panel);
        s3.addObserver(alerts);
        s3.addObserver(logger);

        // Simula atualizações
        System.out.println("\n=== Simulação de leituras iniciais ===");
        s1.setValue(28.5);
        Thread.sleep(200);
        s2.setValue(45.0);
        Thread.sleep(200);
        s3.setValue(60.0);

        // Atualizações que disparam alertas
        System.out.println("\n=== Leituras que podem gerar alertas ===");
        s1.setValue(36.2);  // temperatura alta -> alerta
        Thread.sleep(200);
        s2.setValue(18.0);  // umidade baixa -> alerta
        Thread.sleep(200);
        s3.setValue(120.0); // poluição alta -> alerta

        // Demonstra adicionar novo observador dinamicamente sem alterar Sensor
        System.out.println("\n=== Adicionando um painel secundário dinamicamente ===");
        ControlPanel panelSec = new ControlPanel("Secundario");
        s1.addObserver(panelSec);
        s1.setValue(22.0); // notifica também o painel secundário

        // Demonstra remoção de observador
        System.out.println("\n=== Removendo painel Principal do sensor S-100 ===");
        s1.removeObserver(panel);
        s1.setValue(40.0); // painel principal já não deve receber

        // Mostra histórico registrado
        System.out.println("\n=== Histórico gravado (logger) ===");
        logger.printHistory();

        System.out.println("\nDemo concluída.");
    }
}