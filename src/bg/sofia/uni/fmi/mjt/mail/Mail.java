package bg.sofia.uni.fmi.mjt.mail;

import java.time.LocalDateTime;
import java.util.Set;

public record Mail(Account sender, Set<String> recipients, String subject, String body, LocalDateTime received) {
    Mail checkSender(Account expected) {
        if (!expected.equals(sender)) {
            return new Mail(expected, recipients(), subject(), body(), received());
        }
        return this;
    }
}