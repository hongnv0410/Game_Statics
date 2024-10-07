package View;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TamNhatThocGUI extends JFrame {

    private JPanel mainPanel;
    private JLabel blackBasket;
    private JLabel redBasket;
    private List<JLabel> grainLabels;
    private int score = 0;
    private JLabel scoreLabel; // Hiển thị điểm

    public TamNhatThocGUI() {
        setTitle("Tấm Nhặt Thóc");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainPanel = new JPanel();
        mainPanel.setLayout(null);

        createBaskets();
        createGrains();
        createScoreLabel(); // Tạo label hiển thị điểm

        setContentPane(mainPanel);
        setVisible(true);
    }

    private void createBaskets() {
        // Giỏ đen
        blackBasket = new JLabel("Giỏ Đen");
        blackBasket.setBounds(100, 400, 100, 30);
        blackBasket.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        blackBasket.setTransferHandler(new GrainTransferHandler("Đen"));
        mainPanel.add(blackBasket);

        // Giỏ đỏ
        redBasket = new JLabel("Giỏ Đỏ");
        redBasket.setBounds(600, 400, 100, 30);
        redBasket.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        redBasket.setTransferHandler(new GrainTransferHandler("Đỏ"));
        mainPanel.add(redBasket);
    }

    private void createGrains() {
        grainLabels = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < 30; i++) {
            String color = random.nextBoolean() ? "Đen" : "Đỏ";
            JLabel grain = new JLabel();
            grain.setOpaque(true);
            grain.setBackground(color.equals("Đen") ? Color.BLACK : Color.RED);
            grain.setBounds(50 + (i % 10) * 70, 50 + (i / 10) * 50, 50, 50);
            grain.setHorizontalAlignment(SwingConstants.CENTER);
            grain.setText(color);
            grain.setForeground(Color.WHITE);
            grain.setBorder(BorderFactory.createLineBorder(Color.GRAY));
            grain.setTransferHandler(new TransferHandler("text") {
                @Override
                protected Transferable createTransferable(JComponent c) {
                    return new StringSelection(color); // Gán giá trị đúng là màu sắc
                }
            });

            // Kéo thả
            grain.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    JLabel grainLabel = (JLabel) e.getSource();
                    grainLabel.setText(""); // Xóa văn bản để không hiển thị
                    grainLabel.setBackground(Color.LIGHT_GRAY); // Đổi màu để dễ nhận diện
                    grainLabel.getTransferHandler().exportAsDrag(grainLabel, e, TransferHandler.COPY);
                }
            });
            grainLabels.add(grain);
            mainPanel.add(grain);
        }
    }

    private void createScoreLabel() {
        scoreLabel = new JLabel("Điểm: 0");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 24));
        scoreLabel.setBounds(350, 10, 200, 30);
        mainPanel.add(scoreLabel);
    }

    private class GrainTransferHandler extends TransferHandler {

        private final String expectedColor;

        public GrainTransferHandler(String expectedColor) {
            this.expectedColor = expectedColor;
        }

        @Override
        public boolean canImport(TransferHandler.TransferSupport support) {
            return support.isDataFlavorSupported(DataFlavor.stringFlavor);
        }

        @Override
        public boolean importData(TransferHandler.TransferSupport support) {
            if (!canImport(support)) {
                return false;
            }

            try {
                String data = (String) support.getTransferable().getTransferData(DataFlavor.stringFlavor);
                System.out.println("Data nhận được: " + data); // In ra để kiểm tra
                if (data.equals(expectedColor)) {
                    score++;
                    scoreLabel.setText("Điểm: " + score); // Cập nhật điểm
                    System.out.println("Bạn đã phân loại đúng hạt " + expectedColor + "! Điểm hiện tại: " + score);
                } else {
                    System.out.println("Phân loại sai! Hạt này không phải hạt " + expectedColor + ".");
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    }

//    public static void main(String[] args) {
//        SwingUtilities.invokeLater(TamNhatThocGUI::new);
//    }
}
