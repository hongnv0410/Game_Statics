package View;

import Controller.ClientControl;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MainWindow extends JFrame implements ActionListener {

    private JButton btnPlay;
    private JButton btnRank;
    private JButton btnGuide;
    private JButton btnLogout;
    private String username;
    private ClientControl clientCtr;

    public MainWindow(String username, ClientControl clientCtr) {
        this.clientCtr = clientCtr;
        this.username = username;

        setTitle("Game Tấm Cám");
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);

        // Xử lý đóng cửa sổ để đảm bảo đóng kết nối
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (clientCtr != null) {
                    clientCtr.closeConnection();
                }
                System.exit(0);
            }
        });

        // Tạo panel nền giả gỗ
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Image woodBackground = new ImageIcon(getClass().getResource("/images/hinhnen.png")).getImage();
                g.drawImage(woodBackground, 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setLayout(new BorderLayout());

        // Tiêu đề
        JLabel titleLabel = new JLabel("Game Tấm nhặt thóc", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Lato", Font.BOLD, 36));
        titleLabel.setForeground(new Color(139, 69, 19));  // Màu nâu sẫm
        backgroundPanel.add(titleLabel, BorderLayout.NORTH);

        // Tạo panel cho các nút
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setOpaque(false);

        // Tạo các nút với hình nền nâu và kiểu dáng cổ điển
        btnPlay = createWoodButton("Chơi");
        btnRank = createWoodButton("Bảng xếp hạng");
        btnGuide = createWoodButton("Hướng dẫn");
        btnLogout = createWoodButton("Thoát");

        // Thêm các nút vào panel
        buttonPanel.add(Box.createVerticalGlue()); // Đẩy nút xuống giữa màn hình
        buttonPanel.add(btnPlay);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        buttonPanel.add(btnRank);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        buttonPanel.add(btnGuide);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        buttonPanel.add(btnLogout);
        buttonPanel.add(Box.createVerticalGlue()); // Đẩy nút lên giữa màn hình

        backgroundPanel.add(buttonPanel, BorderLayout.CENTER);
        setContentPane(backgroundPanel);
        setVisible(true);
    }

    // Phương thức tạo nút kiểu giả gỗ
    private JButton createWoodButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(200, 60));
        button.setMaximumSize(new Dimension(200, 60));
        button.setFocusPainted(false);
        button.setBackground(new Color(160, 82, 45));  // Màu nâu nhạt
        button.setForeground(Color.WHITE);  // Chữ trắng
        button.setFont(new Font("Late", Font.BOLD, 20));
        button.setBorder(BorderFactory.createLineBorder(new Color(139, 69, 19), 3)); // Viền nâu đậm
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.addActionListener(this);
        return button;
    }

    // Xử lý sự kiện cho các nút
    @Override
    public void actionPerformed(ActionEvent e) {
        JButton source = (JButton) e.getSource();
        if (source == btnPlay) {
            new InviteWindow(clientCtr, username);
        } else if (source == btnRank) {
            clientCtr.getRankList();
            try {
                Thread.sleep(500); // Đợi phản hồi từ server
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
            var rankList = clientCtr.RankList;
            if (!rankList.isEmpty()) {
                RankView.showRankList(rankList);
            } else {
                JOptionPane.showMessageDialog(this, "Không có dữ liệu xếp hạng!", "Bảng xếp hạng", JOptionPane.INFORMATION_MESSAGE);
            }
        } else if (source == btnGuide) {
            new GuideWindow();
        } else if (source == btnLogout) {
            Login loginFrame = new Login();
            loginFrame.setVisible(true);
            loginFrame.pack();
            loginFrame.setLocationRelativeTo(null);
            this.dispose();
        }
    }

    // Phương thức main để chạy chương trình
    public static void main(String[] args) {
        ClientControl c = new ClientControl();
        new MainWindow("User1", c); // Thay "User1" bằng tên người dùng thực tế nếu cần
    }
}
