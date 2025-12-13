import java.util.List;

public class Main {

    public static void main(String[] args) {
        EmailService emailService = new EmailService();
        String emailFilePath = "EmailCleaner/emails.txt";
        emailService.loadEmails(emailFilePath);

        UserInterface ui = new UserInterface(emailService);
        ui.start();
    }

    private void testRead() {

        EmailService emailService = new EmailService();
        String emailFilePath = "emails.txt";
        emailService.loadEmails(emailFilePath);

        String[] keywords = {"modules", "friend"};
        List<Email> results = emailService.searchEmails(keywords, true);
        if (!results.isEmpty()) {
            for (int i = 0; i < results.size(); i++) {
                System.out.println(results.get(i));
            }

        }
    }
}
