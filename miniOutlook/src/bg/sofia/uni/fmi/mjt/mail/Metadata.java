package bg.sofia.uni.fmi.mjt.mail;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class Metadata {
    private String[] metadata;
    public Metadata(String _metadata){
        metadata=_metadata.split(System.lineSeparator());
    }

    public String getSenderEmail() {
        for (String line : metadata) {
            if (line.contains("sender:")) {
                return line.substring(line.indexOf(":" + 1)).strip();
            }
        }
        return null;
    }

    public String getSubject() {
        for (String line : metadata) {

            if (line.contains("subject:")) {
                return line.substring(line.indexOf(":" + 1)).strip();
            }
        }
        return null;
    }

    public String[] getRecipients() {
        for (String line : metadata) {
            if (line.contains("recipients:")) {
           return line.substring(line.indexOf(":" + 1)).split(",");
            }
        }
        return null;
    }

    public LocalDateTime getReceived() {
        for (String line : metadata) {
            if (line.contains("received:")) {
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                return LocalDateTime.parse(line.substring(line.indexOf(":" + 1)), dateTimeFormatter);
            }
        }
        return null;
    }
/*public String[] changeSender(){

}*/


   /* private Mail readMail(String metaData, String content) {
        String[] data = metaData.split(System.lineSeparator());

        Account sender = null;
        Set<String> recipients = null;
        String subject = null;
        LocalDateTime received = null;

        for (String line : data) {
            if (line.contains("sender:")) {
                sender = Outlook.getAccountByEmail(line.substring(line.indexOf(":" + 1)));
            }
            if (line.contains("subject:")) {
                subject = line.substring(line.indexOf(":" + 1));
            }
            if (line.contains("recipients:")) {
                String[] recip = line.substring(line.indexOf(":" + 1)).split(",");
                recipients.addAll(List.of(recip));
            }
            if (line.contains("received:")) {
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                received = LocalDateTime.parse(line.substring(line.indexOf(":" + 1)), dateTimeFormatter);
            }
        }
        return new Mail(sender, recipients, subject, content, received);
    }*/

}
