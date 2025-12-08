import java.io.*;
import java.util.*;

public class EmailService {

    /**
     * ArrayList<EmailTest> emails - temp.Main list of all emails
     * Stack<temp.Email> deletedEmails - Stack for undo functionality
     * String filePath - Path to the emails file
     */
    private ArrayList<Email> emails;
    private Stack<DeletionAction> deletionHistory;
    private String filePath;

    /**
     * Initialize with empty collection
     */
    public EmailService() {
        this.emails = new ArrayList<>();
        this.deletionHistory = new Stack<>();
    }

    /**
     * emails.txt file loading
     * @param filePath Path to the emails.txt file
     */
    public void loadEmails(String filePath) {
        this.filePath = filePath;

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {

            // Temporary variables to build each email
            int id = 0;
            String from = "";
            String to = "";
            String subject = "";
            String date = "";
            StringBuilder body = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                // Check if we have reached the end of an email
                if (line.equals("---")) {
                    // Create and add the email to the list
                    Email email = new Email(id, from, to, subject, date, body.toString());
                    emails.add(email);

                    // Reset for next email
                    body = new StringBuilder();
                } else if (line.startsWith("ID: ")) {
                    id = Integer.parseInt(line.substring(4));
                } else if (line.startsWith("From: ")) {
                    from = line.substring(6);
                } else if (line.startsWith("To: ")) {
                    to = line.substring(4);
                } else if (line.startsWith("Subject: ")) {
                    subject = line.substring(9);
                } else if (line.startsWith("Date: ")) {
                    date = line.substring(6);
                } else if (line.startsWith("Body: ")) {
                    body.append(line.substring(6));
                } else if (!line.isEmpty()) {
                    body.append(" ").append(line);
                }
            }

            System.out.println("✅ Successfully loaded " + emails.size() + " emails from " + filePath);

        } catch (FileNotFoundException e) {
            System.err.println("❌ Error: Could not find file '" + filePath + "'");
            System.err.println("   Please make sure the file exists in the project directory.");
        } catch (IOException e) {
            System.err.println("❌ Error reading file: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("❌ Error: Invalid email format in file (bad ID number)");
        }
    }

    /**
     * temp.Email Searching
     * @param keywords Array of keywords to search for
     * @param searchInBody If true, search in both subject and body; if false, subject only
     * @return List of emails that match any of the keywords
     *
     */
    public List<Email> searchEmails(String[] keywords, boolean searchInBody) {

        List<Email> results = new ArrayList<>();

        for (Email email : emails) {
            // Build the text to search in
            String searchText;

            if (searchInBody) {
                // Search in both subject and body
                searchText = email.getSubject() + " " + email.getBody();
            } else {
                // Search in subject only
                searchText = email.getSubject();
            }

            // Convert to lowercase for case-insensitive search
            searchText = searchText.toLowerCase();

            // Check if any keyword matches
            boolean found = false;
            for (String keyword : keywords) {
                if (searchText.contains(keyword.toLowerCase())) {
                    found = true;
                    break; // No need to check other keywords for this email
                }
            }

            // If keyword has already been found - found = true
            if (found) {
                results.add(email);
            }
        }
        if (results.isEmpty()) {
            System.out.println("No email with keywords: " + Arrays.toString(keywords) + " found.");
        } else {System.out.println("Found: " + results.size() + " emails with keyword: " + Arrays.toString(keywords));}
        return results;
    }

    /**
     * Email Deletion By ID
     * @param id The ID of the email to delete
     * @return true if deleted, false if not found
     */
    public boolean deleteEmailById(int id) {
        Iterator<Email> iterator = emails.iterator();

        while (iterator.hasNext()) {
            Email email = iterator.next();

            if (email.getId() == id) {
                // Create a deletion action with description
                String description = "Deleted email ID " + id;
                DeletionAction action = new DeletionAction(email, description);

                // Save to history BEFORE removing
                deletionHistory.push(action);

                // Remove from main list
                iterator.remove();

                System.out.println("✅ Deleted email: " + email.getSubject());
                return true;
            }
        }

        System.out.println("❌ temp.Email with ID " + id + " not found.");
        return false;
    }

