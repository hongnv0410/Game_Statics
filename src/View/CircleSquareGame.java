package View;

import Controller.ClientControl;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Random;

public class CircleSquareGame extends JFrame {

    private int score = 0;
    private int opponentScore = 0;
    private JLabel scoreLabel;
    private JLabel opponentScoreLabel;
    private JLabel scoreTextLabel;
    private JLabel opponentScoreTextLabel;
    private ImageSquare square1, square2;
    private JLabel usernameLabel;
    private ArrayList<DraggableCircle> circles = new ArrayList<>();
    private Image backgroundImage;
    private String username;
    private String opponentName;
    private ClientControl ClientCtr;
    private JLabel timerLabel; // Nhãn đếm ngược thời gian
    private Timer gameTimer; // Timer cho đếm ngược
    private int timeRemaining = 60; // Thời gian đếm ngược (60 giây)
    private JButton finishButton; // Nút "Hoàn thành"
    private boolean opponentFinished = false; // Biến lưu trạng thái đối thủ đã hoàn thành
    private boolean userFinished = false;
    private int opponentFinishTime = -1; // Thời gian hoàn thành của đối thủ (nếu có)
    private JLabel opponentFinishTimeLabel; // Nhãn để hiển thị thời gian hoàn thành của đối thủ
    private boolean checkOpponenntExit = true;

    public CircleSquareGame(String username, String opponentName) {
        ClientCtr = new ClientControl();
        ClientCtr.openConnection();
        this.username = username;
        this.opponentName = opponentName;

        // Thiết lập JFrame
        setTitle("Trận đấu giữa " + username + " và " + opponentName);
        setSize(570, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        setLocationRelativeTo(null);

        // Tải hình ảnh nền
        backgroundImage = new ImageIcon("D:\\laptrinhmang\\src\\View\\images\\hinhnen.png").getImage();

        // Tạo JPanel làm nền
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

                // Vẽ ô điểm
                g.setColor(new Color(255, 255, 255, 200));
                g.fillRoundRect(10, 5, 80, 30, 10, 10);
                g.fillRoundRect(400, 5, 140, 30, 10, 10);
            }
        };
        backgroundPanel.setLayout(null);
        setContentPane(backgroundPanel);

        // Nhãn tên người dùng
        usernameLabel = new JLabel(username + " vs " + opponentName);
        usernameLabel.setForeground(Color.BLACK);
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        usernameLabel.setBounds(0, 5, 570, 30);
        usernameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        backgroundPanel.add(usernameLabel);

        // Nhãn "Điểm:"
        scoreTextLabel = new JLabel(username + ":");
        scoreTextLabel.setForeground(Color.BLACK);
        scoreTextLabel.setBounds(15, 10, 50, 20);
        backgroundPanel.add(scoreTextLabel);

        // Nhãn số điểm
        scoreLabel = new JLabel(String.valueOf(score));
        scoreLabel.setForeground(Color.BLACK);
        scoreLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        scoreLabel.setBounds(65, 10, 50, 20);
        backgroundPanel.add(scoreLabel);

        // Nhãn "Điểm đối thủ:"
        opponentScoreTextLabel = new JLabel(opponentName + ":");
        opponentScoreTextLabel.setForeground(Color.BLACK);
        opponentScoreTextLabel.setBounds(410, 10, 100, 20);
        backgroundPanel.add(opponentScoreTextLabel);

        // Nhãn số điểm đối thủ
        opponentScoreLabel = new JLabel(String.valueOf(opponentScore));
        opponentScoreLabel.setForeground(Color.BLACK);
        opponentScoreLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        opponentScoreLabel.setBounds(510, 10, 50, 20);
        backgroundPanel.add(opponentScoreLabel);

        // Nhãn thời gian đếm ngược
        timerLabel = new JLabel("Thời gian: " + timeRemaining + " giây");
        timerLabel.setForeground(Color.RED);
        timerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        timerLabel.setBounds(0, 25, 570, 30);
        timerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        backgroundPanel.add(timerLabel);

        // Nhãn hiển thị thời gian hoàn thành của đối thủ
        opponentFinishTimeLabel = new JLabel();
        opponentFinishTimeLabel.setForeground(Color.BLUE);
        opponentFinishTimeLabel.setBounds(370, 30, 200, 30);
        opponentFinishTimeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        backgroundPanel.add(opponentFinishTimeLabel);

