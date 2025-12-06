package thuchanh;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JOptionPane; 
import javax.swing.SwingUtilities;

public class NotificationService {
    private final List<Reminder> reminders;
    private final ScheduledExecutorService scheduler;
    private ReminderGUI guiCallback; 
    private static final String RINGTONE_FILE = "ring.wav"; 

    public NotificationService(List<Reminder> reminders) {
        this.reminders = reminders;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }
    
    public void setGuiCallback(ReminderGUI gui) {
        this.guiCallback = gui;
    }

    private void playSound() {
        try {
            File soundFile = new File(RINGTONE_FILE);
            if (soundFile.exists()) {
                Clip clip = AudioSystem.getClip();
                clip.open(AudioSystem.getAudioInputStream(soundFile));
                clip.start();
            } else {
                if (guiCallback != null) {
                    guiCallback.appendNotification("!!! ERROR: Khong tim thay file nhac chuong: " + RINGTONE_FILE);
                } else {
                    System.err.println("!!! ERROR: Khong tim thay file nhac chuong: " + RINGTONE_FILE);
                }
            }
        } catch (Exception e) {
            if (guiCallback != null) {
                guiCallback.appendNotification("!!! Loi khi phat am thanh: " + e.getMessage());
            } else {
                 System.err.println("!!! Loi khi phat am thanh: " + e.getMessage());
            }
        }
    }

    public void start() {
        Runnable checkReminders = () -> {
            synchronized (reminders) {
                LocalDateTime now = LocalDateTime.now();

                for (Reminder reminder : reminders) {
                    if (!reminder.isNotified() && (reminder.getDateTime().isBefore(now) || reminder.getDateTime().isEqual(now))) {
                        
                        playSound(); // PHÁT CHUÔNG
                        
                        String notificationMessage = String.format("!!! LOI NHAC: %s (Luc: %s)", 
                            reminder.getContent(), reminder.getDateTime().toLocalTime());
                        
                        if (guiCallback != null) {
                            SwingUtilities.invokeLater(() -> {
                                 guiCallback.appendNotification("----------------------------------------------------");
                                 guiCallback.appendNotification(notificationMessage);
                                 guiCallback.appendNotification("----------------------------------------------------");
                                 
                                 guiCallback.refreshReminderTable(); 
                                 
                                 JOptionPane.showMessageDialog(guiCallback, 
                                     String.format("Đã đến giờ!\n\nNội dung: %s", reminder.getContent()), 
                                     "THÔNG BÁO LỜI NHẮC", 
                                     JOptionPane.INFORMATION_MESSAGE);
                            });
                        } else {
                            System.out.println("\n----------------------------------------------------");
                            System.out.printf("!!! BING! BING! BING! - LOI NHAC !!!\nNoi dung: %s\n", reminder.getContent());
                            System.out.println("----------------------------------------------------");
                        }
                        
                        reminder.setNotified(true);
                    }
                }
            }
        };
        scheduler.scheduleAtFixedRate(checkReminders, 1, 1, TimeUnit.SECONDS);
    }

    public void stop() {
        scheduler.shutdownNow();
    }
}