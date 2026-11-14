public class Staff {

    private String fullName;
    private String username;
    private String password;

    public Staff(String fullName, String username, String password) {
        if (fullName == null || fullName.trim().isEmpty()) {
            throw new IllegalArgumentException("Full name cannot be empty.");
        }
        if (!isValidUsername(username)) {
            throw new IllegalArgumentException("Invalid username.");
        }
        if (!isValidPassword(password)) {
            throw new IllegalArgumentException("Invalid password.");
        }
        this.fullName = fullName.trim();
        this.username = username;
        this.password = password;
    }

    public static boolean isValidUsername(String username) {
        return username != null && username.length() >= 4 && !username.contains(" ");
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 6;
    }

    public String getFullName() {
        return fullName;
    }

    public String getUsername() {
        return username;
    }

    public boolean checkPassword(String typedPassword) {
        return password.equals(typedPassword);
    }
}
