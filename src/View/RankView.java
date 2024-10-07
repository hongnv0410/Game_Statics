package View;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class RankView extends JFrame {
    private JTable table;

    public RankView(List<Object[]> rankList) {
        setTitle("Bảng xếp hạng");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null);
        
        // Tạo model cho bảng
        DefaultTableModel model = new DefaultTableModel(new String[]{"Số thứ tự", "Username", "Điểm"}, 0);
        table = new JTable(model);
        
        // Thêm dữ liệu vào bảng
        for (int i = 0; i < rankList.size(); i++) {
            Object[] entry = rankList.get(i);
            String username = (String) entry[0];
            int score = (Integer) entry[1];
            model.addRow(new Object[]{i + 1, username, score}); // Thêm dữ liệu vào bảng
        }

        // Tô đậm tiêu đề cột
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Arial", Font.BOLD, 14)); // Tô đậm và cỡ chữ tiêu đề
        header.setForeground(Color.BLACK); // Màu chữ của tiêu đề
        header.setBackground(Color.LIGHT_GRAY); // Nền tiêu đề

        // Căn giữa dữ liệu trong các cột
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // Cột "Số thứ tự"
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer); // Cột "Username"
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer); // Cột "Điểm"

        // Đặt bảng vào JScrollPane
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);
    }

    public static void showRankList(List<Object[]> rankList) {
        SwingUtilities.invokeLater(() -> {
            RankView rankView = new RankView(rankList);
            rankView.setVisible(true);
        });
    }
}
