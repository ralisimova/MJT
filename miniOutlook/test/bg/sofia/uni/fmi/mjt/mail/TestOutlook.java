package bg.sofia.uni.fmi.mjt.mail;

import bg.sofia.uni.fmi.mjt.mail.exceptions.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


public class TestOutlook {
    private static final int EXPECTED_NUMBER=2;
    private static final int BETTER_PRIORITY=5;
    private static final int WEAKER_PRIORITY=6;
    private static final int ILLEGAL_PRIORITY=11;



    @Test
    void testAddNewAccountInvalidName() {
        Outlook outlook = new Outlook();

        assertThrows(IllegalArgumentException.class, () -> outlook.addNewAccount(null, "email"));
        assertThrows(IllegalArgumentException.class, () -> outlook.addNewAccount("", "email"));
        assertThrows(IllegalArgumentException.class, () -> outlook.addNewAccount(" ", "email"));
    }

    @Test
    void testAddNewAccountInvalidEmail() {
        Outlook outlook = new Outlook();

        assertThrows(IllegalArgumentException.class, () -> outlook.addNewAccount("name", null));
        assertThrows(IllegalArgumentException.class, () -> outlook.addNewAccount("name", ""));
        assertThrows(IllegalArgumentException.class, () -> outlook.addNewAccount("name", " "));
    }

    @Test
    void testAddNewAccountAlreadyExists() {
        Outlook outlook = new Outlook();
        outlook.addNewAccount("name", "email");

        assertThrows(AccountAlreadyExistsException.class,
                () -> outlook.addNewAccount("name", "email2"));
        assertThrows(AccountAlreadyExistsException.class,
                () -> outlook.addNewAccount("another name", "email"));

    }

    @Test
    void testAddNewAccount() {
        Outlook outlook = new Outlook();
        outlook.addNewAccount("name", "email");
        outlook.addNewAccount("name1", "email1");

        Set<Account> testSet = new HashSet<>(List.of(new Account("email", "name"),
                new Account("email1", "name1")));

        assertTrue(testSet.size() == outlook.getAccounts().size() &&
                testSet.containsAll(outlook.getAccounts()) &&
                outlook.getAccounts().containsAll(testSet));
    }

    @Test
    void testCreateFolderInvalidName() {
        Outlook outlook = new Outlook();

        assertThrows(IllegalArgumentException.class, () -> outlook.createFolder(null, "path"));
        assertThrows(IllegalArgumentException.class, () -> outlook.createFolder("", "path"));
        assertThrows(IllegalArgumentException.class, () -> outlook.createFolder(" ", "path"));
    }

    @Test
    void testCreateFolderInvalidNamePath() {
        Outlook outlook = new Outlook();

        assertThrows(IllegalArgumentException.class, () -> outlook.createFolder("name", null));
        assertThrows(IllegalArgumentException.class, () -> outlook.createFolder("name", ""));
        assertThrows(IllegalArgumentException.class, () -> outlook.createFolder("name", " "));
    }

    @Test
    void testCreateFolderAccountNotFound() {
        Outlook outlook = new Outlook();

        assertThrows(AccountNotFoundException.class, () -> outlook.createFolder("name", "/inbox"));
    }

    @Test
    void testCreateFolderInvalidPath() {
        Outlook outlook = new Outlook();
        outlook.addNewAccount("name", "email");

        assertThrows(InvalidPathException.class,
                () -> outlook.createFolder("name", "impossible/path"));

        assertThrows(InvalidPathException.class,
                () -> outlook.createFolder("name", "/inbox/path/path_nonexistent"));

    }

    @Test
    void testCreateFolderAlreadyExist() {
        Outlook outlook = new Outlook();
        outlook.addNewAccount("name", "email");
        outlook.createFolder("name", "/inbox/path");

        assertThrows(FolderAlreadyExistsException.class,
                () -> outlook.createFolder("name", "/inbox/path"));
    }

    @Test
    void testCreateFolderInitiallyTwoFolders() {
        Outlook outlook = new Outlook();
        outlook.addNewAccount("name", "email");

        assertEquals(EXPECTED_NUMBER, outlook.getPersonalFolder("name").getFolders().size());
    }

