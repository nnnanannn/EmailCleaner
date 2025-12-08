import test.EmailServiceTest;
import test.EmailTest;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Stack;

public class UserInterface {

    private EmailServiceTest emailServiceTest;
    private Scanner scanner;
    private static final int PAGE_SIZE = 15;

    /**
     * Create the UI with a reference to the email service test class
     * @param emailServiceTest The service to use for email operations
     */
    public UserInterface(EmailServiceTest emailServiceTest) {
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
        while (running) {
            displayMainMenu();
            int choice = getIntInput("Enter your choice: ", 1, 4);
            switch (choice) {
                case 1:
                    handleSearch();
                    break;
                case 2:
                    handledViewAll();
                case 3:
                    handleUndo();
                default:
                    break;

            }
        }
    }

    // ============================================
    // MENU DISPLAY
    // ============================================

    /**
     * Display the main menu
     */
    private void displayMainMenu() {
        System.out.println("\n┌─────────────────────────────────────────────────┐");
        System.out.println("│                   MAIN MENU                     │");
        System.out.println("├─────────────────────────────────────────────────┤");
        System.out.println("│  1. 🔍 Search and Delete Emails by Keywords    │");
        System.out.println("│  2. 📋 View All Emails                          │");
        System.out.println("│  3. ↩️  Undo Last Deletion                       │");
        System.out.println("│  4. 🚪 Save and Exit                            │");
        System.out.println("└─────────────────────────────────────────────────┘");
    }

    // ============================================
    // SEARCH FUNCTIONALITY
    // ============================================

    private void handleSearch() {
        System.out.println("\n═══════════════════════════════════════════════════");
        System.out.println("              SEARCH EMAILS BY KEYWORDS            ");
        System.out.println("═══════════════════════════════════════════════════");

        // Get keywords from user
        System.out.print("\n💬 Enter keywords (separate with commas): ");
        String keywordInput = scanner.nextLine().trim();

        if (keywordInput.isEmpty()) {
            System.out.println("⚠\uFE0F  No keywords entered. Returning to main menu.");
            return;
        }

        String[] keywords = keywordInput.split("\\s*,\\s*");

        // Ask about search scope
        System.out.println("\n📍 Search scope:");
        System.out.println("   1. Subject only (faster)");
        System.out.println("   2. Subject + Body (more thorough)");
        int scopeChoice = getIntInput("Choose option (1 or 2): ", 1, 2);
        boolean searchInBody = false;
        if (scopeChoice == 2) {
            searchInBody = true;
        }

        // Perform the search
        System.out.println("\n🔍 Searching...");
        List<EmailTest> results = emailServiceTest.searchEmails(keywords, searchInBody);

        // Display results
        if (results.isEmpty()) {
            System.out.println("\n❌ No emails found matching those keywords.");
            return;
        }

        System.out.println("\n✅ Found " + results.size() + " matching email(s)");

        // Show results with pagination and deletion options
        displaySearchResults(results);
    }

