package thuchanh;

import javax.swing.*;
import javax.swing.border.EmptyBorder; // Thêm import nếu cần
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

@SuppressWarnings("serial")
public class SuccessDialog extends JDialog {

    // Constructor chính
    public SuccessDialog(Frame parent, String content, String timeStr) {
        super(parent, "Thông báo", true); // true = Modal (chặn thao tác cửa sổ cha)
        setSize(400, 300); // Tăng chiều cao lên một chút để chứa đủ nội dung
        
        // Sử dụng BorderLayout cho container chính để dễ quản lý hơn, 
        // hoặc giữ GridBagLayout nhưng cần set cho ContentPane
        getContentPane().setLayout(new GridBagLayout());
        
        // Nếu parent null (khi test), đặt giữa màn hình
        if (parent != null) {
            setLocationRelativeTo(parent);
        } else {
            setLocationRelativeTo(null);
        }

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0; 
        gbc.gridy = GridBagConstraints.RELATIVE;
        gbc.insets = new Insets(10, 0, 10, 0); // Khoảng cách giãn dòng
        gbc.anchor = GridBagConstraints.CENTER;

        // 1. Tiêu đề !THÔNG BÁO!
        JLabel lblTitle = new JLabel("!THÔNG BÁO!");
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 24));
        // SỬA LỖI: Dùng clone() để WindowBuilder không báo lỗi "added ... more than once"
        getContentPane().add(lblTitle, (GridBagConstraints) gbc.clone());

        // 2. Nội dung
        JLabel lblContent = new JLabel("Nhắc nhở: " + content);
        lblContent.setFont(new Font("SansSerif", Font.PLAIN, 16));
        getContentPane().add(lblContent, (GridBagConstraints) gbc.clone());

        // --- SỬA ĐỔI: Tách phần thời gian xuống dưới và làm nổi bật ---
        
        // Tăng khoảng cách top lên để tách biệt với phần nội dung
        gbc.insets = new Insets(20, 0, 5, 0); 
        
        // 3a. Tiêu đề Thời gian
        JLabel lblTimeTitle = new JLabel("Thời gian:");
        lblTimeTitle.setFont(new Font("SansSerif", Font.PLAIN, 16));
        getContentPane().add(lblTimeTitle, (GridBagConstraints) gbc.clone());

        // 3b. Giá trị Thời gian (xuống dòng, in đậm)
        gbc.insets = new Insets(0, 0, 10, 0); // Khoảng cách gần với tiêu đề "Thời gian:"
        JLabel lblTimeValue = new JLabel(timeStr);
        lblTimeValue.setFont(new Font("SansSerif", Font.BOLD, 20)); // Font to và đậm hơn
        getContentPane().add(lblTimeValue, (GridBagConstraints) gbc.clone());

        // 4. Nút OK to
        JButton btnOK = new JButton("OK");
        btnOK.setFont(new Font("SansSerif", Font.BOLD, 14));
        btnOK.setPreferredSize(new Dimension(120, 45)); 
        
        btnOK.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose(); // Đóng dialog
            }
        });
        
        gbc.insets = new Insets(20, 0, 0, 0); 
        getContentPane().add(btnOK, (GridBagConstraints) gbc.clone());
        
        // Chỉ setVisible trong logic gọi, hoặc constructor nếu muốn hiện ngay
        // Nhưng tốt nhất constructor chỉ nên dựng giao diện.
        // Để tương thích code cũ của bạn, ta giữ setVisible(true) ở đây
        setVisible(true);
    }
    
    // --- THÊM HÀM NÀY ĐỂ SỬA LỖI WINDOWBUILDER ---
    public static void main(String[] args) {
        try {
            // Giả lập dữ liệu để WindowBuilder hiển thị được
            SuccessDialog dialog = new SuccessDialog(null, "Nội dung mẫu test", "01/01/2025 10:00");
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}