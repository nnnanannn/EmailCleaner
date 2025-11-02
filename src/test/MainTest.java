package test;

public class MainTest {

    public static void main(String[] args) {
        EmailServiceTest emailServiceTest = new EmailServiceTest();
        String emailFilePath = "emails.txt";
        emailServiceTest.loadEmails(emailFilePath);

        UserInterfaceTest ui = new UserInterfaceTest(emailServiceTest);
        ui.start();
    }
}
