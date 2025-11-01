package test;

public class EmailTest {

    private int id; // Unique identifier for each email
    private String from;
    private String to;
    private String subject;
    private String date;
    private String body;

    public EmailTest(int id, String from, String to, String subject, String date, String body) {
        this.id = id;
        this.from = from;
        this.subject = subject;
        this.date = date;
        this.body = body;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override  // This overrides the default toString() method from Object class
    public String toString() {
        return String.format(
                "ID: %d\nFrom: %s\nTo: %s\nSubject: %s\nDate: %s\nBody: %s\n",
                id, from, to, subject, date, body
        );
    }

    public String getBodyPreview() {
        return body.length() > 100 ? body.substring(0, 100) + "..." : body;
    }
}
