package charcters;

import java.util.Date;
import java.util.HashMap;

import spinnerCalendar.SpinnerEvent;
import spinnerClass.SpinnerClass;
import spinnerClass.SpinnerClasses;
import db.spinner.DBspinner;

public class Persons {

	// private HashMap<Integer, Person> studentsList = null;
	// private HashMap<Integer, Person> instructorsList = null;
	private HashMap<Integer, Person> personList = null;
	private static Persons personsInstance = null;

	protected Persons() {
		personList = new HashMap<Integer, Person>();
	}

	public static Persons getPersonsInstance() throws Exception {
		synchronized (Persons.class) {
			if (personsInstance == null) {
				personsInstance = new Persons();
				personsInstance.initPersonList();
			}
		}
		return personsInstance;
	}

	private void initPersonList() throws Exception {
		personList = DBspinner.getPersonHashMapFromDB();
	}

	public Person addPerson(Person p) throws Exception {
		int personId = findPerson(p);
		if (personId > 0) {
			System.out.println("Person already exist, personId: " + personId);
			p = getPerson(personId);
		} else {
			p = DBspinner.insertPersonInToDB(p);
			personList.put(p.getId(), p);
		}
		return p;
	}

	public Person addSPerson(String pn, String fn, String ln, String add, String e, Date bd) throws Exception {
		Person p = new Person(pn, fn, ln, add, e, bd);
		p = addPerson(p);
		return p;
	}

	private int findPerson(Person p) throws Exception {
		int personId = DBspinner.findPerson(p.getPhoneNumber(), p.getFirstName());
		return personId;
	}

	// public void addPerson(String pn, String fn, String ln, String add, String
	// e, Date bd) throws Exception {
	// Person p = new Person(pn, fn, ln, add, e, bd);
	// addPerson(p);
	// }

	public void deletePerson(int personId) throws Exception {
		Person p = getPerson(personId);
		SpinnerClasses.getSpinnerClassesInstance().deletePerson(personId, p.getCalssList());
		DBspinner.deletePersonFromDB(personId);
		personList.remove(personId);
	}

	public Person getPerson(int personId) throws Exception {
		Person p = personList.get(personId);
		if (p == null) {
			throw new Exception("getStudent Failed: " + personId + " not found in students List");
		}
		return p;
	}

	public HashMap<Integer, SpinnerClass> getPersonSpinnerClasses(int personId) throws Exception {
		Person p = getPerson(personId);
		return p.getCalssList();
	}

	public Person updatePersonDetails(int personId, String newPN, String newFN, String newLN, String newAdd, String newEmail, Date newBD) throws Exception {
		Person p = getPerson(personId);
		if (p == null) {
			System.out.println("updatePersonDetails: " + personId + " do not exist");
		} else {
			p.updatePersonDetails(newPN, newFN, newLN, newAdd, newEmail, newBD);
		}
		return p;
	}

}
