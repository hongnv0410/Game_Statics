package View;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Random;

public class CircleSquareGame extends JFrame {

    private int score = 0;
    private int opponentScore = 0; // Điểm của đối thủ
    private JLabel scoreLabel; // Nhãn hiển thị số điểm
    private JLabel opponentScoreLabel; // Nhãn hiển thị điểm đối thủ
    private JLabel scoreTextLabel; // Nhãn hiển thị chữ "Điểm:"
    private JLabel opponentScoreTextLabel; // Nhãn hiển thị chữ "Điểm đối thủ:"
    private ImageSquare square1, square2; // Hai hình vuông với hình ảnh
    private JLabel usernameLabel; // Nhãn hiển thị tên người dùng
    private ArrayList<DraggableCircle> circles = new ArrayList<>(); // Danh sách các hình tròn
    private Image backgroundImage; // Hình ảnh nền
    private String username;
    private String opponentName;

    public CircleSquareGame(String username, String opponentName) {
        this.username = username; // Gán tên người dùng
        this.opponentName = opponentName;
        // Thiết lập JFrame
        setTitle("Trận đấu giữa " + username + " và " + opponentName);
        setSize(570, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null);

        // Tải hình ảnh nền
        backgroundImage = new ImageIcon("D:\\laptrinhmang\\src\\View\\images\\hinhnen.png").getImage(); // Thay đường dẫn với hình ảnh nền của bạn

        // Tạo một JPanel để làm nền
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this); // Vẽ hình ảnh nền

