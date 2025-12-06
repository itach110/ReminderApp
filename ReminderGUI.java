package thuchanh;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@SuppressWarnings("serial")
public class ReminderGUI extends JFrame {
    
    private final ReminderManager manager;
    private final NotificationService notifier;
    private final DefaultTableModel tableModel;
    private final JTable reminderTable;
    private final JTextArea notificationArea;

    // Fields nhập liệu
    private final JTextField contentField;
    private final JTextField dateTimeField;
    
    private static final DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
    
    // Constructor
    public ReminderGUI(ReminderManager manager, NotificationService notifier) {
        this.manager = manager;
        this.notifier = notifier;
        
        // Cấu hình cửa sổ chính
        setTitle("ỨNG DỤNG QUẢN LÝ LỜI NHẮC");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(850, 600);
        getContentPane().setLayout(new BorderLayout(10, 10)); // Layout chính
        
        // --- 1. Panel Bảng hiển thị (NORTH) ---
        String[] columnNames = {"Nội dung", "Thời gian", "Trạng thái"};
        
        // Model bảng: Khóa không cho sửa trực tiếp
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        reminderTable = new JTable(tableModel);
        
        // --- SỬA LỖI CHỌN DÒNG Ở ĐÂY ---
        // 1. Tắt chế độ chọn từng ô nhỏ (Cell Selection)
        reminderTable.setCellSelectionEnabled(false);
        // 2. Bật chế độ chọn cả hàng (Row Selection)
        reminderTable.setRowSelectionAllowed(true);
        // 3. Tắt chọn cột (để chắc chắn)
        reminderTable.setColumnSelectionAllowed(false);
        // 4. Chỉ cho phép chọn 1 dòng tại 1 thời điểm
        reminderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // --------------------------------
        
        JScrollPane tableScrollPane = new JScrollPane(reminderTable);
        
        // --- 2. Panel Nhập liệu và Thao tác (WEST/CENTER) ---
        JPanel controlPanel = new JPanel(new GridLayout(2, 1, 10, 10));

        // 2a. Form Thêm lời nhắc
        GridBagLayout gbl_addPanel = new GridBagLayout();
        gbl_addPanel.rowHeights = new int[]{35, 36, 37};
        gbl_addPanel.columnWeights = new double[]{0.0, 1.0};
        gbl_addPanel.rowWeights = new double[]{0.0};
        JPanel addPanel = new JPanel(gbl_addPanel);
        addPanel.setBorder(BorderFactory.createTitledBorder("Thêm lời nhắc mới"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Khởi tạo fields
        contentField = new JTextField(20);
        // Tự động điền thời gian hiện tại + 5 phút
        dateTimeField = new JTextField(displayFormatter.format(LocalDateTime.now().plusMinutes(5)), 20);

        gbc.gridx = 0; gbc.gridy = 0; 
        addPanel.add(new JLabel("Nội dung:"), (GridBagConstraints) gbc.clone());
        
        gbc.gridx = 1; gbc.gridy = 0; 
        addPanel.add(contentField, (GridBagConstraints) gbc.clone());
        
        gbc.gridx = 0; gbc.gridy = 1; 
        addPanel.add(new JLabel("Thời gian (dd/MM/yyyy HH:mm):"), (GridBagConstraints) gbc.clone());
        
        gbc.gridx = 1; gbc.gridy = 1; 
        addPanel.add(dateTimeField, (GridBagConstraints) gbc.clone());

        // --- Nút Thêm ---
        JButton addButton = new JButton("Thêm Lời nhắc");
        // Dùng ActionListener truyền thống để WindowBuilder hiển thị tốt
        addButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                addReminderAction(e);
            }
        });
        
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; 
        addPanel.add(addButton, (GridBagConstraints) gbc.clone());
        
        controlPanel.add(addPanel);

        // 2b. Nút Xóa
        JPanel deletePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton deleteButton = new JButton("Xóa Lời nhắc Đã Chọn");
        
        deleteButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                deleteReminderAction(e);
            }
        });
        
        deletePanel.add(deleteButton);
        controlPanel.add(deletePanel);
        
        // --- 3. Panel Thông báo (SOUTH) ---
        notificationArea = new JTextArea(5, 40);
        notificationArea.setEditable(false);
        JScrollPane notificationScrollPane = new JScrollPane(notificationArea);
        notificationArea.append("Bắt đầu ứng dụng. Đảm bảo file 'ring.wav' tồn tại.\n");
        
        // --- Thêm các Panel vào Frame ---
        getContentPane().add(tableScrollPane, BorderLayout.CENTER); // Bảng là trung tâm
        getContentPane().add(controlPanel, BorderLayout.NORTH); // Panel điều khiển ở trên
        getContentPane().add(notificationScrollPane, BorderLayout.SOUTH); // Thông báo ở dưới
        
        // Hiển thị cửa sổ
        setLocationRelativeTo(null); // Đặt ở giữa màn hình
        setVisible(true);
        
        // Khởi tạo bảng lần đầu
        refreshReminderTable();
    }
    
    // --- PHƯƠNG THỨC HÀNH ĐỘNG ---
    @SuppressWarnings("unused")
	private void startAutoCleanTimer() {
        // Tạo Timer chạy mỗi 10 giây (10000 ms)
        Timer autoCleanTimer = new Timer(10000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Gọi hàm cleanUpReminder() bên ReminderManager
                    // (Lưu ý: Bạn phải chắc chắn đã paste hàm này vào file ReminderManager.java)
                    manager.cleanUpReminder();
                    
                    // Cập nhật lại bảng hiển thị để thấy các dòng đã bị xóa
                    refreshReminderTable();
                } catch (Exception ex) {
                    // Bỏ qua lỗi nếu chưa có hàm hoặc lỗi logic
                    System.err.println("Lỗi Auto-Clean: " + ex.getMessage());
                }
            }
        });
        autoCleanTimer.start();
    }

    private void addReminderAction(ActionEvent e) {
        String content = contentField.getText().trim();
        String timeStr = dateTimeField.getText().trim();
        
        if (content.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nội dung không được để trống.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            LocalDateTime dateTime = LocalDateTime.parse(timeStr, displayFormatter);
            if (dateTime.isBefore(LocalDateTime.now())) {
                JOptionPane.showMessageDialog(this, "Thời gian không thể là quá khứ.", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            manager.addReminder(content, dateTime);
            
            // Hiện Dialog thành công (File SuccessDialog.java)
            new SuccessDialog(this, content, timeStr);
            
            appendNotification("Đã thêm lời nhắc: " + content + " vào lúc " + timeStr);
            contentField.setText("");
            dateTimeField.setText(displayFormatter.format(LocalDateTime.now().plusMinutes(5))); 
            refreshReminderTable();
            
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Định dạng thời gian không hợp lệ. Hãy dùng dd/MM/yyyy HH:mm.", "Lỗi định dạng", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void deleteReminderAction(ActionEvent e) {
        // Lấy dòng được chọn (Bây giờ đã chọn được cả dòng)
        int selectedRow = reminderTable.getSelectedRow();
        
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn một lời nhắc để xóa.", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String content = (String) tableModel.getValueAt(selectedRow, 0);
        String timeStr = (String) tableModel.getValueAt(selectedRow, 1);

        try {
            LocalDateTime dateTime = LocalDateTime.parse(timeStr, displayFormatter);
            List<Reminder> currentReminders = manager.getSortedReminders();
            Reminder reminderToDelete = null;
            
            for (Reminder r : currentReminders) {
                if (r.getContent().equals(content) && r.getDateTime().equals(dateTime)) {
                    reminderToDelete = r;
                    break;
                }
            }

            if (reminderToDelete != null) {
                manager.deleteReminder(reminderToDelete);
                appendNotification("Đã xóa lời nhắc: " + content);
                refreshReminderTable();
            } else {
                 appendNotification("Lỗi: Không tìm thấy đối tượng lời nhắc để xóa.");
            }
        } catch (Exception ex) {
            appendNotification("Lỗi khi xóa: " + ex.getMessage());
        }
    }
    
    // --- PHƯƠNG THỨC CÔNG CỘNG ---

    public void refreshReminderTable() {
        SwingUtilities.invokeLater(() -> {
            tableModel.setRowCount(0); 
            List<Reminder> sortedReminders = manager.getSortedReminders();
            for (Reminder r : sortedReminders) {
                String status = r.isNotified() ? "Đã thông báo" : "Đang chờ";
                tableModel.addRow(new Object[]{
                    r.getContent(),
                    r.getDateTime().format(displayFormatter),
                    status
                });
            }
        });
    }

    public void appendNotification(String message) {
        SwingUtilities.invokeLater(() -> {
            notificationArea.append(message + "\n");
            notificationArea.setCaretPosition(notificationArea.getDocument().getLength());
        });
    }
}