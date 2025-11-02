package test;

import java.util.Scanner;

public class UserInterfaceTest {

    private EmailServiceTest emailServiceTest;
    private Scanner scanner;
    private static final int PAGE_SIZE = 15;

    /**
     * Create the UI with a reference to the email service test class
     * @param emailServiceTest The service to use for email operations
     */
    public UserInterfaceTest(EmailServiceTest emailServiceTest) {
        this.emailServiceTest = emailServiceTest;
        this.scanner = new Scanner(System.in);
    }

    // MAIN APPLICATION LOOP

    /**
     * START APPLICATION
     */
    public void start() {
        System.out.println("╔═══════════════════════════════════════════════════╗");
        System.out.println("║         📧 EMAIL CLEANER APPLICATION 📧          ║");
        System.out.println("╚═══════════════════════════════════════════════════╝");
        System.out.println();

        if (!emailServiceTest.hasEmails()) {
            System.out.println("⚠️  No emails loaded. Please check your emails.txt file.");
            return;
        }

        System.out.println("✅ Loaded " + emailServiceTest.getEmailCount() + " emails");
        System.out.println();

        boolean running = true;

        /**
         * MAIN MENU LOOP:
         * Keeps running until user chooses to exit
         *
         * Pattern: Display menu → Get choice → Handle choice → Repeat
         */
    }
}
