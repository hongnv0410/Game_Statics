package View;

import Controller.ClientControl;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class InviteWindow extends JFrame {

    private JPanel userPanel;
    private ClientControl clientCtr; // Client control để gửi yêu cầu mời chơi
    private String username;

    public InviteWindow(ClientControl clientCtr, String username) {
        this.clientCtr = clientCtr;
        this.username = username;
        setTitle("Mời người chơi");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        //
        clientCtr.setInviteWindow(this);

        userPanel = new JPanel();
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS)); // Sắp xếp dọc
        JScrollPane scrollPane = new JScrollPane(userPanel); // Thêm scroll nếu danh sách quá dài

        add(scrollPane, BorderLayout.CENTER);

        // Lấy danh sách người dùng online từ server
        displayOnlineUsers();

        // Bắt đầu lắng nghe danh sách người dùng online
        startListeningForOnlineUsers();

        setVisible(true);

    }

    // Phương thức để hiển thị danh sách người dùng online
    private void displayOnlineUsers() {
        clientCtr.getOnlineUser();
        List<String> onlineUsers = clientCtr.onlineUsers; // Lấy danh sách người dùng online
        userPanel.removeAll(); // Xóa dữ liệu cũ

        for (String user : onlineUsers) {
            if (!user.equals(username)) { // Không hiển thị chính mình trong danh sách
                JPanel userRow = new JPanel();
                userRow.setLayout(new FlowLayout(FlowLayout.LEFT));

                JLabel userLabel = new JLabel(user);
                JButton inviteButton = new JButton("Mời chơi");

                inviteButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        inviteUser(user); // Gửi lời mời
                    }
                });

                userRow.add(userLabel);
                userRow.add(inviteButton);
                userPanel.add(userRow);
            }
        }

        userPanel.revalidate(); // Cập nhật giao diện
        userPanel.repaint(); // Vẽ lại panel
    }

    // Phương thức để gửi lời mời chơi
    private void inviteUser(String invitee) {
        boolean success = clientCtr.sendInvite(invitee); // Gửi lời mời tới server
        if (success) {
            JOptionPane.showMessageDialog(this, "Đã gửi lời mời tới " + invitee, "Mời chơi", JOptionPane.INFORMATION_MESSAGE);

        } else {
            JOptionPane.showMessageDialog(this, "Không thể gửi lời mời tới " + invitee, "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Phương thức bắt đầu lắng nghe danh sách người dùng online
    private void startListeningForOnlineUsers() {
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(3000);
                    SwingUtilities.invokeLater(() -> displayOnlineUsers()); // Cập nhật giao diện trên luồng Event Dispatch
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void closeWindow() {
        this.dispose();
    }
}
