package charcters;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import spinnerClass.SpinnerClass;
import spinnerClass.SpinnerClasses;
import utils.DateUtils;
import db.spinner.DBspinner;
import db.spinner.DButils;

public class Person {
	private int personId;
	private String phoneNumber;
	private String firstName;
	private String lastName;
	private String address;
	private String email;
	private Date birthDay;
	private HashMap<Integer, SpinnerClass> personClasses;

	// private List<Integer> personClassesIds;

	protected Person(String pn, String fn) throws Exception {
		initNewPerson(pn, fn, null, null, null, null);
	}

	public Person(String pn, String fn, String ln, String add, String e, String bds) throws Exception {
		Date bd = DateUtils.stringToStudentBirthDate(bds);
		initNewPerson(pn, fn, ln, add, e, bd);
	}

	public Person(String pn, String fn, String ln, String add, String e, Date bd) throws Exception {
		initNewPerson(pn, fn, ln, add, e, bd);
	}

	protected Person(int id, String pn, String fn) throws Exception {
		initPerson(id, pn, fn, null, null, null, null);
	}

	public Person(int id, String pn, String fn, String ln, String add, String e, Date bd) throws Exception {
		initPerson(id, pn, fn, ln, add, e, bd);
	}

	public Person() {
		// TODO Auto-generated constructor stub
	}

	private void initNewPerson(String pn, String fn, String ln, String add, String e, Date bd) throws Exception {
		phoneNumber = pn;
		firstName = fn;
		lastName = ln;
		address = add;
		email = e;
		birthDay = bd;
		personClasses = new HashMap<Integer, SpinnerClass>();
		// personClassesIds = new ArrayList<Integer>();
	}

	private void initPerson(int id, String pn, String fn, String ln, String add, String e, Date bd) throws Exception {
		personId = id;
		phoneNumber = pn;
		firstName = fn;
		lastName = ln;
		address = add;
		email = e;
		birthDay = bd;
		personClasses = SpinnerClasses.getSpinnerClassesInstance().getClassesPersonAssignedTo(personId);
		// personClassesIds = SpinnerClasses.getSpinnerClassesInstance().getClassesIdsPersonAssignedTo(personId);
	}

	public void updatePersonDetails(String newPN, String newFN, String newLN, String newAdd, String newEmail, Date newBD) throws Exception {
		phoneNumber = newPN;
		firstName = newFN;
		lastName = newLN;
		address = newAdd;
		email = newEmail;
		birthDay = newBD;
		DBspinner.updatePersonDetails(this);
	}

	public void assignPersonToClass(SpinnerClass sc) throws Exception {
		personClasses.put(sc.getId(), sc);
		// personClassesIds.add(sc.getId());
	}

	public void unAssignPersonFromClass(int classId) throws Exception {
		personClasses.remove(classId);
		// personClassesIds.remove((Integer)classId);
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Date getBirthDay() {
		return birthDay;
	}

	public String getBirthDayString() {
		return DateUtils.dateToString(birthDay);
	}

	public void setBirthDay(Date birthDay) {
		this.birthDay = birthDay;
	}

	public String getName() {
		return firstName;
	}

	public void setName(String name) {
		this.firstName = name;
	}

	public int getId() {
		return personId;
	}

	public void setId(int id) {
		this.personId = id;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public HashMap<Integer, SpinnerClass> getCalssList() {
		return personClasses;
	}

}
