package bg.sofia.uni.fmi.mjt.mail;

import java.util.LinkedList;
import java.util.List;

public class PersonalEmail {
    private List<EmailFolder> folders;
    private List<Rule> rules;

    public PersonalEmail() {
        folders = new LinkedList<>();
        rules = new LinkedList<>();

        folders.add(new EmailFolder("/inbox", new LinkedList<>()));
        folders.add(new EmailFolder("/sent", new LinkedList<>()));
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
        folders.add(new EmailFolder(folder, new LinkedList<>()));
    }

    public void add(String definition, int priority, String path) {
        rules.add(new Rule(definition, priority, path));
    }

    private boolean ruleApplies(String rule, Mail mail) {
        String[] conditions = rule.split(System.lineSeparator());

        String toCheck = null;
        for (String line : conditions) {
            line = line.strip();

            String[] data = line.substring(line.indexOf(":") + 1).split(",");

            if (line.contains("subject-includes")) {
                for (String word : data) {
                    word = word.strip();
                    if (!mail.subject().contains(word)){
                        return false;
                    }
                }
            }
            if (line.contains("subject-or-body-includes")) {
                for (String word : data) {
                    word = word.strip();
                    if (!mail.subject().contains(word) && !mail.body().contains(word)) {
                        return false;
                    }
                }
            }
            if (line.contains("recipients-includes")) {
                toCheck = line;
            }
            if (line.contains("from") &&
                    !mail.sender().emailAddress().equals(line.substring(line.indexOf(":")).strip())){
                return false;
            }
        }
        if (toCheck != null) {
            String[] recipients = toCheck.substring(toCheck.indexOf(":") + 1).split(",");
            for (String r : recipients) {
                r = r.strip();
                if (mail.recipients().contains(r)) {
                    return true;
                }
            }
            return false;
        }
        return true;
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

    void moveMail(EmailFolder destination, Mail mail) {
        destination.addMail(mail);
        getFolderByName("/inbox").removeMail(mail);
    }
}