    @Test
    void testCreateFolder() {
        Outlook outlook = new Outlook();
        outlook.addNewAccount("name", "email");

        outlook.createFolder("name", "/inbox/path");
        outlook.createFolder("name", "/inbox/path/next");

        assertEquals(4, outlook.getPersonalFolder("name").getFolders().size());
    }

    @Test
    void testAddRuleInvalidArgumentName() {
        Outlook outlook = new Outlook();

        assertThrows(IllegalArgumentException.class,
                () -> outlook.addRule(null, "path", "definition", BETTER_PRIORITY));
        assertThrows(IllegalArgumentException.class,
                () -> outlook.addRule("", "path", "definition", BETTER_PRIORITY));
        assertThrows(IllegalArgumentException.class,
                () -> outlook.addRule(" ", "path", "definition", BETTER_PRIORITY));
    }

    @Test
    void testAddRuleInvalidArgumentPath() {
        Outlook outlook = new Outlook();

        assertThrows(IllegalArgumentException.class,
                () -> outlook.addRule("name", null, "definition", BETTER_PRIORITY));
        assertThrows(IllegalArgumentException.class,
                () -> outlook.addRule("name", "", "definition", BETTER_PRIORITY));
        assertThrows(IllegalArgumentException.class,
                () -> outlook.addRule("name", " ", "definition", BETTER_PRIORITY));
    }

    @Test
    void testAddRuleInvalidArgumentRule() {
        Outlook outlook = new Outlook();

        assertThrows(IllegalArgumentException.class,
                () -> outlook.addRule("name", "path", null, BETTER_PRIORITY));
        assertThrows(IllegalArgumentException.class,
                () -> outlook.addRule("name", "path", "", BETTER_PRIORITY));
        assertThrows(IllegalArgumentException.class,
                () -> outlook.addRule(null, "path", " ", BETTER_PRIORITY));
    }

    @Test
    void testAddRuleInvalidArgumentPriority() {
        Outlook outlook = new Outlook();

        assertThrows(IllegalArgumentException.class,
                () -> outlook.addRule("name", "path", "definition", ILLEGAL_PRIORITY));
        assertThrows(IllegalArgumentException.class,
                () -> outlook.addRule("name", "path", "definition", 0));
    }


    @Test
    void testAddRuleAccountNotFound() {
        Outlook outlook = new Outlook();

        assertThrows(AccountNotFoundException.class,
                () -> outlook.addRule("name", "path", "definition", BETTER_PRIORITY));
    }

    @Test
    void testAddRuleFolderNotFound() {
        Outlook outlook = new Outlook();

        outlook.addNewAccount("name", "email");
        assertThrows(FolderNotFoundException.class,
                () -> outlook.addRule("name", "/inbox/path", "definition", BETTER_PRIORITY));

    }

    @Test
    void testAddRuleAlreadyDefined() {
        Outlook outlook = new Outlook();
        outlook.addNewAccount("name", "email");

        assertThrows(RuleAlreadyDefinedException.class,
                () -> outlook.addRule("name", "/inbox",
                        "subject-includes: mjt, izpit, 2022" +
                                "subject-includes: mjt, something, else",
                        BETTER_PRIORITY));
    }

    @Test
    void testAddRule() {
        Outlook outlook = new Outlook();
        outlook.addNewAccount("name", "email");

        outlook.addRule("name", "/inbox", "subject-includes: mjt, izpit, 2022"
                + System.lineSeparator() +
                "subject-or-body-includes: mjt, something, else", BETTER_PRIORITY);

        outlook.addRule("name", "/inbox",
                "recipients-includes:stoyo@fmi.bg,stoyo1@fmi.bg", WEAKER_PRIORITY);

        List<Rule> rules = outlook.getPersonalFolder("name").getRules();

        List<Rule> expected = List.of(new Rule("subject-includes: mjt, izpit, 2022"
                        + System.lineSeparator() +
                        "subject-or-body-includes: mjt, something, else", BETTER_PRIORITY, "/inbox"),
                new Rule("recipients-includes:stoyo@fmi.bg,stoyo1@fmi.bg", WEAKER_PRIORITY, "/inbox"));

        assertTrue(expected.size() == rules.size() &&
                expected.containsAll(rules) &&
                rules.containsAll(expected));
    }