    /**
     * Multiple Emails Deletion at once
     * @param ids Array of email IDs to delete
     * @return Number of emails successfully deleted
     */
    public int deleteMultipleEmails(int[] ids) {
        List<Email> deletedInThisAction = new ArrayList<>();

        // Collect all emails to delete
        for (int id : ids) {
            Iterator<Email> iterator = emails.iterator();

            while (iterator.hasNext()) {
                Email email = iterator.next();

                if (email.getId() == id) {
                    deletedInThisAction.add(email);
                    iterator.remove();
                    break;
                }
            }
        }

        // Create ONE deletion action for all deleted emails
        if (!deletedInThisAction.isEmpty()) {
            String description = String.format("Deleted %d email%s by ID",
                    deletedInThisAction.size(),
                    deletedInThisAction.size() == 1 ? "" : "s"
            );

            DeletionAction action = new DeletionAction(deletedInThisAction, description);
            deletionHistory.push(action);

            System.out.println("✅ Deleted \" + deletedInThisAction.size() + \" email(s)");
        }
        return deletedInThisAction.size();
    }

    public int deletedAllEmails(List<Email> emailsToDelete, String actionDescription) {
        if (emailsToDelete == null || emailsToDelete.isEmpty()) {
            System.out.println("⚠️  No emails to delete.");
            return 0;
        }

        List<Email> deleted = new ArrayList<>();

        // Remove each email from the main list
        for (Email email : emailsToDelete) {
            if (emails.remove(email)) {
                deleted.add(email);
            }
        }

        // Create ONE deletion action for all deleted emails
        if (!deleted.isEmpty()) {
            DeletionAction action = new DeletionAction(deleted, actionDescription);
            deletionHistory.push(action);

            System.out.println("✅ Deleted " + deleted.size() + " email(s)");
        }
        return deleted.size();
    }

    /**
     *  UNDO Functionality
     * @return true if undo successful, false if nothing to undo
     */
    public boolean undoLastDeletion() {
        if (deletedEmails.isEmpty()) {
            System.out.println("⚠️  Nothing to undo.");
            return false;
        }

        // Get the last deleted email
        Email restoredEmail = deletedEmails.pop();

        // Add it back to the main list
        emails.add(restoredEmail);

        // Sort by ID to maintain order
        emails.sort(Comparator.comparingInt(Email::getId));

        System.out.println("↩️  Restored email: " + restoredEmail.getSubject());
        return true;

    }
    /**
     * Check if there are any deletions that can be undone
     * @return true if undo is available
     */
    public boolean canUndo() {
        return !deletedEmails.isEmpty();
    }

    /**
     * Get the number of emails that can be undone
     * @return Count of deleted emails in undo stack
     */
    public int getUndoCount() {
        return deletedEmails.size();
    }

    /**
     *  SAVING
     *  @return true if save successful, false otherwise
     */

    public boolean saveEmails() {
        if (filePath == null) {
            System.err.println("❌ Error: No file path set. Cannot save.");
            return false;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            for (Email email : emails) {
                // Write each field on a separate line
                writer.write("ID: " + email.getId());
                writer.newLine();

                writer.write("From: " + email.getFrom());
                writer.newLine();

                writer.write("To: " + email.getTo());
                writer.newLine();

                writer.write("Subject: " + email.getSubject());
                writer.newLine();

                writer.write("Date: " + email.getDate());
                writer.newLine();

                writer.write("Body: " + email.getBody());
                writer.newLine();

                // Separator between emails
                writer.write("---");
                writer.newLine();
            }

            System.out.println("✅ Successfully saved " + emails.size() + " emails to " + filePath);
            return true;

        } catch (IOException e) {
            System.err.println("❌ Error saving file: " + e.getMessage());
            return false;
        }
    }

    // Utility methods
    /**
     * Get all emails (for display purposes)
     * @return Unmodifiable view of the emails list
     */
    public List<Email> getAllEmails() {
        // Return unmodifiable list to prevent external modification
        return Collections.unmodifiableList(emails);
    }

    /**
     * Get email by ID
     * @param id The email ID to find
     * @return The email, or null if not found
     */
    public Email getEmailById(int id) {
        for (Email email : emails) {
            if (email.getId() == id) {
                return email;
            }
        }
        return null;
    }

    /**
     * Get total count of emails
     * @return Number of emails currently loaded
     */
    public int getEmailCount() {
        return emails.size();
    }

    /**
     * Check if any emails are loaded
     * @return true if emails exist
     */
    public boolean hasEmails() {
        return !emails.isEmpty();
    }

    public Stack<Email> getDeletedEmails(){
        return deletedEmails;
    }
}
