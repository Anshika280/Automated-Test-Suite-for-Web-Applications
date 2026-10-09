package framework.models;

public class UserRequestModel {

    private String name;
    private String job;
    private String email;
    private String password;

    public UserRequestModel() {
    }

    public UserRequestModel(String name, String job) {
        this.name = name;
        this.job = job;
    }

    public UserRequestModel(String email, String password, boolean authOnly) {
        this.email = email;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
