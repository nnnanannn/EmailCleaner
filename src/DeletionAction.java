import java.util.*;

public class DeletionAction {

    private List<Email> deletedEmails;
    private long timestamp;
    private String description;

    /**
     * Constructor for multiple deletion
     * Create a new deletion action
     *
     * @param deletedEmails List of emails that were deleted
     * @param description description of the action
     */
    public DeletionAction(List<Email> deletedEmails, String description) {
        this.deletedEmails = new ArrayList<>(deletedEmails);
        this.description = description;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * Constructor for single email deletion
     *
     * @param email Single email that was deleted
     * @param description Description of the action
     */
    public DeletionAction(Email email, String description) {
        this.deletedEmails = new ArrayList<>();
        this.deletedEmails.add(email);
        this.description = description;
        this.timestamp = System.currentTimeMillis();
    }

    /**
     * Get all emails that were deleted in this action
     * Returns a defensive copy to prevent external modification
     */
    public List<Email> getDeletedEmails() {
        return new ArrayList<>(deletedEmails);
    }

    public int getCount() {
        return deletedEmails.size();
    }

    public String getDescription() {
        return description;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String toString() {
        return String.format("%s (%d email%s)",
                description,
                getCount(),
                getCount() == 1 ? "" : "s"
        );
    }

    public String getDetailedSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append(toString()).append("\n");
        sb.append("Emails deleted:\n");

        for (Email email : deletedEmails) {
            sb.append(String.format(" - [ID: %d] %s\n",
                    email.getId(),
                    email.getSubject()
            ));
        }
        return sb.toString();
    }
}
