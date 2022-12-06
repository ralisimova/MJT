package bg.sofia.uni.fmi.mjt.mail;

import bg.sofia.uni.fmi.mjt.mail.exceptions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;


public class TestOutlook {

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
        assertThrows(AccountAlreadyExistsException.class, () -> outlook.addNewAccount("name", "email2"));
    }

    @Test
    void testAddNewAccount() {
        Outlook outlook = new Outlook();
        outlook.addNewAccount("name", "email");
        outlook.addNewAccount("name1", "email1");

        Set<Account> testSet = new HashSet<>(List.of(new Account("email", "name"),
                new Account("email1", "name1")));
        assertTrue(testSet.containsAll(outlook.getAccounts()) && outlook.getAccounts().containsAll(testSet));
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
        assertThrows(InvalidPathException.class, () -> outlook.createFolder("name", "impossible/path"));
        assertThrows(InvalidPathException.class, () -> outlook.createFolder("name", "/inbox/path/path_doesnt_exist"));

    }

    @Test
    void testCreateFolderAlreadyExist() {
        Outlook outlook = new Outlook();
        outlook.addNewAccount("name", "email");
        outlook.createFolder("name", "/inbox/path");
        assertThrows(FolderAlreadyExistsException.class, () -> outlook.createFolder("name", "/inbox/path"));
    }

    @Test
    void testCreateFolder() {
        Outlook outlook = new Outlook();
        outlook.addNewAccount("name", "email");
        assertEquals(2, outlook.getPersonalFolders("name").size());

        outlook.createFolder("name", "/inbox/path");
        outlook.createFolder("name", "/inbox/path/next");

        assertEquals(4, outlook.getPersonalFolders("name").size());
    }

    @Test
    void testAddRuleInvalidName() {
        Outlook outlook = new Outlook();
        assertThrows(IllegalArgumentException.class,
                () -> outlook.addRule(null, "path", "definition", 5));
        assertThrows(IllegalArgumentException.class,
                () -> outlook.addRule("", "path", "definition", 5));
        assertThrows(IllegalArgumentException.class,
                () -> outlook.addRule(" ", "path", "definition", 5));

        assertThrows(IllegalArgumentException.class,
                () -> outlook.addRule("name", null, "definition", 5));
        assertThrows(IllegalArgumentException.class,
                () -> outlook.addRule("name", "", "definition", 5));
        assertThrows(IllegalArgumentException.class,
                () -> outlook.addRule("name", " ", "definition", 5));

        assertThrows(IllegalArgumentException.class,
                () -> outlook.addRule("name", "path", null, 5));
        assertThrows(IllegalArgumentException.class,
                () -> outlook.addRule("name", "path", "", 5));
        assertThrows(IllegalArgumentException.class,
                () -> outlook.addRule(null, "path", " ", 5));

        assertThrows(IllegalArgumentException.class,
                () -> outlook.addRule("name", "path", "definition", 11));
        assertThrows(IllegalArgumentException.class,
                () -> outlook.addRule("name", "path", "definition", 0));
    }

    @Test
    void testAddRuleAccountNotFound() {
        Outlook outlook = new Outlook();
        assertThrows(AccountNotFoundException.class,
                () -> outlook.addRule("name", "path", "definition", 5));
    }

    @Test
    void testAddRuleFolderNotFound() {
        Outlook outlook = new Outlook();
        outlook.addNewAccount("name", "email");
        assertThrows(FolderNotFoundException.class,
                () -> outlook.addRule("name", "/inbox/path", "definition", 5));

    }

    @Test
    void testAddRuleAlreadyDefined() {
        Outlook outlook = new Outlook();
        outlook.addNewAccount("name", "email");
        assertThrows(RuleAlreadyDefinedException.class,
                () -> outlook.addRule("name", "/inbox", "subject-includes: mjt, izpit, 2022" +
                "subject-includes: mjt, something, else", 5));
    }

    @Test
    void testAddRule() {
        Outlook outlook = new Outlook();
        outlook.addNewAccount("name", "email");
        outlook.addRule("name", "/inbox", "subject-includes: mjt, izpit, 2022"
                + System.lineSeparator() +
                "subject-or-body-includes: mjt, something, else", 5);
        outlook.addRule("name", "/inbox", "recipients-includes:stoyo@fmi.bg,stoyo1@fmi.bg", 5);

        List<Rule> rules = outlook.getPersonalEmailByName("name").getRules();

        assertEquals(3, rules.size());

        List<Rule> expected = List.of(new Rule("subject-includes: mjt, izpit, 2022", 5, "/inbox"),
                new Rule("subject-or-body-includes: mjt, something, else", 5, "/inbox"),
                new Rule("recipients-includes:stoyo@fmi.bg,stoyo1@fmi.bg", 5, "/inbox"));
        assertTrue(expected.size() == rules.size() && expected.containsAll(rules) && rules.containsAll(expected));
    }

}
