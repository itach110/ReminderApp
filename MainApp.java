package thuchanh;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import javax.swing.SwingUtilities; 

public class MainApp {
    public static void main(String[] args) {
        // 1. Khởi tạo các dịch vụ
        List<Reminder> sharedList = Collections.synchronizedList(new ArrayList<>());
        
        // Cần một Scanner tạm thời (hoặc null) để phù hợp với constructor của ReminderManager
        // Giữ lại để ReminderManager có thể hoạt động ở chế độ Console nếu cần
        Scanner tempScanner = new Scanner(System.in); 
        
        ReminderManager manager = new ReminderManager(sharedList, tempScanner);
        NotificationService notifier = new NotificationService(sharedList);

        // 2. Thêm dữ liệu mẫu và quét dọn
        manager.addSampleReminders();
        manager.cleanUpReminders(); 

        // 3. Khởi chạy Giao diện đồ họa (GUI)
        // Đảm bảo GUI chạy trên Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            ReminderGUI gui = new ReminderGUI(manager, notifier); 
            
            notifier.setGuiCallback(gui); 
            
            notifier.start(); 
        });
    }
}