package spinnerClass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import spinnerCalendar.SpinnerCalendar;
import spinnerCalendar.SpinnerEvent;
import spinnerCalendar.StudentSpinnerEvent;
import charcters.CharcterType;
import charcters.Person;
import charcters.PersonSpinnerClass;
import charcters.Persons;
import db.spinner.DBspinner;

public class SpinnerClass {

	private String spinnerClassName = null;
	private int classId;
	private String hyperLink = null;
	private SpinnerCalendar classCalendar = null;
	private HashMap<Integer, PersonSpinnerClass> students = null;
	private HashMap<Integer, PersonSpinnerClass> instructors = null;
	private HashMap<Integer, PersonSpinnerClass> admins = null;
	private String openForRegistrationMode;
	private int lockForRegistration;

	public SpinnerClass(String className, String openForRegistrationMode, int lockForRegistration, String hyperLink) {
		spinnerClassName = className;
		this.openForRegistrationMode=openForRegistrationMode;
		this.lockForRegistration=lockForRegistration;
		this.hyperLink = hyperLink;
	}

	public SpinnerClass(int classId) throws Exception {
		this.classId = classId;
		// classCalendar = new SpinnerCalendar(classId);
		initStudetsList();
		initInstructorsList();
		initAdminsList();
	}

	public void setId(int id) throws Exception {
		classId = id;
		// initClassCalendar();
		initStudetsList();
		initInstructorsList();
		initAdminsList();
	}

	private void initInstructorsList() throws Exception {
		instructors = DBspinner.getClassPersonsListFromDB(classId, CharcterType.INSTRUCTOR);
	}

	private void initStudetsList() throws Exception {
		students = DBspinner.getClassPersonsListFromDB(classId, CharcterType.STUDENT);
	}

	private void initAdminsList() throws Exception {
		admins = DBspinner.getClassPersonsListFromDB(classId, CharcterType.ADMIN);
	}

	private void initClassCalendar() throws Exception {
		if (classCalendar == null) {
			classCalendar = new SpinnerCalendar(classId);
		}
	}

	public String getSpinnerClassName() {
		return spinnerClassName;
	}

	public int getId() {
		return classId;
	}
	
	public String getOpenForRegistrationMode() {
		return openForRegistrationMode;
	}

	public int getLockForRegistration() {
		return lockForRegistration;
	}


	public String getHyperLink() {
		return hyperLink;
	}

	public PersonSpinnerClass getStudent(int personId) {
		return students.get(personId);
	}

	public PersonSpinnerClass getInstructor(int personId) {
		return instructors.get(personId);
	}

	protected PersonSpinnerClass assignStudentToClass(Person p, int numberOfValidRegistrations) throws Exception {
		PersonSpinnerClass psc = students.get(p.getId());
		if (students.get(p.getId()) == null) {
			psc = new PersonSpinnerClass(classId, p.getId(), CharcterType.STUDENT, numberOfValidRegistrations);
			p.assignPersonToClass(this);
			DBspinner.assignPersonToClass(this, psc);
			students.put(psc.getPerson().getId(), psc);
		}
		return psc;
	}

	protected void unAssignStudentFromClass(Person p) throws Exception {
		PersonSpinnerClass psc = students.get(p.getId());
		if (psc != null) {
			psc.deleteStudentRegisterationFromClassEvents();
			p.unAssignPersonFromClass(classId);
			DBspinner.unAssignPersonFromClass(this, psc);
			students.remove(psc.getPerson().getId());
		}
	}

	protected void deletePerson(int personId) throws Exception {
		deleteStudent(personId);
		deleteInstructor(personId);
	}

	private void deleteStudent(int personId) throws Exception {
		PersonSpinnerClass psc = students.get(personId);
		if (psc != null) {
			psc.deleteStudentRegisterationFromClassEvents();
			DBspinner.unAssignPersonFromClass(this, psc);
			students.remove(personId);
		}
	}

	private void deleteInstructor(int personId) throws Exception {
		PersonSpinnerClass psc = instructors.get(personId);
		if (psc != null) {
			deleteInstructorsFromClassEvents(personId);
			DBspinner.unAssignPersonFromClass(this, psc);
			instructors.remove(personId);
		}
	}

