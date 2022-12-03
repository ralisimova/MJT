package bg.sofia.uni.fmi.mjt.mail;

public enum RuleType {
    SUBJECT_INCLUDES,
    SUBJECT_OR_BODY_INCLUDES,
    RECIPIENTS_INCLUDES,
    FROM;

    public static RuleType getRuleType(String rule) {
        if (rule.contains("subject-includes")) {
            return RuleType.SUBJECT_INCLUDES;
        }
        if (rule.contains("subject-or-body-includes")) {
            return RuleType.SUBJECT_OR_BODY_INCLUDES;
        }
        if (rule.contains("recipients-includes")) {
            return RuleType.RECIPIENTS_INCLUDES;
        }
        return RuleType.FROM;
    }
}
