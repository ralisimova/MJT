package bg.sofia.uni.fmi.mjt.mail;

import java.util.LinkedList;
import java.util.List;

public class PersonalEmail {
    private List<EmailFolder> folders;
    private List<Rule> rules;

    public PersonalEmail() {
        folders = new LinkedList<>();
        rules = new LinkedList<>();
        folders.add(new EmailFolder("/inbox", null));
        folders.add(new EmailFolder("/sent", null));
    }

    public EmailFolder getFolderByName(String name) {
        for (EmailFolder folder : folders) {
            if (folder.path().equals(name)) return folder;
        }
        return null;
    }

    public List<EmailFolder> getFolders() {
        return folders;
    }

    public List<Rule> getRules() {
        return rules;
    }

    public boolean containsPath(String path) {
        for (EmailFolder folder : folders) {
            if (folder.path().equals(path)) return true;
        }
        return false;
    }

    public void add(String folder) {
        folders.add(new EmailFolder(folder, null));
    }

    public void add(String definition, int priority, String path) {
        List<Rule> result = new LinkedList<>();
        String[] lines = definition.split(System.lineSeparator());
        for (String line : lines) {
            rules.add(new Rule(line, priority, path));
        }
    }

    private boolean ruleApplies(String rule, Mail mail) {
        if (rule.contains("subject-includes")) {
            String[] subjects = rule.substring(rule.indexOf(":" + 1)).split(",");
            for (String word : subjects) {
                if (!mail.subject().contains(word)) return false;
            }
            return true;
        }
        if (rule.contains("subject-or-body-includes")) {
            String[] subjects = rule.substring(rule.indexOf(":" + 1)).split(",");
            for (String word : subjects) {
                if (!mail.subject().contains(word) && !mail.body().contains(word)) return false;
            }
            return true;
        }
        if (rule.contains("recipients-includes")) {
            String[] recip = rule.substring(rule.indexOf(":" + 1)).split(",");
            for (String r : recip) {
                if (mail.recipients().contains(r)) return true;
            }
        }
        if (rule.contains("from") &&
                mail.sender().emailAddress().equals(rule.substring(rule.indexOf(":"))))
            return true;

        return false;

    }

    public EmailFolder applyRules(Mail mail) {
        int maxPriority = 11;
        Rule maxRule = null;
        for (Rule r : rules) {
            if (ruleApplies(r.definition(), mail) && r.priority() < maxPriority) {
                maxRule = r;
                maxPriority = r.priority();
            }
        }
        if (maxRule == null) return getFolderByName("/inbox");
        return getFolderByName(maxRule.path());
    }
}
