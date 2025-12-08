public class Email {

    /**
     * id - Unique identifier for each email
     * from - Sender's email address
     * to - Recipient's email address
     * subject - temp.Email subject line
     * date - Date email was sent
     * body - temp.Email content/ message
     */
    private int id; // Unique identifier for each email
    private String from;
    private String to;
    private String subject;
    private String date;
    private String body;

    /**
     * Field
     * @param id - Unique identifier
     * @param from - Sender email address
     * @param to - Recipient email address
     * @param subject - temp.Email subject
     * @param date - Date sent
     * @param body - temp.Email content
     */
    public Email(int id, String from, String to, String subject, String date, String body) {
        this.id = id;
        this.from = from;
        this.subject = subject;
        this.date = date;
        this.body = body;
    }

    /**
     * Get the email's unique ID
     * @return The email ID
     */
    public int getId() {
        return id;
    }

    /**
     * Get the sender's email address
     * @return The from email address
     */
    public String getFrom() {
        return from;
    }

    /**
     * Get the sender's email address
     * @return The to email address
     */
    public String getTo() {
        return to;
    }

    /**
     * Get the email subject
     * @return The subject lin
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Get the date the email was sent
     * @return The date string
     */
    public String getDate() {
        return date;
    }

    /**
     * Get the full email body
     * @return The complete email content
     */
    public String getBody() {
        return body;
    }

    /**
     * @return Formatted string representation of the email
     */
    @Override
    public String toString() {
        return String.format(
                "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━%n" +
                        "ID:      %d%n" +
                        "From:    %s%n" +
                        "To:      %s%n" +
                        "Subject: %s%n" +
                        "Date:    %s%n" +
                        "Body:    %s%n" +
                        "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━",
                id, from, to, subject, date, getBodyPreview()
        );
    }

    /**
     * @return First 100 characters of body, or full body if shorter
     * If body is longer than 100 chars, take first 100 and add "..."
     * Otherwise, return the full body
     */
    public String getBodyPreview() {
        return body.length() > 100 ? body.substring(0, 100) + "..." : body;
    }

    /**
     * Alternative: Compact display format for list views
     * Shows just essential info in one line
     *
     * @return One-line summary of the email
     */
    public String toCompactString() {
        return String.format("[%d] %s | %s | %s",
                id, date, from, subject);
    }
}
