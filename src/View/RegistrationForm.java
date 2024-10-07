package View;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import Controller.ClientControl;
import Model.User;

public class RegistrationForm extends JFrame implements ActionListener {

    private JTextField txtUsername;
    private JPasswordField txtPassword, txtConfirmPassword;
    private JButton btnRegister;

    public RegistrationForm() {
        super("Register");

        // Thiết lập các trường cho form đăng ký
        txtUsername = new JTextField(15);
        txtPassword = new JPasswordField(15);
        txtConfirmPassword = new JPasswordField(15);
        txtPassword.setEchoChar('*');
        txtConfirmPassword.setEchoChar('*');
        btnRegister = new JButton("Register");

        // Panel đăng ký
        JPanel registerPanel = new JPanel();
        registerPanel.setLayout(new FlowLayout());
        registerPanel.add(new JLabel("Username:"));
        registerPanel.add(txtUsername);
        registerPanel.add(new JLabel("Password:"));
        registerPanel.add(txtPassword);
        registerPanel.add(new JLabel("Confirm Password:"));
        registerPanel.add(txtConfirmPassword);
        registerPanel.add(btnRegister);

        this.setLayout(new BorderLayout());
        this.add(registerPanel, BorderLayout.CENTER);

        this.pack();
        this.setLocationRelativeTo(null); // Căn giữa cửa sổ
        this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Thêm action listener cho nút
        btnRegister.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(btnRegister)) {
            String username = txtUsername.getText();
            String password = new String(txtPassword.getPassword());
            String confirmPassword = new String(txtConfirmPassword.getPassword());

            if (!password.equals(confirmPassword)) {
                showMessage("Passwords do not match!");
                return;
            }

            User newUser = new User(username, password);
            ClientControl clientCtr = new ClientControl();
            clientCtr.openConnection();
            clientCtr.sendData(newUser);
            String result = clientCtr.receiveData();
            if (result.equals("register_success")) {
                showMessage("Registration successful!");
                this.dispose(); // Đóng form đăng ký sau khi đăng ký thành công
            } else {
                showMessage("Registration failed. Please try again.");
            }
            clientCtr.closeConnection();
        }
    }

    public void showMessage(String msg) {
        JOptionPane.showMessageDialog(this, msg);
    }

    public static void main(String[] args) {
        new RegistrationForm().setVisible(true);
    }
}
