package Controller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import Model.User;

public class ServerControl {

    private Connection con; // Kết nối với cơ sở dữ liệu
    private ServerSocket myServer; // Cổng máy chủ
    private final int serverPort = 8889; // Số cổng cho máy chủ
    private List<ClientHandler> clients = new ArrayList<>(); // Danh sách các khách hàng kết nối
    private ExecutorService pool = Executors.newFixedThreadPool(10); // Hồ bơi luồng để xử lý khách hàng

    public ServerControl() {
        getDBConnection("test", "root", "123456"); // Kết nối đến cơ sở dữ liệu
        openServer(serverPort); // Mở máy chủ
        while (true) {
            listenForClients(); // Liên tục lắng nghe các kết nối từ khách hàng
        }
    }

    private void getDBConnection(String dbName, String username, String password) {
        String dbUrl = "jdbc:mysql://localhost:3306/" + dbName; // URL của cơ sở dữ liệu
        String dbClass = "com.mysql.cj.jdbc.Driver"; // Lớp driver JDBC
        try {
            Class.forName(dbClass); // Nạp driver
            con = DriverManager.getConnection(dbUrl, username, password); // Thiết lập kết nối
        } catch (Exception e) {
            e.printStackTrace(); // Xử lý lỗi
        }
    }

    private void openServer(int portNumber) {
        try {
            myServer = new ServerSocket(portNumber); // Mở cổng máy chủ
            System.out.println("Server started on port: " + portNumber); // Thông báo khởi động máy chủ
        } catch (IOException e) {
            e.printStackTrace(); // Xử lý lỗi
        }
    }

    private void listenForClients() {
        try {
            Socket clientSocket = myServer.accept(); // Chấp nhận kết nối từ khách hàng
            System.out.println("Client connected: " + clientSocket.getInetAddress()); // Thông báo kết nối thành công
            ClientHandler clientHandler = new ClientHandler(clientSocket); // Tạo đối tượng xử lý cho khách hàng
            clients.add(clientHandler); // Thêm khách hàng vào danh sách
            pool.execute(clientHandler); // Chạy đối tượng xử lý trong một luồng riêng
        } catch (IOException e) {
            e.printStackTrace(); // Xử lý lỗi
        }
    }

    private class ClientHandler implements Runnable {

        private Socket clientSocket; // Kết nối khách hàng
        private ObjectOutputStream oos; // Đầu ra đối tượng
        private ObjectInputStream ois; // Đầu vào đối tượng
        private User user; // Thông tin người dùng
        private boolean IsLogin = false;
        private boolean IsPlay  = false;
        private String opponentName = null;
        
        public boolean isIsPlay() {
            return IsPlay;
        }

        public void setIsPlay(boolean IsPlay) {
            this.IsPlay = IsPlay;
        }

        public String getOpponentName() {
            return opponentName;
        }

        public void setOpponentName(String opponentName) {
            this.opponentName = opponentName;
        }
        
        public ClientHandler(Socket socket) {
            this.clientSocket = socket; // Lưu kết nối khách hàng
        }

        @Override
        public void run() {
            try {
                oos = new ObjectOutputStream(clientSocket.getOutputStream()); // Khởi tạo đầu ra
                ois = new ObjectInputStream(clientSocket.getInputStream()); // Khởi tạo đầu vào
                while (true) {
                    Object o = ois.readObject(); // Đọc yêu cầu từ khách hàng
                    handleClientRequest(o); // Xử lý yêu cầu
                }
            } catch (Exception e) {
                System.out.println("Da dong ket noi"); // Xử lý lỗi
            } finally {
                closeConnection(); // Đóng kết nối khi hoàn tất
            }
        }

