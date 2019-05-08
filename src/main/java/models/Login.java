package models;

public class Login {
	public String id;
	public String email;
	public String password;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public Login(String id, String email, String password) {
		this.id = id;
		this.email = email;
		this.password = password;
	}

	public Login() {

	}
}
