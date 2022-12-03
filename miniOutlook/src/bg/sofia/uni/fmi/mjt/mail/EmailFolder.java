package bg.sofia.uni.fmi.mjt.mail;

import java.util.List;

public record EmailFolder(String path,List<Rule>rules,List<Mail> mails) {
}
