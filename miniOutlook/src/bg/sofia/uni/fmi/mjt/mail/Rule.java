package bg.sofia.uni.fmi.mjt.mail;

import java.util.LinkedList;
import java.util.List;

public record Rule(String definition, int priority, RuleType ruleType) {

    static List<Rule> readRules(String definition, int priority) {
        List<Rule> result = new LinkedList<>();
        String[] lines = definition.split(System.lineSeparator());
        for (String line : lines) {
            if (line.contains("subject-includes:")) {
                String[] parts = line.split(":");
                result.add(new Rule(parts[1], priority, RuleType.SUBJECT_INCLUDES));
            }
            if (line.contains("subject-or-body-includes:")) {
                String[] parts = line.split(":");
                result.add(new Rule(parts[1], priority, RuleType.SUBJECT_OR_BODY_INCLUDES));
            }
            if (line.contains("from")) {
                String[] parts = line.split(":");
                result.add(new Rule(parts[1], priority, RuleType.FROM));
            }
            if (line.contains("recipients-includes::")) {
                String[] parts = line.split(":");
                result.add(new Rule(parts[1], priority, RuleType.RECIPIENTS_INCLUDES));
            }
        }
        return result;
    }
}
