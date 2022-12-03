package bg.sofia.uni.fmi.mjt.mail;

import java.util.List;

public record PersonalEmail(Account account, List<EmailFolder>folders,List<Rule>rules) {
}