    // Display and Pagination
    private void displaySearchResults(List<EmailTest> resultEmails) {
        int currentPage = 0;
        int totalPages = (int) Math.ceil((double) resultEmails.size() / PAGE_SIZE);

        boolean viewing = true;

        while (viewing) {
            // Clear screen effect
            System.out.println("\n\n");

            // Calculate range for current page
            int startIndex = currentPage * PAGE_SIZE;
            int endIndex = Math.min(startIndex + PAGE_SIZE, resultEmails.size());

            // Display header
            System.out.println("═══════════════════════════════════════════════════════════════");
            System.out.printf("  SEARCH RESULTS - Page %d of %d (Showing %d-%d of %d)%n",
                    currentPage + 1, totalPages, startIndex + 1, endIndex, resultEmails.size());
            System.out.println("═══════════════════════════════════════════════════════════════");

            // Display emails on current page
            for (int i = startIndex; i < endIndex; i++) {
                EmailTest emailTest = resultEmails.get(i);
                System.out.println(emailTest);
            }

            // Display pagination menu
            displayPaginationMenu(currentPage, totalPages);

            String choice = scanner.nextLine().trim().toLowerCase();

            switch (choice) {
                case "n": // Next page
                    if (currentPage < totalPages -1 ) {
                        currentPage++;
                    } else {
                        System.out.println("⚠\uFE0F  Already on last page.");
                        pause();
                    }
                    break;

                case "p": // Previous page
                    if (currentPage > 0) {
                        currentPage--;
                    } else {
                        System.out.println("⚠️  Already on first page.");
                        pause();
                    }
                    break;

                case "d": // Delete
                    handleDeleteFromResults(resultEmails, startIndex, endIndex);
                    // Update total pages in case emails were deleted
                    totalPages = (int) Math.ceil((double) resultEmails.size() / PAGE_SIZE);
                    // Adjust current page if needed
                    if (currentPage >= totalPages && totalPages > 0) {
                        currentPage = totalPages - 1;
                    }
                    if (resultEmails.isEmpty()) {
                        System.out.println("✅ All matching emails deleted.");
                        viewing = false;
                    }
                    break;

                case "b": // Back to main menu
                    viewing = false;
                    break;

                default:
                    System.out.println("⚠️  Invalid choice. Please try again.");
                    pause();
            }
        }
    }

    // Display the pagination menu
    private void displayPaginationMenu(int currentPage, int totalPages) {
        System.out.println("\n┌─────────────────────────────────────────────────┐");
        System.out.println("│                 NAVIGATION                      │");
        System.out.println("├─────────────────────────────────────────────────┤");

        if (currentPage < totalPages - 1) {
            System.out.println("│  [N] Next Page                                  │");
        }
        if (currentPage > 0) {
            System.out.println("│  [P] Previous Page                              │");
        }

        System.out.println("│  [D] Delete temp.Email(s) from Current Page          │");
        System.out.println("│  [B] Back to temp.Main Menu                          │");
        System.out.println("└─────────────────────────────────────────────────┘");
        System.out.print("\n💬 Your choice: ");
    }

    private void handleDeleteFromResults(List<EmailTest> resultEmails, int startIndex, int endIndex) {
        System.out.println("\n┌─────────────────────────────────────────────────┐");
        System.out.println("│              DELETE CONFIRMATION                │");
        System.out.println("└─────────────────────────────────────────────────┘");

        System.out.println("\n💡 Enter the IDs of emails you want to delete");
        System.out.println("   (separate multiple IDs with commas, e.g., 5,12,18)");
        System.out.println("   Or press Enter to cancel");

        // Show compact list of current page
        System.out.println("\n📋 Emails on this page:");
        for (int i = startIndex; i < endIndex; i++) {
            EmailTest email = resultEmails.get(i);
            System.out.printf("   [ID: %d] %s - %s%n",
                    email.getId(), email.getFrom(), email.getSubject());
        }

        System.out.print("\n💬 IDs to delete: ");
        String input = scanner.nextLine().trim();

        if (input.isEmpty()) {
            System.out.println("❌ Cancelled.");
            pause();
            return;
        }

        // Parse IDs
        try {
            String[] idStrings = input.split("\\s*,\\s*");
            int[] ids = new int[idStrings.length];

            for (int i = 0; i < idStrings.length; i++) {
                ids[i] = Integer.parseInt(idStrings[i]);
            }

            // Confirm deletion
            System.out.print("\n⚠️  Are you sure you want to delete " + ids.length + " email(s)? (yes/no): ");
            String confirm = scanner.nextLine().trim().toLowerCase();

            if (!confirm.equals("yes") && !confirm.equals("y")) {
                System.out.println("❌ Cancelled.");
                pause();
                return;
            }

            saveDeletedEmails(ids);

            // Perform deletion
            int deletedCount = emailServiceTest.deleteMultipleEmails(ids);

            // Remove from results list too
            resultEmails.removeIf(email -> {
                for (int id : ids) {
                    if (email.getId() == id)
                        return true;
                }
                return false;
            });

            System.out.print("\n✅ Successfully deleted " + deletedCount + " email(s)");
            System.out.print(" (id: ");
            for (int i = 0; i <= ids.length - 1; i++) {
                if (i == ids.length - 1) {
                    System.out.print(ids[i] + ")\n");
                    break;
                }
                System.out.print(ids[i] + ", ");
            }

            System.out.println("💡 You can undo this from the main menu");
            pause();

        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid input. Please enter valid numbers.");
            pause();
        }

    }

