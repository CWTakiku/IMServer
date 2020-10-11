package po;

public class ShakeHands {
    private String userId;
    private String token;

    public ShakeHands(String userId, String token) {
        this.userId = userId;
        this.token = token;
    }

    public ShakeHands() {
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
