package View;

import javax.swing.*;

public class ClientView {
    public static void main(String[] args) {
        // Khởi tạo và hiển thị giao diện Login
        Login loginFrame = new Login();
        loginFrame.setVisible(true); // Đặt trạng thái hiển thị là true
        loginFrame.pack();
        loginFrame.setLocationRelativeTo(null); // Đặt vị trí ở giữa màn hình
    }
}