        // Nút "Hoàn thành"
        finishButton = new JButton("Hoàn thành");
        finishButton.setBounds(200, 540, 150, 20);
        finishButton.setFont(new Font("Arial", Font.PLAIN, 16));
        finishButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                endGame();
            }
        });
        backgroundPanel.add(finishButton);

        // Tạo hình vuông với hình ảnh
        square1 = new ImageSquare("C:\\Users\\admin\\Game_Statics\\src\\View\\images\\caithung.png", 200, 150);
        square1.setBounds(250, 400, 200, 150);
        backgroundPanel.add(square1);

        square2 = new ImageSquare("C:\\Users\\admin\\Game_Statics\\src\\View\\images\\caithung.png", 200, 150);
        square2.setBounds(50, 400, 200, 150);
        backgroundPanel.add(square2);

        // Khởi tạo mảng ngẫu nhiên và các hình tròn
        int[] values = generateRandomValues(40);
        for (int i = 0; i < 40; i++) {
            int x = (i % 10) * 50 + 50;
            int y = (i / 10) * 50 + 50;
            String imagePath = values[i] == 0 ? "C:\\Users\\admin\\Game_Statics\\src\\View\\images\\caphe.png" : "C:\\Users\\admin\\Game_Statics\\src\\View\\images\\daunanh.png";
            DraggableCircle circle = new DraggableCircle(imagePath, 40, values[i]);
            circle.setBounds(x, y, 40, 40);
            circles.add(circle);
            backgroundPanel.add(circle);
        }

        // Khởi tạo timer cho đếm ngược
        gameTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timeRemaining--;
                timerLabel.setText("Thời gian: " + timeRemaining + " giây");

                if (timeRemaining <= 0) {
                    gameTimer.stop();
                    endGame();
                }
            }
        });
        gameTimer.start();
        
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);  // Ngăn hành động mặc định khi nhấn "X"

        // Lắng nghe sự kiện khi người dùng nhấn nút "X"
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Hiển thị hộp thoại xác nhận thoát game
                int confirm = JOptionPane.showConfirmDialog(
                        CircleSquareGame.this,
                        "Bạn có chắc chắn muốn thoát trò chơi?",
                        "Xác nhận thoát",
                        JOptionPane.YES_NO_OPTION
                );
                
                if (confirm == JOptionPane.YES_OPTION) {
                    // Thực hiện hành động khi người chơi chọn thoát, ví dụ:
                    ClientCtr.notifyOpponentOfExit(opponentName);  // Thông báo cho đối thủ
                    dispose();  // Đóng cửa sổ game
                }
            }
        });

        setVisible(true);
    }

    // Phương thức kết thúc trò chơi và gửi thời gian cho đối thủ
    private void endGame() {
        gameTimer.stop();
        userFinished = true;
        if(checkOpponenntExit){
            sendFinishTimeToOpponent();
            if (opponentFinished) {
                // Đối thủ đã hoàn thành -> So sánh điểm và thời gian
                String result1 = compareScores();
                int result = JOptionPane.showOptionDialog(this,
                    result1+"\nBấm 'Quay lại' để thoát khỏi game.",
               "Thoát game",
            JOptionPane.DEFAULT_OPTION,
           JOptionPane.INFORMATION_MESSAGE,
                null,
                    new Object[]{"Quay lại"},  // Tạo nút "Quay lại"
          "Quay lại"
                );

                if (result == JOptionPane.OK_OPTION) {
                    // Đóng cửa sổ game khi người chơi bấm "Quay lại"
                    dispose();
                }
            } else {
                // Đối thủ chưa hoàn thành -> Hiển thị thời gian hoàn thành của mình
                JOptionPane.showMessageDialog(this, 
                    "Trò chơi kết thúc!\nĐiểm của bạn: " + score + "\nThời gian còn lại: " + timeRemaining + " giây\nBạn hãy đợi đối thủ chơi xong");
            }
        disableCircles(); // Vô hiệu hóa các hình tròn
        }
        else{
            int result = JOptionPane.showOptionDialog(this,
                "Trò chơi kết thúc!\nĐiểm của bạn: " + score + "\nThời gian còn lại: " + timeRemaining + " giây\n"
                 +"Bấm 'Quay lại' để thoát khỏi game.",
            "Thoát game",
        JOptionPane.DEFAULT_OPTION,
       JOptionPane.INFORMATION_MESSAGE,
            null,
                new Object[]{"Quay lại"},  // Tạo nút "Quay lại"
      "Quay lại"
            );

            if (result == JOptionPane.OK_OPTION) {
                // Đóng cửa sổ game khi người chơi bấm "Quay lại"
                dispose();
            }
        }
        
    }
    public void updateStatusOpponent(int time){
        opponentFinished = true; // Biến lưu trạng thái đối thủ đã hoàn thành
        opponentFinishTime = time;
        
        if (userFinished) {
            // Mình đã hoàn thành -> So sánh điểm và thời gian
            String result1 = compareScores();
            int result = JOptionPane.showOptionDialog(this,
                    result1+"\nBấm 'Quay lại' để thoát khỏi game.",
               "Thoát game",
            JOptionPane.DEFAULT_OPTION,
           JOptionPane.INFORMATION_MESSAGE,
                null,
                    new Object[]{"Quay lại"},  // Tạo nút "Quay lại"
          "Quay lại"
                );

            if (result == JOptionPane.OK_OPTION) {
                // Đóng cửa sổ game khi người chơi bấm "Quay lại"
                dispose();
            }
        }
    }
    
    // Gửi thời gian hoàn thành cho đối thủ
    private void sendFinishTimeToOpponent() {
        // Gửi thời gian hoàn thành của người chơi hiện tại cho đối thủ
        ClientCtr.sendTime(timeRemaining,opponentName);

        if (opponentFinished) {
            opponentFinishTimeLabel.setText("Hoàn thành trong:"+ opponentFinishTime + " giây");
            Timer timer = new Timer(1500, e -> opponentFinishTimeLabel.setText("")); // 2000ms = 2 giây
            timer.setRepeats(false); // Không lặp lại
            timer.start(); // Bắt đầu đếm ngược
        }
    }

    // So sánh điểm và thời gian giữa 2 người chơi để quyết định người thắng
    private String compareScores() {
        if (score > opponentScore) {
            return "Bạn thắng với điểm số cao hơn!";
        } else if (score < opponentScore) {
            return opponentName + " thắng với điểm số cao hơn!";
        } else {
            if (timeRemaining > opponentFinishTime) {
                return "Bạn thắng vì hoàn thành nhanh hơn!";
            } else if(timeRemaining < opponentFinishTime){
                return opponentName + " thắng vì hoàn thành nhanh hơn!";
            }
            else{
               return  "Bạn và"+opponentName+" hòa nhau";
            }   
        }
    }

    // Vô hiệu hóa các hình tròn khi kết thúc trò chơi
    private void disableCircles() {
        for (DraggableCircle circle : circles) {
            circle.setEnabled(false); // Không cho kéo nữa
        }
    }

    private int[] generateRandomValues(int size) {
        Random random = new Random();
        int[] values = new int[size];
        for (int i = 0; i < size; i++) {
            values[i] = random.nextInt(2);
        }
        return values;
    }

    public static void main(String[] args) {
        new CircleSquareGame("hello", "hello1");
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

    public void updateScoreOpp(int score) {
        opponentScoreLabel.setText(String.valueOf(score));
    }
    public void opponentOut() {
        checkOpponenntExit = false;
        int result = JOptionPane.showConfirmDialog(
            this,
            "Đối thủ đã thoát trận. Bạn có muốn rời khỏi phòng không?",
            "Đối thủ thoát trận",
            JOptionPane.YES_NO_OPTION
        );

        if (result == JOptionPane.YES_OPTION) {
            // Nếu người chơi chọn "Yes", đóng cửa sổ game
            dispose();  // Đóng cửa sổ game
        } 
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
                            ClientCtr.sendScore(score, opponentName);
                        }
                        animateScoreLabel(); // Thêm hiệu ứng cho nhãn số điểm
                        setVisible(false); // Ẩn hình tròn khi đưa vào hình vuông

                    } else if (square2.getBounds().intersects(getBounds())) {
                        // Nếu là giỏ cà phê (giỏ 2) và giá trị của hạt là 0 (cà phê)
                        if (value == 0) {
                            score++; // Tăng điểm cho người chơi
                            scoreLabel.setText(String.valueOf(score));
                            square2.showPlusOne(); // Hiển thị "+1" trên hình vuông
                            ClientCtr.sendScore(score, opponentName);
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