        private void handleClientRequest(Object o) throws Exception {
            if (o instanceof User) {
                user = (User) o; // Chuyển đổi đối tượng thành User
                if (checkUser(user)) {
                    IsLogin = true;
                    oos.writeObject("ok"); // Xác thực thành công
                } else {
                    oos.writeObject("false"); // Xác thực thất bại
                    IsLogin = true;
                }
            } else if (o instanceof String) {
                String command = (String) o; // Chuyển đổi đối tượng thành chuỗi
                if (command.startsWith("createUser:")) {
                    String[] parts = command.split(":"); // Phân tách thông tin
                    String username = parts[1]; // Tên người dùng
                    String password = parts[2]; // Mật khẩu
                    boolean success = createUser(username, password); // Tạo người dùng
                    oos.writeObject(success ? "User created successfully!" : "Username already exists."); // Phản hồi kết quả
                } else if (command.equals("getRankList")) {
                    List<Object[]> rankList = getRankings(); // Lấy danh sách xếp hạng
                    oos.writeObject(rankList); // Gửi danh sách xếp hạng
                } else if (command.equals("getOnlineUsers")) {
                    List<String> onlineUsers = getOnlineUsers();
                    oos.writeObject(onlineUsers); // Gửi danh sách người dùng online
                    oos.flush(); // Đảm bảo dữ liệu được gửi
                } else if (command.startsWith("invite:")) {
                    String invitee = command.split(":")[1]; // Người được mời
                    sendInviteToUser(this.user.getUserName(), invitee); // Gửi lời mời từ người chơi hiện tại tới invitee
                } else if (command.startsWith("inviteResponse:")) {
                    String[] parts = command.split(":");
                    String inviter = parts[1]; // Người đã mời
                    String response = parts[2]; // Phản hồi ("accept" hoặc "decline")
                    sendInviteResponseToInviter(inviter, this.user.getUserName(), response); // Gửi phản hồi tới người mời
                }else if(command.startsWith("sendScore:")){
                    int score =Integer.parseInt(command.split(":")[1]) ;
                    String opponentName = command.split(":")[2];
                    sendInviteScore(score, opponentName);
                }
                else if(command.startsWith("sendTime:")){
                    int time =Integer.parseInt(command.split(":")[1]) ;
                    String opponentName1 = command.split(":")[2];
                    sendInviteTime(time, opponentName1);
                    IsPlay = false;
                    opponentName = null;
                }
                else if(command.startsWith("notifyExit:")){
                    String opponentName1 = command.split(":")[1];
                    System.out.println("gui cho"+opponentName1);
                    sendNotification(opponentName1);
                    IsPlay = false;
                    opponentName = null;
                }

            }
        }

        private List<String> getOnlineUsers() {
            List<String> onlineUsernames = new ArrayList<>(); // Danh sách người dùng online
            for (ClientHandler client : clients) {
                if (client.user != null && client.IsLogin == true) {
                    onlineUsernames.add(client.user.getUserName()); // Thêm tên người dùng vào danh sách
                }
            }
            return onlineUsernames; // Trả về danh sách người dùng online
        }

        private boolean checkUser(User user) throws Exception {
            String query = "SELECT * FROM users WHERE username ='" + user.getUserName() + "' AND password ='" + user.getPassword() + "'"; // Truy vấn để xác thực người dùng
            try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
                return rs.next(); // Trả về true nếu người dùng tồn tại
            }
        }

        private boolean createUser(String username, String password) {
            try {
                String checkQuery = "SELECT * FROM users WHERE username ='" + username + "'"; // Kiểm tra xem tên người dùng đã tồn tại
                Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(checkQuery);
                if (rs.next()) {
                    return false; // Tên người dùng đã tồn tại
                }

                String insertQuery = "INSERT INTO users (username, password) VALUES ('" + username + "', '" + password + "')"; // Thêm người dùng mới
                stmt.executeUpdate(insertQuery); // Thực hiện truy vấn thêm
                return true; // Tạo người dùng thành công
            } catch (Exception e) {
                e.printStackTrace(); // Xử lý lỗi
                return false; // Thất bại khi tạo người dùng
            }
        }