    // Save deleted emails
    private void saveDeletedEmails(int[] deletedEmailIds) {

    }

    private void displayDeleteEmails(int[] deletedEmailIds) {
        for (int i = 0; i <deletedEmailIds.length; i++) {
            System.out.println();
        }
    }

    // View all emails
    private void handledViewAll() {
        System.out.println("\n═══════════════════════════════════════════════════");
        System.out.println("                 ALL EMAILS                        ");
        System.out.println("═══════════════════════════════════════════════════");

        List<EmailTest> allEmails = emailServiceTest.getAllEmails();

        if (allEmails.isEmpty()) {
            System.out.println("\n📭 No emails to display.");
            pause();
            return;
        }
        displaySearchResults(new ArrayList<>(allEmails));
    }

    // Undo functionality
    private void handleUndo() {
        System.out.println("\n═══════════════════════════════════════════════════");
        System.out.println("                 UNDO DELETION                     ");
        System.out.println("═══════════════════════════════════════════════════");

        if (!emailServiceTest.canUndo()) {
            System.out.println("\n⚠️  Nothing to undo. No emails have been deleted.");
            pause();
            return;
        }

        int undoCount = emailServiceTest.getUndoCount();
        System.out.println("\n💡 You have " + undoCount + " deletion(s) that can be undone.");
        System.out.println("   Deleted emails: ");

        Stack<EmailTest> savedDeletedEmails = emailServiceTest.getDeletedEmails();
        int deletedEmailsCounter = 1;
        while (!savedDeletedEmails.empty()) {
            System.out.println(deletedEmailsCounter + ") ID:" +
                    (savedDeletedEmails.pop()).getSubject());
            deletedEmailsCounter++;
        }

        System.out.print("   Undo the last deletion? (yes/no): ");

        String confirm = scanner.nextLine().trim().toLowerCase();

        if (confirm.equals("yes") || confirm.equals("y")) {
            if (emailServiceTest.undoLastDeletion()) {
                System.out.println("\n✅ Last deletion undone successfully!");
            }
        } else {
            System.out.println("❌ Cancelled.");
        }

        pause();
    }

    // Handle exit with save confirmation
    private void handleExit() {
        System.out.println("\n═══════════════════════════════════════════════════");
        System.out.println("                   EXIT                            ");
        System.out.println("═══════════════════════════════════════════════════");

        System.out.print("\n💾 Save changes before exiting? (yes/no): ");
        String confirm = scanner.nextLine().trim().toLowerCase();

        if (confirm.equals("yes") || confirm.equals("y")) {
            if (emailServiceTest.saveEmails()) {
                System.out.println("\n✅ Changes saved successfully!");
            } else {
                System.out.println("\n⚠️  Failed to save changes.");
            }
        } else {
            System.out.println("\n⚠️  Changes discarded.");
        }
        System.out.println("\n👋 Thank you for using temp.Email Cleaner!");
        System.out.println("═══════════════════════════════════════════════════\n");
    }

    private int getIntInput(String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);

            if (scanner.hasNextInt()) {
                int value = scanner.nextInt();
                scanner.nextLine();

                if (value >= min && value <= max) {
                    return value;
                } else {
                    System.out.printf("⚠️  Please enter a number between %d and %d.%n", min, max);
                }
            } else {
                scanner.nextLine();
                System.out.println("⚠️  Invalid input. Please enter a number.");
            }
        }
    }

    private void pause() {
        System.out.print("\n⏸️  Press Enter to continue...");
        scanner.nextLine();
    }

}
