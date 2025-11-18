package Questao4;

import java.util.*;

public class AntiFraude {

    // Modelo simples de transação
    public static class Transaction {
        public final double amount;
        public final String country;
        public final String userId;
        public final String deviceId;
        public final boolean hasPastFraud;
        public final Set<String> knownDevices;

        public Transaction(double amount, String country, String userId, String deviceId,
                           boolean hasPastFraud, Set<String> knownDevices) {
            this.amount = amount;
            this.country = country;
            this.userId = userId;
            this.deviceId = deviceId;
            this.hasPastFraud = hasPastFraud;
            this.knownDevices = knownDevices != null ? knownDevices : Collections.emptySet();
        }
    }

    // Resultado da validação
    public static class ValidationResult {
        public final boolean approved;
        public final String reason;

        public ValidationResult(boolean approved, String reason) {
            this.approved = approved;
            this.reason = reason;
        }

        public static ValidationResult ok() { return new ValidationResult(true, "Aprovado"); }
        public static ValidationResult fail(String reason) { return new ValidationResult(false, reason); }
        @Override
        public String toString() { return (approved ? "APROVADO: " : "BARRADO: ") + reason; }
    }

    // Handler da cadeia
    public interface ValidationHandler {
        void setNext(ValidationHandler next);
        ValidationResult handle(Transaction tx);
    }

    // Classe base que facilita criação de handlers
    public static abstract class BaseHandler implements ValidationHandler {
        private ValidationHandler next;

        public void setNext(ValidationHandler next) { this.next = next; }

        // Implementações devem retornar null quando a checagem passar
        protected abstract ValidationResult check(Transaction tx);

        public ValidationResult handle(Transaction tx) {
            ValidationResult res = check(tx);
            if (res != null && !res.approved) {
                // bloqueado aqui
                return res;
            }
            // passou nessa etapa -> encaminha ao próximo ou aprova
            if (next != null) return next.handle(tx);
            return ValidationResult.ok();
        }
    }

    // 1) Checagem de valor suspeito
    public static class AmountCheck extends BaseHandler {
        private final double threshold;

        public AmountCheck(double threshold) { this.threshold = threshold; }

        @Override
        protected ValidationResult check(Transaction tx) {
            if (tx.amount > threshold) {
                return ValidationResult.fail(String.format("Valor suspeito: %.2f > %.2f", tx.amount, threshold));
            }
            return null;
        }
    }

    // 2) Checagem de geolocalização
    public static class GeoLocationCheck extends BaseHandler {
        private final Set<String> allowedCountries;

        public GeoLocationCheck(Set<String> allowedCountries) {
            this.allowedCountries = allowedCountries != null ? allowedCountries : Collections.emptySet();
        }

        @Override
        protected ValidationResult check(Transaction tx) {
            if (!allowedCountries.isEmpty() && !allowedCountries.contains(tx.country)) {
                return ValidationResult.fail("Localização incomum: " + tx.country);
            }
            return null;
        }
    }

    // 3) Verificação de histórico do usuário
    public static class UserHistoryCheck extends BaseHandler {
        @Override
        protected ValidationResult check(Transaction tx) {
            if (tx.hasPastFraud) {
                return ValidationResult.fail("Usuário com histórico de fraude");
            }
            return null;
        }
    }

    // 4) Detecção de dispositivo incomum
    public static class DeviceCheck extends BaseHandler {
        @Override
        protected ValidationResult check(Transaction tx) {
            if (tx.deviceId != null && !tx.knownDevices.contains(tx.deviceId)) {
                return ValidationResult.fail("Dispositivo incomum: " + tx.deviceId);
            }
            return null;
        }
    }

    // Exemplo de como adicionar uma nova verificação sem alterar as anteriores
    public static class BlacklistCountryCheck extends BaseHandler {
        private final Set<String> blacklisted;
        public BlacklistCountryCheck(Set<String> blacklisted) { this.blacklisted = blacklisted; }
        @Override
        protected ValidationResult check(Transaction tx) {
            if (blacklisted != null && blacklisted.contains(tx.country)) {
                return ValidationResult.fail("País na lista negra: " + tx.country);
            }
            return null;
        }
    }

    // Demonstração e uso
    public static void main(String[] args) {
        // Monta cadeia: Amount -> Geo -> UserHistory -> Device
        AmountCheck amount = new AmountCheck(10000.0);
        GeoLocationCheck geo = new GeoLocationCheck(new HashSet<>(Arrays.asList("BR", "US", "PT")));
        UserHistoryCheck history = new UserHistoryCheck();
        DeviceCheck device = new DeviceCheck();

        amount.setNext(geo);
        geo.setNext(history);
        history.setNext(device);

        // Também mostramos que podemos inserir uma checagem extra sem modificar classes existentes
        BlacklistCountryCheck blacklist = new BlacklistCountryCheck(new HashSet<>(Arrays.asList("NG", "IR")));
        // exemplo: inserir blacklist entre geo e history
        geo.setNext(blacklist);
        blacklist.setNext(history);

        // Casos de teste
        List<Transaction> tests = new ArrayList<>();
        tests.add(new Transaction(50.0, "BR", "user1", "dev-1", false, new HashSet<>(Arrays.asList("dev-1"))));
        tests.add(new Transaction(20000.0, "BR", "user2", "dev-2", false, new HashSet<>(Arrays.asList("dev-2"))));
        tests.add(new Transaction(100.0, "NG", "user3", "dev-3", false, new HashSet<>(Arrays.asList("dev-3"))));
        tests.add(new Transaction(20.0, "US", "user4", "dev-new", false, new HashSet<>(Arrays.asList("dev-old"))));
        tests.add(new Transaction(10.0, "PT", "user5", "dev-a", true, new HashSet<>(Arrays.asList("dev-a"))));

        int i = 1;
        for (Transaction tx : tests) {
            ValidationResult r = amount.handle(tx);
            System.out.println("Transacao " + (i++) + ": " + r);
        }

        // Exemplo de reorganização: colocar DeviceCheck antes de AmountCheck
        System.out.println("\n-- Reorganizando cadeia: Device -> Amount -> Geo -> History --");
        device.setNext(amount);
        amount.setNext(geo);
        geo.setNext(history); // removing blacklist for simplicity
        history.setNext(null);

        Transaction tx6 = new Transaction(15000.0, "BR", "user6", "dev-x", false, new HashSet<>(Arrays.asList("dev-x")));
        System.out.println("Transacao reorganizada: " + device.handle(tx6));
    }
}