        private List<Object[]> getRankings() throws Exception {
            List<Object[]> rankingList = new ArrayList<>(); // Danh sách xếp hạng
            String query = "SELECT username, score FROM users ORDER BY score DESC"; // Truy vấn lấy danh sách xếp hạng
            try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
                while (rs.next()) {
                    rankingList.add(new Object[]{rs.getString("username"), rs.getInt("score")}); // Thêm người dùng và điểm vào danh sách
                }
            }
            return rankingList; // Trả về danh sách xếp hạng
        }

        private void closeConnection() {
            try {
                if(IsPlay == true)
                {
                    sendNotification(opponentName);
                    IsPlay = false;
                    opponentName = null;
                }
                if (ois != null) {
                    ois.close(); // Đóng ObjectInputStream
                }
                if (oos != null) {
                    oos.close(); // Đóng ObjectOutputStream
                }
                if (clientSocket != null && !clientSocket.isClosed()) {
                    clientSocket.close(); // Đóng socket nếu chưa đóng
                }
                
                clients.remove(this); // Xóa client khỏi danh sách quản lý
                System.out.println("Client disconnected: " + clientSocket.getInetAddress()); // Thông báo ngắt kết nối
            } catch (IOException e) {
                e.printStackTrace(); // Xử lý ngoại lệ
            }
        }

        private void sendInviteToUser(String inviter, String invitee) throws IOException {
            for (ClientHandler client : clients) {
                if (client.user != null && client.user.getUserName().equals(invitee)) {
                    client.oos.writeObject("invite:" + inviter);
                    client.oos.flush();
                    System.out.println(inviter + " đã mời " + invitee + " chơi game.");
                    break;
                }
            }
        }

        private void sendInviteResponseToInviter(String inviter, String invitee, String response) throws IOException {
            ClientHandler inviterHandler = null;
            ClientHandler inviteeHandler = null;

            // Tìm kiếm ClientHandler của người mời và người được mời
            for (ClientHandler client : clients) {
                if (client.user != null && client.user.getUserName().equals(inviter)) {
                    inviterHandler = client; // Lưu lại ClientHandler của người mời
                } else if (client.user != null && client.user.getUserName().equals(invitee)) {
                    inviteeHandler = client; // Lưu lại ClientHandler của người được mời
                }
            }

            // Nếu cả hai người chơi đều tồn tại và lời mời được chấp nhận
            if (inviterHandler != null && inviteeHandler != null && response.equals("accept")) {
                // Gửi thông báo tới cả hai người chơi
                inviterHandler.oos.writeObject("gameStart:" + invitee);
                inviteeHandler.oos.writeObject("gameStart:" + inviter);

                inviterHandler.oos.flush();
                inviteeHandler.oos.flush();
                // Thêm thuộc tính đàn chơi game
                inviterHandler.setIsPlay(true); 
                inviteeHandler.setIsPlay(true);
                //Thêm tên đối thủ khi chấp nhận chơi
                inviterHandler.setOpponentName(invitee); 
                inviteeHandler.setOpponentName(inviter);

                System.out.println("Phòng chơi đã được tạo cho " + inviter + " và " + invitee);
            }
        }
        
        private void sendInviteScore(int score, String opponentName) throws IOException{
            for (ClientHandler client : clients) {
                if (client.user != null && client.user.getUserName().equals(opponentName)) {
                    client.oos.writeObject("scoreOPP:" + score);
                    client.oos.flush();
                    return;
                } 
            }
        }
        private void sendInviteTime(int time, String opponentName) throws IOException{
            for (ClientHandler client : clients) {
                if (client.user != null && client.user.getUserName().equals(opponentName)) {
                    client.oos.writeObject("timeOPP:" + time);
                    client.oos.flush();
                    return;
                } 
            }
        }
        private void sendNotification(String opponentName) throws IOException{
            for (ClientHandler client : clients) {
                if (client.user != null && client.user.getUserName().equals(opponentName)) {
                    client.oos.writeObject("Notification");
                    client.oos.flush();
                    return;
                } 
            }
        }
       
    }

    public static void main(String[] args) {
        new ServerControl(); // Khởi động máy chủ
    }
}
