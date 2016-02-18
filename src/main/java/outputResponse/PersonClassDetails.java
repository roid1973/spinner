package outputResponse;

public class PersonClassDetails implements Comparable<PersonClassDetails> {

	private int personId;
	private String firstName;
	private String lastName;
	private String phoneNumber;
	private String address;
	private String email;
	private String birthDate;
	private int numberOfValidRegistrations;
	private int credit;
	private int numberOfRegistrations;
	private int numberOfStandBy;

	public PersonClassDetails(int personId, String firstName, String lastName, String phoneNumber, String address, String email, String birthDate, int credit, int numberOfRegistrations, int numberOfStandBy) {
		this(personId, firstName, lastName, phoneNumber, address, email, birthDate, credit);
		this.numberOfRegistrations = numberOfRegistrations;
		this.numberOfStandBy = numberOfStandBy;
	}

	public PersonClassDetails(int personId, String firstName, String lastName, String phoneNumber, String address, String email, String birthDate, int credit) {
		this.personId = personId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.phoneNumber = phoneNumber;
		this.address = address;
		this.email = email;
		this.birthDate = birthDate;
		this.numberOfValidRegistrations = credit;
		this.credit = credit;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public int getNumberOfValidRegistrations() {
		return numberOfValidRegistrations;
	}

	public int getCredit() {
		return credit;
	}

	public int getNumberOfRegistrations() {
		return numberOfRegistrations;
	}

	public int getNumberOfStandBy() {
		return numberOfStandBy;
	}

	public int getPersonId() {
		return personId;
	}

	public String getEmail() {
		return email;
	}

	public String getBirthDate() {
		return birthDate;
	}

	public String getAddress() {
		return address;
	}

	public int compareTo(PersonClassDetails p) {
		int comp = 0;

		if (p.getLastName() == null) {
			return -1;
		}

		if (lastName == null) {
			return 1;
		}

		int compLastName = p.getLastName().compareToIgnoreCase(lastName);
		if (compLastName == 0) {
			int compFirstName = p.getFirstName().compareTo(firstName);
			if (compFirstName == 0) {
				comp = 0;
			} else if (compFirstName < 0) {
				comp = 1;
			} else if (compFirstName > 0) {
				comp = -1;
			}
		} else if (compLastName < 0) {
			comp = 1;
		} else if (compLastName > 0) {
			comp = -1;
		}

		return comp;
	}

}
