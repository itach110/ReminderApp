package thuchanh;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Reminder implements Comparable<Reminder> {
    private String content;
    private LocalDateTime dateTime;
    private boolean isNotified = false; 
    
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public Reminder(String content, LocalDateTime dateTime) {
        this.content = content;
        this.dateTime = dateTime;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }
    
    public boolean isNotified() {
        return isNotified;
    }

    public void setNotified(boolean notified) {
        this.isNotified = notified;
    }

    @Override
    public int compareTo(Reminder other) {
        return this.dateTime.compareTo(other.dateTime);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s", dateTime.format(formatter), content);
    }
}