import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UserInterface {

    private EmailService emailService;
    private Scanner scanner;
    private static final int PAGE_SIZE = 15;

    /**
     * Create the UI with a reference to the email service test class
     * @param emailService The service to use for email operations
     */
    public UserInterface(EmailService emailService) {
        this.emailService = emailService;
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

        if (!emailService.hasEmails()) {
            System.out.println("⚠️  No emails loaded. Please check your emails.txt file.");
            return;
        }

        System.out.println("✅ Loaded " + emailService.getEmailCount() + " emails");
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
                    handleViewAll();
                    break;
                case 3:
                    handleUndo();
                    break;
                case 4:
                    handleExit();
                    running = false;
                    break;
            }
        }
        scanner.close();
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
        boolean searchInBody = (scopeChoice == 2);

        // Perform the search
        System.out.println("\n🔍 Searching...");
        List<Email> results = emailService.searchEmails(keywords, searchInBody);

        // Display results
        if (results.isEmpty()) {
            System.out.println("\n❌ No emails found matching those keywords.");
            return;
        }

        System.out.println("\n✅ Found " + results.size() + " matching email(s)");

        // Show results with pagination and deletion options
        displaySearchResults(results, keywords);
    }

    // Display and Pagination
    private void displaySearchResults(List<Email> emails, String[] keywords) {
        int currentPage = 0;
        int totalPages = (int) Math.ceil((double) emails.size() / PAGE_SIZE);

        boolean viewing = true;

        while (viewing && !emails.isEmpty()) {
            // Clear screen effect
            System.out.println("\n\n");

            // Calculate range for current page
            int startIndex = currentPage * PAGE_SIZE;
            int endIndex = Math.min(startIndex + PAGE_SIZE, emails.size());

            // Display header
            System.out.println("═══════════════════════════════════════════════════════════════");
            System.out.printf("  SEARCH RESULTS - Page %d of %d (Showing %d-%d of %d)%n",
                    currentPage + 1, totalPages, startIndex + 1, endIndex, emails.size());
            System.out.println("═══════════════════════════════════════════════════════════════");

            // Display emails on current page
            for (int i = startIndex; i < endIndex; i++) {
                Email email = emails.get(i);
                System.out.println(email);
            }

            // Display pagination menu
            displayPaginationMenu(currentPage, totalPages, emails.size());

            String choice = scanner.nextLine().trim().toLowerCase();

            switch (choice) {
                case "n": // Next page
                    if (currentPage < totalPages -1 ) {
                        currentPage++;
                    } else {
                        System.out.println("⚠️ Already on last page.");
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

                case "d": // Delete specific emails
                    handleDeleteFromResults(emails, startIndex, endIndex);
                    // Update total pages in case emails were deleted
                    totalPages = (int) Math.ceil((double) emails.size() / PAGE_SIZE);
                    // Adjust current page if needed
                    if (currentPage >= totalPages && totalPages > 0) {
                        currentPage = totalPages - 1;
                    }
                    if (emails.isEmpty()) {
                        System.out.println("✅ All matching emails deleted.");
                        viewing = false;
                    }
                    break;

                case "a":  // Delete ALL matching emails
                    handleDeleteAll(emails, keywords);
                    viewing = false;  // Exit after deleting all
                    break;

                case "f":  // Delete FIRST N emails
                    handleDeleteFirstN(emails, keywords);
                    totalPages = (int) Math.ceil((double) emails.size() / PAGE_SIZE);
                    if (currentPage >= totalPages && totalPages > 0) {
                        currentPage = totalPages - 1;
                    }
                    if (emails.isEmpty()) {
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
    private void displayPaginationMenu(int currentPage, int totalPages, int totalResults) {
        System.out.println("\n┌─────────────────────────────────────────────────┐");
        System.out.println("│                 NAVIGATION                      │");
        System.out.println("├─────────────────────────────────────────────────┤");

        if (currentPage < totalPages - 1) {
            System.out.println("│  [N] Next Page                                  │");
        }
        if (currentPage > 0) {
            System.out.println("│  [P] Previous Page                              │");
        }

        System.out.println("│                                                 │");
        System.out.println("│  DELETION OPTIONS:                              │");
        System.out.println("│  [D] Delete Email(s) from Current Page          │");
        System.out.println("│  [A] Delete ALL " + String.format("%-2d", totalResults) + " Matching Emails           │");
        System.out.println("│  [F] Delete First N Matching Emails             │");
        System.out.println("│                                                 │");
        System.out.println("│  [B] Back to Main Menu                          │");
        System.out.println("└─────────────────────────────────────────────────┘");
        System.out.print("\n💬 Your choice: ");
    }

    private void handleDeleteAll(List<Email> emails, String[] keywords) {
        System.out.println("\n┌─────────────────────────────────────────────────┐");
        System.out.println("│            DELETE ALL CONFIRMATION              │");
        System.out.println("└─────────────────────────────────────────────────┘");

        System.out.println("\n⚠️  WARNING: You are about to delete " + emails.size() + " email(s)!");
        System.out.println("   Keywords: " + String.join(", ", keywords));
        System.out.println("\n💡 You can undo this action from the main menu.");

        System.out.print("\n⚠️  Are you SURE you want to delete ALL " + emails.size() + " emails? (yes/no): ");
        String confirm = scanner.nextLine().trim().toLowerCase();

        if (!confirm.equals("yes") && !confirm.equals("y")) {
            System.out.println("❌ Cancelled. No emails were deleted.");
            pause();
            return;
        }

        // Create description for undo history
        String description = String.format("Deleted all %d email(s) matching '%s'",
                emails.size(),
                String.join(", ", keywords)
        );

        // Delete all emails as ONE action
        int deletedCount = emailService.deleteAllEmails(new ArrayList<>(emails), description);

        if (deletedCount > 0) {
            // Clear the results list since we deleted them
            emails.clear();

            System.out.println("\n✅ Successfully deleted " + deletedCount + " email(s)");
            System.out.println("💡 You can undo this action from the main menu");
        }

        pause();
    }

    // Delete First N emails
    private void handleDeleteFirstN(List<Email> emails, String[] keywords) {
        System.out.println("\n┌─────────────────────────────────────────────────┐");
        System.out.println("│          DELETE FIRST N EMAILS                  │");
        System.out.println("└─────────────────────────────────────────────────┘");

        System.out.println("\n💡 You have " + emails.size() + " matching email(s)");
        System.out.println("   How many would you like to delete (from the beginning)?");

        int count = getIntInput("\n💬 Number to delete (1-" + emails.size() + "): ", 1, emails.size());

         // Show which emails will be deleted
        System.out.println("\n📋 First " + count + " email(s) that will be deleted:");
        for (int i = 0; i < count && i < emails.size(); i++) {
            Email email = emails.get(i);
            System.out.printf("   %d. [ID: %d] %s - %s%n",
                    i + 1, email.getId(), email.getFrom(), email.getSubject());
        }

        System.out.print("\n⚠️  Confirm deletion of these " + count + " email(s)? (yes/no): ");
        String confirm = scanner.nextLine().trim().toLowerCase();

        if (!confirm.equals("yes") && !confirm.equals("y")) {
            System.out.println("❌ Cancelled. No emails were deleted.");
            pause();
            return;
        }

        // Create description for undo history
        String description = String.format("Deleted first %d email(s) matching '%s'",
                count,
                String.join(", ", keywords)
        );

        // Delete first N emails as ONE action
        int deletedCount = emailService.deleteFirstN(emails, count, description);

        if (deletedCount > 0) {
            // Remove deleted emails from the results list
            for (int i = 0; i < deletedCount; i++) {
                emails.remove(0);
            }

            System.out.println("\n✅ Successfully deleted " + deletedCount + " email(s)");
            System.out.println("💡 You can undo this action from the main menu");
        }

        pause();

    }

    private void handleDeleteFromResults(List<Email> emails, int startIndex, int endIndex) {
        System.out.println("\n┌─────────────────────────────────────────────────┐");
        System.out.println("│              DELETE CONFIRMATION                │");
        System.out.println("└─────────────────────────────────────────────────┘");

        System.out.println("\n💡 Enter the IDs of emails you want to delete");
        System.out.println("   (separate multiple IDs with commas, e.g., 5,12,18)");
        System.out.println("   Or press Enter to cancel");

        // Show compact list of current page
        System.out.println("\n📋 Emails on this page:");
        for (int i = startIndex; i < endIndex; i++) {
            Email email = emails.get(i);
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
            int deletedCount = emailService.deleteMultipleEmails(ids);

            // Remove from results list too
            emails.removeIf(email -> {
                for (int id : ids) {
                    if (email.getId() == id)
                        return true;
                }
                return false;
            });

            System.out.println("\n✅ Successfully deleted " + deletedCount + " email(s)");
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
    private void handleViewAll() {
        System.out.println("\n═══════════════════════════════════════════════════");
        System.out.println("                 ALL EMAILS                        ");
        System.out.println("═══════════════════════════════════════════════════");

        List<Email> allEmails = emailService.getAllEmails();

        if (allEmails.isEmpty()) {
            System.out.println("\n📭 No emails to display.");
            pause();
            return;
        }
        displaySearchResults(new ArrayList<>(allEmails), new String[]{"all"});
    }

    // Undo functionality
    private void handleUndo() {
        System.out.println("\n═══════════════════════════════════════════════════");
        System.out.println("                 UNDO DELETION                     ");
        System.out.println("═══════════════════════════════════════════════════");

        if (!emailService.canUndo()) {
            System.out.println("\n⚠️  Nothing to undo. No emails have been deleted.");
            pause();
            return;
        }

        // Show undo history
        System.out.println("\n📜 DELETION HISTORY (Most Recent First):");
        System.out.println("─────────────────────────────────────────────────");

        List<DeletionAction> history = emailService.getDeletionHistory();
        for (int i = 0; i < Math.min(5, history.size()); i++) {
            DeletionAction action = history.get(i);
            System.out.printf("%d. %s%n", i + 1, action.toString());
        }

        if (history.size() > 5) {
            System.out.println("   ... and " + (history.size() - 5) + " more action(s)");
        }

        System.out.println("─────────────────────────────────────────────────");

        // Show what will be undone
        String nextUndo = emailService.getLastActionDescription();
        System.out.println("\n💡 Next undo will restore: " + nextUndo);

        System.out.print("\n⚠️  Undo this deletion action? (yes/no): ");
        String confirm = scanner.nextLine().trim().toLowerCase();

        if (confirm.equals("yes") || confirm.equals("y")) {
            if (emailService.undoLastAction()) {
                System.out.println("\n✅ Action undone successfully!");
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
            if (emailService.saveEmails()) {
                System.out.println("\n✅ Changes saved successfully!");
            } else {
                System.out.println("\n⚠️  Failed to save changes.");
            }
        } else {
            System.out.println("\n⚠️  Changes discarded.");
        }
        System.out.println("\n👋 Thank you for using Email Cleaner!");
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