	private void deleteInstructorsFromClassEvents(int personId) throws Exception {
		Iterator it = classCalendar.getSpinnerCalendarEventsHashMap().entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			SpinnerEvent se = (SpinnerEvent) pair.getValue();
			if (se.getInstructorId() == personId) {
				se.updateInstructor(-1);
			}
		}

	}

	protected void assignInstructorToClass(Person p) throws Exception {
		PersonSpinnerClass psc = instructors.get(p.getId());
		if (instructors.get(p.getId()) == null) {
			psc = new PersonSpinnerClass(classId, p.getId(), CharcterType.INSTRUCTOR, -999);
			p.assignPersonToClass(this);
			DBspinner.assignPersonToClass(this, psc);
			instructors.put(psc.getPerson().getId(), psc);
		}
	}

	protected void unAssignInstructorFromClass(Person p) throws Exception {
		PersonSpinnerClass psc = instructors.get(p.getId());
		if (psc != null) {
			// psc.deleteStudentRegisterationFromClassEvents(); TODO: need to
			// delete instructor from all events
			p.unAssignPersonFromClass(classId);
			DBspinner.unAssignPersonFromClass(this, psc);
			instructors.remove(psc.getPerson().getId());
		}
	}

	protected void assignAdminToClass(Person p) throws Exception {
		PersonSpinnerClass psc = admins.get(p.getId());
		if (admins.get(p.getId()) == null) {
			psc = new PersonSpinnerClass(classId, p.getId(), CharcterType.ADMIN, -999);
			p.assignPersonToClass(this);
			DBspinner.assignPersonToClass(this, psc);
			admins.put(psc.getPerson().getId(), psc);
		}
	}
	
	protected void unAssignAdminFromClass(Person p) throws Exception {
		PersonSpinnerClass psc = admins.get(p.getId());
		if (psc != null) {			
			p.unAssignPersonFromClass(classId);
			DBspinner.unAssignPersonFromClass(this, psc);
			admins.remove(psc.getPerson().getId());
		}
	}

	protected SpinnerEvent addSpinnerEventToClassCalendar(SpinnerEvent se) throws Exception {
		initClassCalendar();
		return classCalendar.addSpinnerEventToSpinnerCalendar(se);
	}

	protected SpinnerEvent updateSpinnerEventToClassCalendar(int eventId, SpinnerEvent newEvent) throws Exception {
		initClassCalendar();
		return classCalendar.updateSpinnerEventDetails(eventId, newEvent);
	}

	protected SpinnerEvent deleteSpinnerEventFromClassCalendar(int eventId) throws Exception {
		initClassCalendar();		
		return classCalendar.deleteSpinnerEventFromSpinnerCalendar(eventId);
	}

	protected StudentSpinnerEvent registerToSpinnerEvent(int eventId, int studentId) throws Exception {
		SpinnerEvent se = getClassEvent(eventId);
		PersonSpinnerClass s = getStudent(studentId);
		String sts = se.registerToSpinnerEvent(s);
		StudentSpinnerEvent studentEvent = new StudentSpinnerEvent(se, sts, s.getNumberOfValidRegistrations());
		// TODO: do we need to return SpinnerEvent?
		return studentEvent;
	}

	protected StudentSpinnerEvent unRegisterFromSpinnerEvent(int eventId, int studentId) throws Exception {
		SpinnerEvent se = getClassEvent(eventId);
		PersonSpinnerClass s = getStudent(studentId);
		String sts = se.unRegisterFromSpinnerEvent(s);
		StudentSpinnerEvent studentEvent = new StudentSpinnerEvent(se, sts, s.getNumberOfValidRegistrations());
		// TODO: do we need to return SpinnerEvent?
		return studentEvent;
	}

	public SpinnerEvent getClassEvent(int eventId) throws Exception {
		initClassCalendar();
		SpinnerEvent se = classCalendar.getSpinnerEvent(eventId);
		return se;
	}

	public SpinnerCalendar getClassEvents() throws Exception {
		initClassCalendar();
		return classCalendar;
	}

	public void deleteClassEvents() throws Exception {
		DBspinner.deleteClassEvents(classId);
	}

	public void unAssignPersonsFromClass() throws Exception {
		unAssignPersonsFromClass(students);
		unAssignPersonsFromClass(instructors);
	}

	private void unAssignPersonsFromClass(HashMap<Integer, PersonSpinnerClass> persons) throws Exception {
		Iterator it = persons.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			int personId = (Integer) pair.getKey();
			Person p = Persons.getPersonsInstance().getPerson(personId);
			p.unAssignPersonFromClass(classId);
		}
		DBspinner.unAssignPersonsFromClass(classId);
	}

	public List<PersonSpinnerClass> studentsList() {
		List<PersonSpinnerClass> pscList = new ArrayList<PersonSpinnerClass>();
		Iterator it = students.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			PersonSpinnerClass psc = (PersonSpinnerClass) pair.getValue();
			pscList.add(psc);
		}
		return pscList;
	}

	public List<Person> instructorList() throws Exception {
		List<Person> pList = new ArrayList<Person>();
		Iterator it = instructors.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			PersonSpinnerClass psc = (PersonSpinnerClass) pair.getValue();
			pList.add(psc.getPerson());
		}
		return pList;
	}

	public boolean isAdmin(int personId) {
		boolean admin = false;
		if (admins.containsKey(personId)) {
			admin = true;
		}
		return admin;

	}

	public boolean isInstructor(int personId) {
		boolean instructor = false;
		if (instructors.containsKey(personId)) {
			instructor = true;
		}
		return instructor;

	}

	public boolean isStudent(int personId) {
		boolean student = false;
		if (students.containsKey(personId)) {
			student = true;
		}
		return student;

	}


	



}
