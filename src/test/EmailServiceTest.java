package test;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

public class EmailServiceTest {

    private ArrayList<EmailTest> emails;
    private Stack<EmailTest> deletedEmails;
    private String filePath;

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

        } catch (FileNotFoundException e) {
            System.err.println("❌ Error: Could not find file '" + filePath + "'");
            System.err.println("   Please make sure the file exists in the project directory.");
        } catch (IOException e) {
            System.err.println("❌ Error reading file: " + e.getMessage());
        }
    }

}
