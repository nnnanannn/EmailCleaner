package test;

import java.io.IOException;
import java.util.List;

public class TestEmail {
    public static void main(String[] args) {
        EmailTest testEmail = new EmailTest(1,
                "john@example.com",
                "jane@example.com",
                "Test Subject",
                "2025-11-01",
                "This is a test email body."
        );

//        System.out.println(testEmail);
//        System.out.println("Subject is: " + testEmail.getSubject());

        EmailServiceTest emailTest = new EmailServiceTest();

        try {
            emailTest.loadEmails("emails.txt");
            List<EmailTest> searchResult = emailTest.searchEmails(new String[]{"Meeting","drink"}, false);
            System.out.println(searchResult);
            emailTest.deleteMultipleEmails(new int[] {1, 2, 3});
            System.out.println();
            System.out.println(emailTest.canUndo());
            System.out.println(emailTest.getUndoCount());
            emailTest.undoLastDeletion();
            emailTest.undoLastDeletion();
            emailTest.undoLastDeletion();
            emailTest.saveEmails();
            System.out.println(emailTest.getEmailById(2));
            System.out.println(emailTest.getEmailCount());
            System.out.println(emailTest.hasEmails());

        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }

    }
}