                // Vẽ ô điểm trực tiếp lên nền
                g.setColor(new Color(255, 255, 255, 200)); // Màu nền trắng với độ trong suốt
                g.fillRoundRect(10, 5, 80, 30, 10, 10); // Vẽ hình chữ nhật cho ô điểm
                g.fillRoundRect(400, 5, 140, 30, 10, 10); // Vẽ ô điểm cho đối thủ
            }
        };
        backgroundPanel.setLayout(null);
        setContentPane(backgroundPanel); // Đặt JPanel làm nền
        // Thêm nhãn hiển thị tên người dùng
        usernameLabel = new JLabel( username+" vs " + opponentName); // Khởi tạo JLabel với tên người dùng
        usernameLabel.setForeground(Color.BLACK); // Màu chữ
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 16)); // Đặt phông chữ
        usernameLabel.setBounds(0, 10, 570, 30); // Đặt vị trí và kích thước
        usernameLabel.setHorizontalAlignment(SwingConstants.CENTER); // Căn giữa
        backgroundPanel.add(usernameLabel);

        // Thêm nhãn chữ "Điểm:"
        scoreTextLabel = new JLabel(username+":");
        scoreTextLabel.setForeground(Color.BLACK); // Màu chữ
        scoreTextLabel.setBounds(15, 10, 50, 20); // Vị trí của nhãn "Điểm"
        backgroundPanel.add(scoreTextLabel);

        // Thêm nhãn số điểm
        scoreLabel = new JLabel(String.valueOf(score));
        scoreLabel.setForeground(Color.BLACK); // Màu chữ
        scoreLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        scoreLabel.setBounds(65, 10, 50, 20); // Vị trí của nhãn số điểm
        backgroundPanel.add(scoreLabel);

        // Thêm nhãn chữ "Điểm đối thủ:"
        opponentScoreTextLabel = new JLabel(opponentName+":");
        opponentScoreTextLabel.setForeground(Color.BLACK); // Màu chữ
        opponentScoreTextLabel.setBounds(410, 10, 100, 20); // Vị trí của nhãn "Điểm đối thủ"
        backgroundPanel.add(opponentScoreTextLabel);

        // Thêm nhãn số điểm đối thủ
        opponentScoreLabel = new JLabel(String.valueOf(opponentScore));
        opponentScoreLabel.setForeground(Color.BLACK); // Màu chữ
        opponentScoreLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        opponentScoreLabel.setBounds(510, 10, 50, 20); // Vị trí của nhãn số điểm đối thủ
        backgroundPanel.add(opponentScoreLabel);

        // Tạo hình vuông đầu tiên với hình ảnh
        square1 = new ImageSquare("C:\\Users\\admin\\Documents\\NetBeansProjects\\LoginSocket\\src\\View\\images\\caithung.png", 200, 150);
        square1.setBounds(250, 400, 200, 150);
        backgroundPanel.add(square1);

        // Tạo hình vuông thứ hai với hình ảnh
        square2 = new ImageSquare("C:\\Users\\admin\\Documents\\NetBeansProjects\\LoginSocket\\src\\View\\images\\caithung.png", 200, 150);
        square2.setBounds(50, 400, 200, 150);
        backgroundPanel.add(square2);

        // Tạo mảng giá trị ngẫu nhiên 0 hoặc 1
        int[] values = generateRandomValues(40);

        // Tạo các hình tròn dựa trên giá trị trong mảng
        for (int i = 0; i < 40; i++) {
            int x = (i % 10) * 50 + 50; // Tính toán vị trí x
            int y = (i / 10) * 50 + 50; // Tính toán vị trí y

            String imagePath;
            if (values[i] == 0) {
                imagePath = "C:\\Users\\admin\\Documents\\NetBeansProjects\\LoginSocket\\src\\View\\images\\caphe.png"; // Hạt cà phê
            } else {
                imagePath = "C:\\Users\\admin\\Documents\\NetBeansProjects\\LoginSocket\\src\\View\\images\\daunanh.png"; // Hạt đậu nành
            }
            DraggableCircle circle = new DraggableCircle(imagePath, 40, values[i]); // Sử dụng lớp DraggableCircle
            circle.setBounds(x, y, 40, 40);
            circles.add(circle);
            backgroundPanel.add(circle);
        }

        setVisible(true);
    }

    private int[] generateRandomValues(int size) {
        Random random = new Random();
        int[] values = new int[size];
        for (int i = 0; i < size; i++) {
            values[i] = random.nextInt(2); // Tạo giá trị ngẫu nhiên 0 hoặc 1
        }
        return values;
    }

    private void animateScoreLabel() {
        // Tăng kích thước
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 25));

        // Tạo timer để thu nhỏ lại sau 300ms
        Timer timer = new Timer(300, e -> {
            scoreLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        });
        timer.setRepeats(false);
        timer.start();
    }

    // Phương thức cập nhật điểm của đối thủ
    public void updateOpponentScore(int points) {

    }

    public static void main(String[] args) {
        new CircleSquareGame("hello" , "hello1");
    }

    // Lớp ImageSquare để hiển thị hình vuông với hình ảnh
    class ImageSquare extends JComponent {

        private Image image;
        private int width, height;

        public ImageSquare(String imagePath, int width, int height) {
            this.width = width;
            this.height = height;
            ImageIcon icon = new ImageIcon(imagePath); // Tải hình ảnh từ đường dẫn
            image = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH); // Chỉnh kích thước
            setBounds(0, 0, width, height); // Thiết lập kích thước hình vuông
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(image, 0, 0, this); // Vẽ hình ảnh
        }

        public void showPlusOne() {
            Graphics g = getGraphics();
            g.setColor(Color.GREEN);
            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("+1", width - 50, height / 2 + 10);

            // Tạo timer để xóa "+1" sau 1 giây
            Timer timer = new Timer(200, e -> repaint());
            timer.setRepeats(false);
            timer.start();
        }
    }

    // Lớp hình tròn có khả năng kéo
    class DraggableCircle extends JComponent {

        private Image image;
        private Point originalLocation;
        private int value; // Thêm thuộc tính giá trị 0 hoặc 1

        public DraggableCircle(String imagePath, int diameter, int value) {
            this.value = value; // Gán giá trị
            ImageIcon icon = new ImageIcon(imagePath); // Tải hình ảnh từ đường dẫn
            image = icon.getImage().getScaledInstance(diameter, diameter, Image.SCALE_SMOOTH); // Chỉnh kích thước
            setBounds(0, 0, diameter, diameter); // Thiết lập kích thước hình tròn
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    originalLocation = e.getPoint(); // Lưu vị trí gốc
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    // Kiểm tra xem có thả vào hình vuông nào không
                    if (square1.getBounds().intersects(getBounds())) {
                        // Nếu là giỏ đậu nành (giỏ 1) và giá trị của hạt là 1 (đậu nành)
                        if (value == 1) {
                            score++; // Tăng điểm cho người chơi
                            scoreLabel.setText(String.valueOf(score));
                            square1.showPlusOne(); // Hiển thị "+1" trên hình vuông
                        } else {
                            opponentScore++; // Nếu sai, cộng điểm cho đối thủ
                            opponentScoreLabel.setText(String.valueOf(opponentScore));
                        }
                        animateScoreLabel(); // Thêm hiệu ứng cho nhãn số điểm
                        setVisible(false); // Ẩn hình tròn khi đưa vào hình vuông

                    } else if (square2.getBounds().intersects(getBounds())) {
                        // Nếu là giỏ cà phê (giỏ 2) và giá trị của hạt là 0 (cà phê)
                        if (value == 0) {
                            score++; // Tăng điểm cho người chơi
                            scoreLabel.setText(String.valueOf(score));
                            square2.showPlusOne(); // Hiển thị "+1" trên hình vuông
                        } else {
                            opponentScore++; // Nếu sai, cộng điểm cho đối thủ
                            opponentScoreLabel.setText(String.valueOf(opponentScore));
                        }
                        animateScoreLabel();
                        setVisible(false);
                    }
                }

            });

            addMouseMotionListener(new MouseAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    // Di chuyển hình tròn theo chuột
                    int newX = getX() + e.getX() - originalLocation.x;
                    int newY = getY() + e.getY() - originalLocation.y;

                    // Cập nhật vị trí
                    setLocation(newX, newY); // Cập nhật vị trí
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(image, 0, 0, this); // Vẽ hình ảnh
        }
    }
}
