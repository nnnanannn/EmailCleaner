package test;

import com.sun.tools.javac.Main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainTest {

    public static void main(String[] args) {
        EmailServiceTest emailServiceTest = new EmailServiceTest();
        String emailFilePath = "emails.txt";
        emailServiceTest.loadEmails(emailFilePath);

        UserInterfaceTest ui = new UserInterfaceTest(emailServiceTest);
        ui.start();

        //MainTest test = new MainTest();
        //test.testRead();

    }

    private void testRead() {

        EmailServiceTest emailServiceTest = new EmailServiceTest();
        String emailFilePath = "emails.txt";
        emailServiceTest.loadEmails(emailFilePath);

        String[] keywords = {"modules", "friend"};
        List<EmailTest> results = emailServiceTest.searchEmails(keywords, true);
        if (!results.isEmpty()) {
            for (int i = 0; i < results.size(); i++) {
                System.out.println(results.get(i));
            }

        }
    }
}
