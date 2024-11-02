package View;

import javax.swing.*;
import java.awt.*;

public class GuideWindow extends JFrame {

    public GuideWindow() {
        setTitle("Hướng dẫn trò chơi");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Đóng cửa sổ này mà không thoát chương trình

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Hướng dẫn", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.BLACK);
        panel.add(titleLabel, BorderLayout.NORTH);

        // Sử dụng JEditorPane để hiển thị HTML
        JEditorPane guideText = new JEditorPane();
        guideText.setContentType("text/html");
        guideText.setText(
                "<html>"
                + "<b>• Luật chơi:</b><br>"
                + "  o Server sẽ gửi về cho người chơi một danh sách các hạt lúa bao gồm 2 loại hạt cà phê và hạt đậu nành.<br>"
                + "  o Nhiệm vụ của người chơi là phân loại các hạt thóc dựa trên màu sắc (đỏ, đen) của chúng và bỏ vào 2 giỏ riêng.<br>"
                + "  o Người chơi phải hoàn thành việc phân loại trong thời gian nhanh nhất mà không được phạm lỗi (phân loại sai), nếu phân loại sai sẽ bị trừ điểm.<br>"
                + "  o Sau khi hoàn thành, người chơi gửi kết quả phân loại về cho server để được chấm điểm.<br><br>"
                + "<b>• Chấm điểm:</b><br>"
                + "  o Server sẽ so sánh kết quả của người chơi với đáp án chuẩn để tính điểm. Điểm sẽ dựa trên số hạt phân loại đúng và tốc độ hoàn thành.<br>"
                + "  o Người chơi phân loại đúng và nhanh nhất sẽ nhận được điểm tối đa.<br>"
                + "  o Nếu hết giờ, tự động kiểm tra chất lượng, tốt hết thì cho hòa."
                + "</html>"
        );
        guideText.setEditable(false); // Không cho chỉnh sửa nội dung
        guideText.setCaretPosition(0); // Cuộn lên đầu văn bản

        JScrollPane scrollPane = new JScrollPane(guideText);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Nút để đóng cửa sổ hướng dẫn
        JButton closeButton = new JButton("Đóng");
        closeButton.setFont(new Font("Arial", Font.PLAIN, 18));
        closeButton.addActionListener(e -> dispose()); // Đóng cửa sổ khi nhấn nút
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel);
        setLocationRelativeTo(null); // Căn giữa cửa sổ trên màn hình
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GuideWindow::new);
    }
}
