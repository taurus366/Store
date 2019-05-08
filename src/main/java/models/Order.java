package models;

public class Order {

	String firstname;
	String lastname;
	String phone;
	String country;
	String postalcode;
	String city;
	String address;

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getPostalcode() {
		return postalcode;
	}

	public void setPostalcode(String postalcode) {
		this.postalcode = postalcode;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public Order(String firstname, String lastname, String phone, String country, String postalcode, String city,
			String address) {

		this.firstname = firstname;
		this.lastname = lastname;
		this.phone = phone;
		this.country = country;
		this.postalcode = postalcode;
		this.city = city;
		this.address = address;
	}

	public Order() {

	}

}
