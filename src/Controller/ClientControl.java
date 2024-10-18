package Controller;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import Model.User;
import View.CircleSquareGame;
import View.ClientView;
import java.io.IOException;
import javax.swing.JOptionPane;

public class ClientControl {

    private Socket mySocket;
    private String serverHost = "localhost";
    private int serverPort = 8889;
    private ObjectOutputStream oos; // Tái sử dụng chung cho tất cả các yêu cầu
    private ObjectInputStream ois;  // Tái sử dụng chung cho tất cả các yêu cầu
    private User user;
    public List<String> RankList = new ArrayList<>();
    public List<String> onlineUsers = new ArrayList<>();
    private CircleSquareGame CSG = null;

    public ClientControl() {
    }

    // Mở kết nối đến server và khởi tạo các luồng IO
    public boolean openConnection() {
        try {
            mySocket = new Socket(serverHost, serverPort);
            oos = new ObjectOutputStream(mySocket.getOutputStream());
            ois = new ObjectInputStream(mySocket.getInputStream());
            return true; // Kết nối thành công
        } catch (Exception ex) {
            ex.printStackTrace();
            return false; // Kết nối không thành công
        }
    }

    // Gửi thông tin người dùng
    public boolean sendData(User user) {
        return sendCommand(user);
    }

    // Tạo người dùng mới
    public boolean createUser(String username, String password) {
        String command = "createUser:" + username + ":" + password; // Tạo chuỗi lệnh
        return sendCommand(command);
    }

    // Gửi lệnh đến server
    private boolean sendCommand(Object command) {
        try {
            oos.writeObject(command);
            oos.flush(); // Đảm bảo dữ liệu được gửi đi
            return true; // Gửi thành công
        } catch (Exception ex) {
            ex.printStackTrace();
            return false; // Gửi không thành công
        }
    }