    @Test
    void testAddRuleChangesInbox() {
        Outlook outlook = new Outlook();
        outlook.addNewAccount("name", "email");
        outlook.createFolder("name", "/inbox/special");

        outlook.receiveMail("name", "sender: testy@gmail.com" +
                System.lineSeparator() +
                "      subject: Hello, mjt!" +
                System.lineSeparator() +
                "      recipients: pesho@gmail.com, gosho@gmail.com," +
                System.lineSeparator() +
                "      received: 2022-12-08 14:14", "content");

        outlook.addRule("name", "/inbox/special",
                "subject-includes:    mjt   ", BETTER_PRIORITY);

        assertEquals(1, outlook.getMailsFromFolder("name", "/inbox/special").size());
        assertEquals(0, outlook.getMailsFromFolder("name", "/inbox").size());
    }

    @Test
    void testAddRuleChangesInboxWithPriority() {
        Outlook outlook = new Outlook();
        outlook.addNewAccount("name", "email");
        outlook.createFolder("name", "/inbox/special");
        outlook.createFolder("name", "/inbox/another");

        outlook.receiveMail("name", "sender: testy@gmail.com" +
                System.lineSeparator() +
                "      subject: Hello, mjt!" +
                System.lineSeparator() +
                "      recipients: pesho@gmail.com, gosho@gmail.com," +
                System.lineSeparator() +
                "      received: 2022-12-08 14:14", "content");

        outlook.addRule("name", "/inbox/special",
                "subject-includes: mjt", BETTER_PRIORITY);
        outlook.addRule("name", "/inbox/another",
                "subject-includes: mjt", WEAKER_PRIORITY);

        assertEquals(1, outlook.getMailsFromFolder("name", "/inbox/special").size());
        assertEquals(0, outlook.getMailsFromFolder("name", "/inbox/another").size());
    }

    @Test
    void testAddRuleChangesInboxMultipleConditions() {
        Outlook outlook = new Outlook();
        outlook.addNewAccount("name", "email");
        outlook.addNewAccount("name1", "email1");

        outlook.createFolder("name", "/inbox/special");

        outlook.receiveMail("name", "sender: testy@gmail.com" +
                System.lineSeparator() +
                "      subject: Hello, mjt  ,ok !" +
                System.lineSeparator() +
                "      recipients: pesho@gmail.com,email1, gosho@gmail.com," +
                System.lineSeparator() +
                "      received: 2022-12-08 14:14", "content");

        outlook.addRule("name", "/inbox/special",
                "subject-includes:    mjt , ok  "
                        + System.lineSeparator() +
                        "recipients-includes:   wrongEmail,   email1  ", BETTER_PRIORITY);

        assertEquals(1, outlook.getMailsFromFolder("name", "/inbox/special").size());
        assertEquals(0, outlook.getMailsFromFolder("name", "/inbox").size());
    }

    @Test
    void testAddRuleChangesInboxMultipleConditionsNegative() {
        Outlook outlook = new Outlook();
        outlook.addNewAccount("name", "email");
        outlook.createFolder("name", "/inbox/special");

        outlook.receiveMail("name", "sender: testy@gmail.com" +
                System.lineSeparator() +
                "      subject: Hello, mjt!" +
                System.lineSeparator() +
                "      recipients: pesho@gmail.com, gosho@gmail.com," +
                System.lineSeparator() +
                "      received: 2022-12-08 14:14", "content");

        outlook.addRule("name", "/inbox/special",
                "subject-includes:    mjt , ok  "
                        + System.lineSeparator() +
                        "recipients-includes:   wrongEmail,   email1  ", BETTER_PRIORITY);

        assertEquals(0, outlook.getMailsFromFolder("name", "/inbox/special").size());
        assertEquals(1, outlook.getMailsFromFolder("name", "/inbox").size());
    }

    @Test
    void testReceiveMailIllegalArgumentsName() {
        Outlook outlook = new Outlook();

        assertThrows(IllegalArgumentException.class,
                () -> outlook.receiveMail(null, "metadata", "mail content"));
        assertThrows(IllegalArgumentException.class,
                () -> outlook.receiveMail("", "metadata", "mail content"));
        assertThrows(IllegalArgumentException.class,
                () -> outlook.receiveMail(" ", "metadata", "mail content"));
    }

