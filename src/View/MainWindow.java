package View;

import Controller.ClientControl;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class MainWindow extends JFrame implements ActionListener {

    private JButton btnPlay;
    private JButton btnRank;
    private JButton btnGuide;
    private JButton btnLogout;
    private String username;
    private ClientControl clientCtr; // Thêm thuộc tính ClientControl

    public MainWindow(String username, ClientControl clientCtr) {
        this.clientCtr = clientCtr;
        setTitle("Game Hành Gà");
        this.username = username;
        setSize(800, 500);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // Không tự động đóng, để ta quản lý
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Đóng kết nối trước khi đóng cửa sổ
                if (clientCtr != null) {
                    clientCtr.closeConnection(); // Gọi hàm đóng kết nối
                }
                System.exit(0); // Đóng ứng dụng
            }
        });
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Image backgroundImage = new ImageIcon("C:\\Users\\admin\\Downloads\\z5862069502821_67adf93f75a33a466ba16ec8036cdc55.jpg").getImage();
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        };

        backgroundPanel.setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Game Hành Gà", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 30));
        titleLabel.setForeground(Color.WHITE);
        backgroundPanel.add(titleLabel, BorderLayout.NORTH);

        // Tạo JPanel cho các nút với BoxLayout
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS)); // Hàng dọc
        buttonPanel.setOpaque(false); // Để thấy hình nền
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 50))); // Padding-top

        // Tạo các nút
        btnPlay = createButton("Chơi");
        btnRank = createButton("Bảng xếp hạng");
        btnGuide = createButton("Hướng dẫn");
        btnLogout = createButton("Thoát");

        // Thêm ActionListener cho từng nút
        btnPlay.addActionListener(this);
        btnRank.addActionListener(this);
        btnGuide.addActionListener(this);
        btnLogout.addActionListener(this);

        // Thêm các nút vào JPanel
        buttonPanel.add(btnPlay);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Tăng khoảng cách giữa các nút
        buttonPanel.add(btnRank);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Khoảng cách giữa các nút
        buttonPanel.add(btnGuide);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 20))); // Khoảng cách giữa các nút
        buttonPanel.add(btnLogout);

        // Căn giữa buttonPanel trong backgroundPanel
        backgroundPanel.add(buttonPanel, BorderLayout.CENTER);

        // Căn giữa cửa sổ trên màn hình
        setLocationRelativeTo(null);
        setContentPane(backgroundPanel);
        setVisible(true);
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(200, 60));  // Kích thước nút lớn hơn
        button.setMaximumSize(new Dimension(200, 60));    // Giới hạn kích thước tối đa
        button.setOpaque(true);
        button.setForeground(Color.WHITE);  // Chữ màu trắng
        button.setBackground(new Color(255, 0, 127));  // Màu hồng đậm không trong suốt
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));  // Viền trắng dày hơn
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setFont(new Font("Arial", Font.PLAIN, 20));
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setVerticalAlignment(SwingConstants.CENTER);
        button.setMargin(new Insets(10, 20, 10, 20)); // Padding bên trong nút
        return button;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == btnPlay) {
            // Lấy danh sách người dùng online
//            List<String> onlineUsers = clientCtr.getOnlineUsers(); // Lấy danh sách người dùng online
//            System.out.println("Users online:");
//            for (String username : onlineUsers) {
//                System.out.println(username); // In ra tên người dùng
//            }
            // new CircleSquareGame(this.username);
            // this.dispose();
            new InviteWindow(clientCtr, username);
        } else if (e.getSource() == btnRank) {
            var rankList = clientCtr.getRankList();
            if (!rankList.isEmpty()) {
                RankView.showRankList(rankList);
            } else {
                JOptionPane.showMessageDialog(this, "Không có dữ liệu xếp hạng!", "Bảng xếp hạng", JOptionPane.INFORMATION_MESSAGE);
            }
        } else if (e.getSource() == btnGuide) {
            new GuideWindow();
        } else if (e.getSource() == btnLogout) {
            Login loginFrame = new Login();
            loginFrame.setVisible(true); // Đặt trạng thái hiển thị là true
            loginFrame.pack();
            loginFrame.setLocationRelativeTo(null);
            this.dispose();
        }
    }

    public static void main(String[] args) {
        ClientControl c = new ClientControl();
        new MainWindow("User1", c); // Thay thế "User1" bằng tên người dùng thực tế nếu cần
    }
}
