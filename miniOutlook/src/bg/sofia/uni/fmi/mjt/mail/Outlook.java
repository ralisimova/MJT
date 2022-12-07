package bg.sofia.uni.fmi.mjt.mail;

import bg.sofia.uni.fmi.mjt.mail.exceptions.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Outlook implements MailClient {
    private static final int MAX_PRIORITY = 10;
    private Map<Account, PersonalEmail> accounts;

    public Outlook() {
    }

    public Set<Account> getAccounts() {
        return accounts.keySet();
    }

    public PersonalEmail getPersonalFolder(String name) {
        return accounts.get(getAccountByName(name));
    }

    private boolean invalidString(String data) {

        return data == null || data.isEmpty() || data.isBlank();
    }

    private boolean containsAccount(String accountName) {

        return getAccountByName(accountName) != null;
    }

    public Account getAccountByName(String accountName) {
        if (accounts == null) return null;
        for (Account a : accounts.keySet()) {
            if (a.name().equals(accountName)) {
                return a;
            }
        }
        return null;
    }

    private boolean containsStringTwice(String line, String toSearch) {
        int index = line.indexOf(toSearch);
        if (index == -1) return false;

        return (line.substring(index + 1).contains(toSearch));
    }

    private boolean repeatsRule(String rule) {
        return (containsStringTwice(rule, "subject-includes:") ||
                containsStringTwice(rule, "subject-or-body-includes:") ||
                containsStringTwice(rule, "recipients-includes:") ||
                containsStringTwice(rule, "from:"));
    }

    public Account getAccountByEmail(String email) {
        if (accounts == null) return null;

        for (Account a : accounts.keySet()) {
            if (a.emailAddress().equals(email)) {
                return a;
            }
        }
        return null;
    }

    private Mail readMail(String metaData, String content) {
        String[] data = metaData.split(System.lineSeparator());

        Account sender = null;
        Set<String> recipients = new HashSet<>();
        String subject = null;
        LocalDateTime received = null;

        for (String line : data) {
            String result = line.substring(line.indexOf(":") + 1).strip();

            if (line.contains("sender:")) {
                sender = getAccountByEmail(result);
            }
            if (line.contains("subject:")) {
                subject = result;
            }
            if (line.contains("recipients:")) {
                String[] recipient = line.substring(line.indexOf(":") + 1).split(",");
                for (String r : recipient) {
                    r = r.strip();
                    recipients.add(r);
                }
            }
            if (line.contains("received:")) {
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                received = LocalDateTime.parse(result, dateTimeFormatter);
            }
        }
        return new Mail(sender, recipients, subject, content, received);
    }


    @Override
    public Account addNewAccount(String accountName, String email) {
        if (invalidString(accountName) || invalidString(email)) {
            throw new IllegalArgumentException("Tried to add an acount with an " +
                    "invalid account name or email.");
        }

        if (containsAccount(accountName)) {
            throw new AccountAlreadyExistsException("Tried to add an account that already exists.");
        }

        if (getAccountByEmail(email) != null) {
            throw new AccountAlreadyExistsException("Tried to add an account with an email that already exists.");
        }

        Account account = new Account(email, accountName);
        if (accounts == null) {
            accounts = new HashMap<>();
        }
        accounts.put(account, new PersonalEmail());

        return account;
    }

    @Override
    public void createFolder(String accountName, String path) {
        if (invalidString(accountName) || invalidString(path)) {
            throw new IllegalArgumentException("Tried to add an account with an " +
                    "invalid account name or email.");
        }

        if (!containsAccount(accountName)) {
            throw new AccountNotFoundException("This account doesn't exist.");
        }

        int index = path.lastIndexOf('/');
        String parentPath = path.substring(0, index);

        if (!path.startsWith("/inbox") ||
                !getPersonalFolder(accountName).containsPath(parentPath)) {
            throw new InvalidPathException("Tried to create a folder with an invalid path.");
        }
        if (getPersonalFolder(accountName).containsPath(path)) {
            throw new FolderAlreadyExistsException("Tried to create a folder that already exists.");
        }

        getPersonalFolder(accountName).add(path);
    }


    @Override
    public void addRule(String accountName, String folderPath, String ruleDefinition, int priority) {
        if (invalidString(accountName) || invalidString(folderPath) || invalidString(ruleDefinition)) {
            throw new IllegalArgumentException("Tried to add a rule with an " +
                    "invalid account name or email or rule definition.");
        }

        if (priority < 1 || priority > MAX_PRIORITY) {
            throw new IllegalArgumentException("Tried to add a rule with an invalid priority");
        }

        if (!containsAccount(accountName)) {
            throw new AccountNotFoundException("This account doesn't exist.");
        }

        if (!getPersonalFolder(accountName).containsPath(folderPath)) {
            throw new FolderNotFoundException("Tried to create a rule for a folder that doesn't exist.");
        }
        if (repeatsRule(ruleDefinition)) {
            throw new RuleAlreadyDefinedException("Tried to add the same rule type twice.");
        }

        getPersonalFolder(accountName).add(ruleDefinition, priority, folderPath);

        PersonalEmail personal = getPersonalFolder(accountName);

        for (Mail mail : getMailsFromFolder(accountName, "/inbox")) {
            EmailFolder folder = personal.applyRules(mail);
            if (folder != personal.getFolderByName("/inbox")) {
                personal.moveMail(folder, mail);
            }
        }
    }

    @Override
    public void receiveMail(String accountName, String mailMetadata, String mailContent) {
        if (invalidString(accountName) || invalidString(mailMetadata) || invalidString(mailContent)) {
            throw new IllegalArgumentException("Tried to receive mail with invalid params.");
        }
        if (!containsAccount(accountName)) {
            throw new AccountNotFoundException("Tried to receive mail for an account that doesn't exist.");
        }

        Mail mail = readMail(mailMetadata, mailContent);
        getPersonalFolder(accountName).applyRules(mail).addMail(mail);
    }

    @Override
    public Collection<Mail> getMailsFromFolder(String account, String folderPath) {
        if (invalidString(account) || invalidString(folderPath)) {
            throw new IllegalArgumentException("Tried to get mail with invalid data.");
        }
        if (!containsAccount(account)) {
            throw new AccountNotFoundException("tried to get mails for an account that doesn't exist.");
        }
        if (getPersonalFolder(account).getFolderByName(folderPath) == null) {
            throw new FolderNotFoundException("Tried to get mails from a folder that doesn't exist.");
        }
        return getPersonalFolder(account).getFolderByName(folderPath).mails();

    }

    @Override
    public void sendMail(String accountName, String mailMetadata, String mailContent) {
        if (invalidString(accountName) || invalidString(mailMetadata) || invalidString(mailContent)) {
            throw new IllegalArgumentException("Tried to send a mail with invalid data.");
        }
        if (!containsAccount(accountName)) {
            throw new AccountNotFoundException("Tried to send mails from an account that doesn't exist.");
        }

        Mail mail = readMail(mailMetadata, mailContent);
        mail = mail.checkSender(getAccountByName(accountName));

        getPersonalFolder(accountName).getFolderByName("/sent").addMail(mail);

        for (String email : mail.recipients()) {
            email = email.strip();
            Account account = getAccountByEmail(email);
            if (account != null) {
                receiveMail(account.name(), mailMetadata, mailContent);
            }
        }
    }
}