    @Test
    void testReceiveMailIllegalArgumentsMetadata() {
        Outlook outlook = new Outlook();

        assertThrows(IllegalArgumentException.class,
                () -> outlook.receiveMail("name", null, "mail content"));
        assertThrows(IllegalArgumentException.class,
                () -> outlook.receiveMail("name", "", "mail content"));
        assertThrows(IllegalArgumentException.class,
                () -> outlook.receiveMail("name", " ", "mail content"));
    }

    @Test
    void testReceiveMailIllegalArgumentsContent() {
        Outlook outlook = new Outlook();

        assertThrows(IllegalArgumentException.class,
                () -> outlook.receiveMail("name", "metadata", null));
        assertThrows(IllegalArgumentException.class,
                () -> outlook.receiveMail("name", "metadata", ""));
        assertThrows(IllegalArgumentException.class,
                () -> outlook.receiveMail("name", "metadata", " "));
    }

    @Test
    void testReceiveMailAccountNotFound() {
        Outlook outlook = new Outlook();

        assertThrows(AccountNotFoundException.class,
                () -> outlook.receiveMail("name", "metadata", "mail content"));
    }

    @Test
    void testReceiveMail() {
        Outlook outlook = new Outlook();
        outlook.addNewAccount("name", "email");

        outlook.receiveMail("name", "sender: testy@gmail.com" +
                System.lineSeparator() +
                "      subject: Hello, MJT!" +
                System.lineSeparator() +
                "      recipients: pesho@gmail.com, gosho@gmail.com," +
                System.lineSeparator() +
                "      received: 2022-12-08 14:14", "content");

        assertEquals(1, outlook.getPersonalFolder("name").getFolderByName("/inbox").mails().size());
    }

    //rules in conflict???????????
    @Test
    void testReceiveMailApplyRule() {
        Outlook outlook = new Outlook();
        outlook.addNewAccount("name", "email");

        outlook.createFolder("name", "/inbox/special");
        outlook.createFolder("name", "/inbox/another");

        outlook.receiveMail("name", "sender: testy@gmail.com" +
                System.lineSeparator() +
                "      subject: Hello, mjt!" +
                System.lineSeparator() +
                "      recipients: pesho@gmail.com, gosho@gmail.com," +
                System.lineSeparator() +
                "      received: 2022-12-08 14:14", "content");

        outlook.addRule("name", "/inbox/special",
                "subject-includes: mjt    ", BETTER_PRIORITY);
        outlook.addRule("name", "/inbox/another",
                "subject-includes: mjt   ", WEAKER_PRIORITY);

        assertEquals(1,
                outlook.getPersonalFolder("name").getFolderByName("/inbox/special").mails().size());
        assertEquals(0,
                outlook.getPersonalFolder("name").getFolderByName("/inbox").mails().size());
        assertEquals(0,
                outlook.getPersonalFolder("name").getFolderByName("/inbox/another").mails().size());

    }

    @Test
    void testGetMailsFromFolderIllegalArgumentsName() {
        Outlook outlook = new Outlook();

        assertThrows(IllegalArgumentException.class, () -> outlook.getMailsFromFolder(null, "/inbox"));
        assertThrows(IllegalArgumentException.class, () -> outlook.getMailsFromFolder("", "/inbox"));
        assertThrows(IllegalArgumentException.class, () -> outlook.getMailsFromFolder(" ", "/inbox"));
    }

    @Test
    void testGetMailsFromFolderIllegalArgumentsFolder() {
        Outlook outlook = new Outlook();

        assertThrows(IllegalArgumentException.class, () -> outlook.getMailsFromFolder("name", null));
        assertThrows(IllegalArgumentException.class, () -> outlook.getMailsFromFolder("name", ""));
        assertThrows(IllegalArgumentException.class, () -> outlook.getMailsFromFolder("name", " "));
    }

    @Test
    void testGetMailsFromFolderAccountNotFound() {
        Outlook outlook = new Outlook();

        assertThrows(AccountNotFoundException.class,
                () -> outlook.getMailsFromFolder("name", "/inbox"));
    }

