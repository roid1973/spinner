package charcters;

import java.util.Date;

import utils.DateUtils;

public class PersonInputRequest {

	private static String prefix = "(972)";

	private String phoneNumber;
	private String firstName;
	private String email;
	private String lastName;
	private Date birthDate;
	private String address;
	private int credit;

	public String getPhoneNumber() {		
		phoneNumber = phoneNumber.substring(phoneNumber.length() - 10);		
		phoneNumber = prefix + phoneNumber;		
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public Date getBirthDate() {
		return birthDate;
	}

	public void setBirthDate(String birthDate) {
		this.birthDate = DateUtils.stringToStudentBirthDate(birthDate);
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getCredit() {
		return credit;
	}

	public void setCredit(int credit) {
		this.credit = credit;
	}

}
