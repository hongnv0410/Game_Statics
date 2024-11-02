package View;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class RankView extends JFrame {

    private JTable table;

    public RankView(List<String> rankList) {
        setTitle("Bảng xếp hạng");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(450, 320);
        setLocationRelativeTo(null);

        // Set frame background color
        getContentPane().setBackground(new Color(240, 240, 240));

        // Create table model
        DefaultTableModel model = new DefaultTableModel(new String[]{"Số thứ tự", "Username", "Điểm"}, 0);
        table = new JTable(model);

        // Add data to the table
        for (int i = 0; i < rankList.size(); i++) {
            String entry = rankList.get(i).replace("[", "").replace("]", "").trim();
            String[] parts = entry.split(",");
            String username = parts[0].trim();
            int score = Integer.parseInt(parts[1].trim());
            model.addRow(new Object[]{i + 1, username, score});
        }

        // Customize table header
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 15));
        header.setForeground(Color.DARK_GRAY);
        header.setBackground(new Color(210, 210, 210));

        // Center-align data in all columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Set row height
        table.setRowHeight(30);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));

        // Custom cell renderer to highlight top 3 rows and alternating row colors
        DefaultTableCellRenderer customRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                // Highlight top 3 ranks with special colors
                if (row == 0) {
                    cell.setBackground(new Color(255, 223, 0)); // Gold for 1st
                    cell.setFont(cell.getFont().deriveFont(Font.BOLD));
                } else if (row == 1) {
                    cell.setBackground(new Color(192, 192, 192)); // Silver for 2nd
                    cell.setFont(cell.getFont().deriveFont(Font.BOLD));
                } else if (row == 2) {
                    cell.setBackground(new Color(205, 127, 50)); // Bronze for 3rd
                    cell.setFont(cell.getFont().deriveFont(Font.BOLD));
                } else {
                    // Apply alternating row colors for others
                    cell.setBackground(Color.WHITE);
                }

                if (isSelected) {
                    cell.setBackground(new Color(184, 207, 229)); // Selected row color
                }

                setBorder(new EmptyBorder(5, 5, 5, 5));
                setHorizontalAlignment(SwingConstants.CENTER);
                return cell;
            }
        };

        // Apply custom renderer to each column
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(customRenderer);
        }

        // Add table to a scroll pane with padding
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(scrollPane, BorderLayout.CENTER);
    }

    public static void showRankList(List<String> rankList) {
        SwingUtilities.invokeLater(() -> {
            RankView rankView = new RankView(rankList);
            rankView.setVisible(true);
        });
    }
}
