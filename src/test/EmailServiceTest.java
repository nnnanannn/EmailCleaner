package test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

public class EmailServiceTest {

    /**
     * ArrayList<EmailTest> emails - Main list of all emails
     * Stack<Email> deletedEmails - Stack for undo functionality
     * String filePath - Path to the emails file
     */
    private ArrayList<EmailTest> emails;
    private Stack<EmailTest> deletedEmails;
    private String filePath;

    /**
     * Initialize with empty collection
     */
    public EmailServiceTest() {
        this.emails = new ArrayList<>();
        this.deletedEmails = new Stack<>();
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
                    EmailTest email = new EmailTest(id, from, to, subject, date, body.toString());
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
     * Email Searching
     * @param keywords Array of keywords to search for
     * @param searchInBody If true, search in both subject and body; if false, subject only
     * @return List of emails that match any of the keywords
     *
     */
    public List<EmailTest> searchEmails(String[] keywords, boolean searchInBody) {

        List<EmailTest> results = new ArrayList<>();

        for (EmailTest email : emails) {
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
            System.out.println(searchText);

            // Check if any keyword matches
            boolean found = false;
            for (String keyword : keywords) {
                System.out.println(keyword.toLowerCase());
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
        return results;
    }

    /**
     * Email Deletion By ID
     * @param id The ID of the email to delete
     * @return true if deleted, false if not found
     */
    public boolean deleteEmailById(int id) {
        Iterator<EmailTest> iterator = emails.iterator();

        while (iterator.hasNext()) {
            EmailTest email = iterator.next();

            if (email.getId() == id) {
                // Save to undo stack Before ACTUAL Removing
                deletedEmails.push(email);

                iterator.remove();

                System.out.println("✅ Deleted email: " + email.getSubject());
                return true;
            }
        }

        System.out.println("❌ Email with ID " + id + " not found.");
        return false;
    }

    /**
     * Multiple Emails Deletion at once
     * @param ids Array of email IDs to delete
     * @return Number of emails successfully deleted
     */
    public int deleteMultipleEmails(int[] ids) {
        int deletedCount = 0;

        for (int id : ids) {
            if (deleteEmailById(id)) {
                deletedCount++;
            }
        }
        return deletedCount;
    }
}