    @Test
    void testGetMailsFromFolderNotFound() {
        Outlook outlook = new Outlook();
        outlook.addNewAccount("name", "email");

        assertThrows(FolderNotFoundException.class,
                () -> outlook.getMailsFromFolder("name", "/inbox/weird"));
    }

    @Test
    void testGetMailsFromFolder() {
        Outlook outlook = new Outlook();
        outlook.addNewAccount("name", "email");
        outlook.addNewAccount("name1", "testy@gmail.com");

        outlook.receiveMail("name", "sender: testy@gmail.com" +
                System.lineSeparator() +
                "      subject: Hello, MJT!" +
                System.lineSeparator() +
                "      recipients: pesho@gmail.com, gosho@gmail.com," +
                System.lineSeparator() +
                "      received: 2022-12-08 14:14", "content");

        Collection<Mail> actual = outlook.getMailsFromFolder("name", "/inbox");

        Collection<Mail> expected = List.of(new Mail(new Account("testy@gmail.com", "name1"),
                Set.of("pesho@gmail.com", "gosho@gmail.com"), "Hello, MJT!", "content",
                LocalDateTime.parse("2022-12-08 14:14", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));

        assertTrue(actual.size() == expected.size()
                && actual.containsAll(expected)
                && expected.containsAll(actual));
    }

    @Test
    void testSendMailIllegalArgumentName() {
        Outlook outlook = new Outlook();

        assertThrows(IllegalArgumentException.class,
                () -> outlook.sendMail(null, "metadata", "mail content"));
        assertThrows(IllegalArgumentException.class,
                () -> outlook.sendMail("", "metadata", "mail content"));
        assertThrows(IllegalArgumentException.class,
                () -> outlook.sendMail(" ", "metadata", "mail content"));
    }

    @Test
    void testSendMailIllegalArgumentMetadata() {
        Outlook outlook = new Outlook();

        assertThrows(IllegalArgumentException.class,
                () -> outlook.sendMail("name", null, "mail content"));
        assertThrows(IllegalArgumentException.class,
                () -> outlook.sendMail("name", "", "mail content"));
        assertThrows(IllegalArgumentException.class,
                () -> outlook.sendMail("name", " ", "mail content"));
    }

    @Test
    void testSendMailIllegalArgumentContent() {
        Outlook outlook = new Outlook();

        assertThrows(IllegalArgumentException.class,
                () -> outlook.sendMail("name", "metadata", null));
        assertThrows(IllegalArgumentException.class,
                () -> outlook.sendMail("name", "metadata", ""));
        assertThrows(IllegalArgumentException.class,
                () -> outlook.sendMail("name", "metadata", " "));
    }

    @Test
    void testSendMailAccountNotFound() {
        Outlook outlook = new Outlook();

        assertThrows(AccountNotFoundException.class,
                () -> outlook.sendMail("name", "      subject: Hello, MJT!" +
                        System.lineSeparator() +
                        "      recipients: email1, gosho@gmail.com," +
                        System.lineSeparator() +
                        "      received: 2022-12-08 14:14", "content"));
    }

    @Test
    void testSendMail() {
        Outlook outlook = new Outlook();
        outlook.addNewAccount("name", "email");
        outlook.addNewAccount("name1", "email1");

        outlook.sendMail("name",
                "      subject: Hello, MJT!" +
                        System.lineSeparator() +
                        "      recipients: email1, gosho@gmail.com," +
                        System.lineSeparator() +
                        "      received: 2022-12-08 14:14", "content");

        outlook.sendMail("name", "sender: anothername@gmail.com" +
                System.lineSeparator() +
                "      subject: Another Hello, MJT!" +
                System.lineSeparator() +
                "      recipients: email1, email" +
                System.lineSeparator() +
                "      received: 2022-12-08 14:14", "content1");

        assertEquals(1, outlook.getMailsFromFolder("name", "/inbox").size());
        assertEquals(EXPECTED_NUMBER, outlook.getMailsFromFolder("name", "/sent").size());
        assertEquals(EXPECTED_NUMBER, outlook.getMailsFromFolder("name1", "/inbox").size());
    }
}
