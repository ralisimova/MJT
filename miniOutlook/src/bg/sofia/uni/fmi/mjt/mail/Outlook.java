package bg.sofia.uni.fmi.mjt.mail;

import bg.sofia.uni.fmi.mjt.mail.exceptions.*;

import java.util.*;

public class Outlook implements MailClient {
    private static final int MAX_PRIORITY = 10;
    // private List<Account> accounts;
    private Map<Account, List<EmailFolder>> personalFolders;

    public Outlook() {
    }

    private boolean invalidString(String data) {
        return data == null || data.isEmpty() || data.isBlank();
    }

    private boolean containsAccount(String accountName) {
        return getAccountByName(accountName) != null;
    }

    private List<Rule> getRulesByPath(String accountName, String path) {
        for (EmailFolder e : personalFolders.get(getAccountByName(accountName))) {
            if (e.path().equals(path)) {
                return e.rules();
            }
        }
        return null;
    }

    private Account getAccountByName(String accountName) {
        for (Account a : personalFolders.keySet()) {
            if (a.name().equals(accountName)) {
                return a;
            }
        }
        return null;
    }



    private String readMetaDataSender(String metaData) {
        //end of string?????
        return metaData.substring("from:".length());
    }

    private String readMetaDataSubject(String metaData) {
        return metaData.substring("subject-includes:".length());
    }

    private String readMetaDataSubjectBody(String metaData) {
        return metaData.substring("subject-or-body-includes:".length());
    }

    private String readMetaDataRecipients(String metaData) {
        return metaData.substring("recipients-includes:".length());
    }
private boolean ruleAppliesSubject(Rule r, String metaData){
        return r.ruleType()==RuleType.SUBJECT_INCLUDES &&
                r.definition().contains(readMetaDataSubject(metaData));
}
    private boolean ruleAppliesSubjectBody(Rule r, String metaData,String content){
        return r.ruleType()==RuleType.SUBJECT_OR_BODY_INCLUDES &&
                r.definition().contains(readMetaDataSubject(metaData)
                &&r.definition().contains(readMetaDataSubject(content));
    }private boolean ruleAppliesSubject(Rule r, String metaData){
        return r.ruleType()==RuleType.SUBJECT_INCLUDES &&
                r.definition().contains(readMetaDataSubject(metaData));
    }private boolean ruleAppliesSubject(Rule r, String metaData){
        return r.ruleType()==RuleType.SUBJECT_INCLUDES &&
                r.definition().contains(readMetaDataSubject(metaData));
    }
    private boolean ruleApplies(List<Rule> rules, String metaData, String content) {
        int priority =MAX_PRIORITY;
        Rule maxRule=null;
      for(Rule r:rules){
          if(r.ruleType()==RuleType.SUBJECT_INCLUDES &&
                  r.definition().contains(readMetaDataSubject(metaData)) &&
                  r.priority()<priority){
              maxRule=r;
              priority=r.priority();
          }
      }
    }

    private String getFolderByRule(String accountName, String metaData, String content) {
        for (EmailFolder e : personalFolders.get(getAccountByName(accountName))) {
            if (e.rules())
        }
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

        Account account = new Account(accountName, email);

        //accounts.add(account);
        List<EmailFolder> folders = new LinkedList<>();
        folders.add(new EmailFolder("inbox", null, null));
        folders.add(new EmailFolder("sent", null, null));
        personalFolders.put(account, folders);

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
        String parentPath = path.substring(0, index - 1);

        List<EmailFolder> currentPaths = personalFolders.get(getAccountByName(accountName));
        //if not with null mails????

        if (!path.startsWith("inbox") || !currentPaths.contains(new EmailFolder(parentPath, getRulesByPath(accountName, parentPath), null))) {
            throw new InvalidPathException("Tried to create a folder with an invalid path.");
        }
//if not with null mails????
        if (currentPaths.contains(new EmailFolder(path, getRulesByPath(accountName, path), null))) {
            throw new FolderAlreadyExistsException("Tried to create a folder that already exists.");
        }

        personalFolders.get(getAccountByName(accountName)).add(new EmailFolder(path, null, null));
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

        List<EmailFolder> currentPaths = personalFolders.get(getAccountByName(accountName));
        if (!currentPaths.contains(new EmailFolder(folderPath, getRulesByPath(accountName, folderPath), null))) {
            throw new FolderNotFoundException("Tried to create a folder that already exists.");
        }
//personalFolders.get(getAccountByName(accountName)).
        //????
        //RuleAlreadyExists????
        /*List<Rule> rulesToAdd=Rule.readRules(ruleDefinition,priority);
        List<EmailFolder> folders=personalFolders.get(getAccountByName(accountName));
        List<Rule>existingRules=null;
        Map<RuleType,Integer> existing;
        for(Rule r:existingRules){
            existing.put(r.ruleType(),r.priority());
        }
        for(EmailFolder f:folders){
            existingRules.addAll(f.rules());
        }
        for(Rule r:rulesToAdd){
            if(existingRules.)
        }*/
        getRulesByPath(accountName, folderPath).addAll(Rule.readRules(ruleDefinition,priority));

    }

    @Override
    public void receiveMail(String accountName, String mailMetadata, String mailContent) {
        if (invalidString(accountName) || invalidString(mailMetadata) || invalidString(mailContent)) {
            throw new IllegalArgumentException("Tried to receive mail with invalid params.");
        }
        if (getAccountByName(accountName) == null) {
            throw new AccountNotFoundException("Tried to receive mail for an account that doesn't exist.");
        }
        //????
        //FolderNotFoundException

//getAccountByName(accountName).
    }

    @Override
    public Collection<Mail> getMailsFromFolder(String account, String folderPath) {
        return null;
    }

    @Override
    public void sendMail(String accountName, String mailMetadata, String mailContent) {

    }
}