    // Nhận dữ liệu phản hồi từ server
    public String receiveData() {
        try {
            Object o = ois.readObject(); // Nhận phản hồi từ server
            if (o instanceof String) {
                return (String) o; // Trả về kết quả
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null; // Không có dữ liệu
    }

    // Đóng kết nối với server
    public boolean closeConnection() {
        try {
            if (ois != null) {
                ois.close(); // Đóng luồng đầu vào
            }
            if (oos != null) {
                oos.close(); // Đóng luồng đầu ra
            }
            if (mySocket != null && !mySocket.isClosed()) {
                mySocket.close(); // Đóng socket
            }
            return true; // Đóng thành công
        } catch (Exception ex) {
            ex.printStackTrace();
            return false; // Đóng không thành công
        }
    }

    // Tìm kiếm người dùng
    public List<User> searchUser(String username) {
        List<User> userList = new ArrayList<>();
        if (sendCommand("search:" + username)) {
            try {
                Object response = ois.readObject();
                if (response instanceof List) {
                    userList = (List<User>) response; // Ép kiểu phản hồi về danh sách
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return userList; // Trả về danh sách người dùng
    }

    // Lấy danh sách xếp hạng
//    public List<Object[]> getRankList() {
//        List<Object[]> rankList = new ArrayList<>();
//        if (sendCommand("getRankList")) {
//            try {
//                Object response = ois.readObject(); // Đọc phản hồi từ server
//                if (response instanceof List) {
//                    rankList = (List<Object[]>) response; // Ép kiểu phản hồi về danh sách
//                }
//            } catch (Exception ex) {
//                ex.printStackTrace();
//            }
//        }
//        return rankList; // Trả về danh sách xếp hạng
//    }
    public void getRankList() {
        sendCommand("getRankList");
    }

    public boolean sendInvite(String invitee) {
        try {
            oos.writeObject("invite:" + invitee); // Gửi yêu cầu mời chơi tới server
            oos.flush();
            return true;
        } catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }

    // Lắng nghe lời mời từ server trong một Thread riêng
    public void listenForInvites(String username) {
        new Thread(() -> {
            while (true) {
                try {
                    Object response = ois.readObject(); // Nhận dữ liệu từ server

                    // Xử lý lời mời
                    if (response instanceof String) {
                        String command = (String) response;
                        if (command.startsWith("invite:")) {
                            String inviter = command.split(":")[1]; // Lấy tên người mời
                            handleInvite(inviter); // Xử lý lời mời chơi
                        }
                        if (command.startsWith("inviteResponse:")) {
                            String invitee = command.split(":")[1]; // Người được mời
                            String inviteeResponse = command.split(":")[2]; // Phản hồi ("accept" hoặc "decline")
                            handleInviteResponse(invitee, inviteeResponse); // Xử lý phản hồi lời mời
                        }
                        if (command.startsWith("gameStart:")) {
                            // Tách tên người chơi đối thủ từ thông báo
                            String opponent = command.split(":")[1];
                            CSG = new CircleSquareGame(username, opponent);
                        }
                        if (command.startsWith("scoreOPP:")) {
                            int scoreOPP = Integer.parseInt(command.split(":")[1]);
                            System.out.println("da nhan diem " + scoreOPP);
                            CSG.updateScoreOpp(scoreOPP);
                        }
                        if (command.startsWith("timeOPP:")) {
                            int timeOPP = Integer.parseInt(command.split(":")[1]);
                            System.out.println("da nhan thoi gian hoan thanh" + timeOPP);
                            CSG.updateStatusOpponent(timeOPP);
                        }
                        if (command.startsWith("Notification")) {
                            CSG.opponentOut();
                        }
                        if (command.equals("getOnlineUsers")) {
                            onlineUsers = (List<String>) ois.readObject();
                        }
                        if (command.equals("getRankList")) {
                            RankList = (List<String>) ois.readObject();
                        }
                    } 
                } catch (IOException e) {
                    System.err.println("Lỗi kết nối: " + e.getMessage());
                    e.printStackTrace();
                    break; // Thoát vòng lặp nếu có lỗi kết nối
                } catch (ClassNotFoundException e) {
                    System.err.println("Lỗi khi nhận đối tượng: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }).start();
    }

    // Xử lý lời mời chơi
    private void handleInvite(String inviter) {
        int response = JOptionPane.showConfirmDialog(null, "Bạn đã nhận được lời mời chơi từ " + inviter + ". Bạn có chấp nhận không?", "Lời mời chơi", JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION) {
            sendResponseToInvite(inviter, "accept");
        } else {
            sendResponseToInvite(inviter, "decline");
        }
    }

    private void sendResponseToInvite(String inviter, String response) {
        try {
            oos.writeObject("inviteResponse:" + inviter + ":" + response); // Gửi phản hồi lời mời về server
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Xử lý phản hồi lời mời
    private void handleInviteResponse(String invitee, String inviteeResponse) {
        if (inviteeResponse.equals("accept")) {
            JOptionPane.showMessageDialog(null, invitee + " đã chấp nhận lời mời của bạn!", "Lời mời chơi", JOptionPane.INFORMATION_MESSAGE);
            // Bắt đầu trận đấu
        } else {
            JOptionPane.showMessageDialog(null, invitee + " đã từ chối lời mời của bạn.", "Lời mời chơi", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void getOnlineUser() {
        sendCommand("getOnlineUsers");
    }

    public void sendScore(int score, String opponentName) {
        try {
            System.out.println("sendScore:" + score + ":" + opponentName);
            oos.writeObject("sendScore:" + score + ":" + opponentName); // Gửi phản hồi lời mời về server
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendTime(int time, String opponentName) {
        try {
            System.out.println("sendTime:" + time + ":" + opponentName);
            oos.writeObject("sendTime:" + time + ":" + opponentName); // Gửi thời gian chơi về server
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void notifyOpponentOfExit(String opponentName) {
        try {
            System.out.println("notifyExit:" + opponentName);
            oos.writeObject("notifyExit:" + opponentName); // Gửi thời gian chơi về server
            oos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

// Class chạy client
class ClientRun {

    public static void main(String[] args) {
        ClientView.main(args);
    }
}
