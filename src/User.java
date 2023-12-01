public class User {
    private String username;
    private int id;
    private boolean isAdmin;
    private String role;

    public User(String username, int id, boolean isAdmin) {
        this.username = username;
        this.id = id;
        this.isAdmin = isAdmin;
        this.role = isAdmin() ? "admin" : "user";
    }

    public String getUsername() {
        return username;
    }

    public int getId() {
        return id;
    }

    public String getRole() {
        return role;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
}