package bg.sofia.uni.fmi.mjt.mail;

import java.util.List;

public record EmailFolder(String path,List<Mail> mails) {
   public void addMail(Mail mail){
       mails.add(mail);
   }
}
