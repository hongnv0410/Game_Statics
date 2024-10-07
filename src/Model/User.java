package Model;

import java.io.Serializable;

public class User implements Serializable {

    private String userName;
    private String password;
    private Integer score;

    public User() {
    }

    public User(String username, String password) {
        this.userName = username;
        this.password = password;
        this.score = 0;
    }

    public String getPassword() {
        return password;

    }

    public void setPassword(String password) {
        this.password = password;

    }
    public Integer getScore() {
        return score;

    }

    public String getUserName() {
        return userName;

    }

    public void setUserName(String userName) {
        this.userName = userName;

    }
}
