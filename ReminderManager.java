package thuchanh;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import javax.swing.SwingUtilities;

public class ReminderManager {
    private final List<Reminder> reminders;
    private final Scanner scanner;
    private final DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public ReminderManager(List<Reminder> reminders, Scanner scanner) {
        this.reminders = reminders;
        this.scanner = scanner;
    }


    public void addReminder() {
        System.out.print("Nhap noi dung loi nhac: ");
        String content = scanner.nextLine().trim();
        if (content.isEmpty()) {
            System.out.println(">>> Noi dung khong đuoc đe trong.");
            return;
        }
        System.out.print("Nhap thoi gian (dd/MM/yyyy HH:mm): ");
        String timeStr = scanner.nextLine().trim();
        try {
            LocalDateTime dateTime = LocalDateTime.parse(timeStr, inputFormatter);
            if (dateTime.isBefore(LocalDateTime.now())) {
                System.out.println(">>> Khong the đat loi nhac trong qua khu.");
                return;
            }
            addReminder(content, dateTime); 
            System.out.println(">>> Đã them loi nhac thanh cong.");
        } catch (DateTimeParseException e) {
            System.out.println(">>> Đinh dang thoi gian khong hop le. Hay dung dd/MM/yyyy HH:mm.");
        }
    }

    public void displayReminders() {
        List<Reminder> sortedList = getSortedReminders();
        if (sortedList.isEmpty()) {
            System.out.println(">>> Danh sach loi nhac hien tai đag trong.");
            return;
        }
        System.out.println("\n--- DANH SACH LOI NHAC SAP XEP ---");
        for (int i = 0; i < sortedList.size(); i++) {
            Reminder r = sortedList.get(i);
            System.out.printf("%d. %s\n", i + 1, r.toString());
        }
        System.out.println("------------------------------------");
    }

    public void deleteReminder() {
        displayReminders();
        if (reminders.isEmpty()) return;
        
        System.out.print("Nhap so thu tu loi nhac can xoa: ");
        if (scanner.hasNextLine()) {
            try {
                int index = Integer.parseInt(scanner.nextLine().trim());
                List<Reminder> sortedList = getSortedReminders();
                if (index > 0 && index <= sortedList.size()) {
                    Reminder reminderToDelete = sortedList.get(index - 1);
                    deleteReminder(reminderToDelete); 
                    System.out.println(">>> Đã xoa loi nhac thanh cong.");
                } else {
                    System.out.println(">>> So thu tu khong hop le.");
                }
            } catch (NumberFormatException e) {
                System.out.println(">>> Nhap sai đinh dang so.");
            }
        }
    }
    public void cleanUpReminder() {
        synchronized (reminders) {
            LocalDateTime now = LocalDateTime.now();
            List<Reminder> toRemove = new ArrayList<>();

            // 1. Tìm tất cả lời nhắc đã qua giờ (trong quá khứ)
            for (Reminder r : reminders) {
                if (r.getDateTime().isBefore(now)) {
                    toRemove.add(r);
                }
            }

            // 2. Xử lý xóa và thông báo
            if (!toRemove.isEmpty()) {
                System.out.println("\n>>> [AUTO-CLEAN] Hệ thống tự động dọn dẹp " + toRemove.size() + " lời nhắc cũ:");
                
                for (Reminder r : toRemove) {
                    // Kiểm tra trạng thái để in log phù hợp
                    if (r.isNotified()) {
                        // Case 1: Đã thông báo thành công -> Xóa bình thường
                        System.out.printf("    + [ĐÃ XONG] %s (Lúc: %s)\n", 
                            r.getContent(), r.getDateTime().format(inputFormatter));
                    } else {
                        // Case 2: Bị lỡ (chưa kịp báo) -> Cảnh báo
                        System.out.printf("    ! [BỊ LỠ]   %s (Hạn: %s) -> Đã xóa\n", 
                            r.getContent(), r.getDateTime().format(inputFormatter));
                    }
                    
                    // Xóa khỏi danh sách chín
                    reminders.remove(r);
                }
                System.out.println("----------------------------------------------------");
            }
        }
    }
    public List<Reminder> getSortedReminders() {
        synchronized (reminders) {
            List<Reminder> sortedList = new ArrayList<>(reminders);
            Collections.sort(sortedList);
            return sortedList;
        }
    }
    
    public void addReminder(String content, LocalDateTime dateTime) {
        synchronized (reminders) {
            reminders.add(new Reminder(content, dateTime));
        }
    }
    
    public void deleteReminder(Reminder reminder) {
        synchronized (reminders) {
            reminders.remove(reminder);
        }
    }
    
    public void cleanUpReminders() {
        synchronized (reminders) {
            LocalDateTime now = LocalDateTime.now();
            reminders.removeIf(r -> r.getDateTime().isBefore(now));
        }
    }
    
    public void addSampleReminders() {
        LocalDateTime now = LocalDateTime.now();
        synchronized (reminders) {
            reminders.add(new Reminder("Hen gap mat", now.plusMinutes(5)));
            reminders.add(new Reminder("Nop bai tap lon", now.plusHours(1)));
            reminders.add(new Reminder("Test loi nhac DA QUA HAN", now.minusDays(1))); 
        }
    }
}