package engine.key_management.entities;

public class User {
    private String username;
    private String email;

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPassphrase() {
        return passphrase;
    }

    private String passphrase;

    public User(){}
    public User(String username, String email, String passphrase) {
        this.username = username;
        this.email = email;
        this.passphrase = passphrase;
    }

    public String getId() {
        return this.username+"<"+this.email+">";
    }
}
