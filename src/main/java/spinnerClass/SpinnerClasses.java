package spinnerClass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import spinnerCalendar.SpinnerEvent;
import spinnerCalendar.Status;
import spinnerCalendar.StudentSpinnerEvent;
import charcters.CharcterType;
import charcters.Person;
import charcters.PersonInputRequest;
import charcters.PersonSpinnerClass;
import charcters.Persons;
import charcters.Student;
import db.spinner.DBspinner;

public class SpinnerClasses {
	private HashMap<Integer, SpinnerClass> spinnerClasses = null;
	private static SpinnerClasses SpinnerClassesInstance = null;

	private SpinnerClasses() throws Exception {
		// spinnerClasses = DBspinner.getSpinnerClassListFromDB();
	}

	public static SpinnerClasses getSpinnerClassesInstance() throws Exception {
		synchronized (SpinnerClasses.class) {
			if (SpinnerClassesInstance == null) {
				SpinnerClassesInstance = new SpinnerClasses();
				SpinnerClassesInstance.InitSpinnerClasses();
			}
		}
		return SpinnerClassesInstance;
	}

	private void InitSpinnerClasses() throws Exception {
		spinnerClasses = DBspinner.getSpinnerClassListFromDB();
	}

	public SpinnerClass addSpinnerClass(String className, String hyperLink) throws Exception {
		SpinnerClass sc = new SpinnerClass(className, hyperLink);
		int id = DBspinner.addClass(className);
		sc.setId(id);
		spinnerClasses.put(sc.getId(), sc);
		return sc;
	}

	public SpinnerClass getSpinnerClass(int classId) {
		SpinnerClass sc = spinnerClasses.get(classId);
		return sc;
	}

	public Status deleteSpinnerClass(int classId) throws Exception {
		Status sts = new Status(Status.CLASS_NOT_EXIST);
		SpinnerClass spinnerClass = getSpinnerClass(classId);
		if (spinnerClass != null) {
			spinnerClass.unAssignPersonsFromClass();
			spinnerClass.deleteClassEvents();
			DBspinner.deleteSpinnerClass(classId);
			spinnerClasses.remove(classId);
			sts.setStatus(Status.CLASS_DELETED);
		}
		return sts;
	}

	public SpinnerEvent addSpinnerEventToSpinnerCalendar(int classId, SpinnerEvent se) throws Exception {
		SpinnerClass sc = getSpinnerClass(classId);
		return sc.addSpinnerEventToClassCalendar(se);
	}

	public SpinnerEvent updateSpinnerEventInSpinnerCalendar(int classId, int eventId, SpinnerEvent newEvent) throws Exception {
		SpinnerClass sc = getSpinnerClass(classId);
		return sc.updateSpinnerEventToClassCalendar(eventId, newEvent);
	}

	public void deleteSpinnerEventFromSpinnerCalendar(int classId, int eventId) throws Exception {
		SpinnerClass sc = getSpinnerClass(classId);
		sc.deleteSpinnerEventFromClassCalendar(eventId);
	}

	public PersonSpinnerClass assignStudentToClass(int personId, int classId, int numberOfValidRegistrations) throws Exception {
		SpinnerClass sc = getSpinnerClass(classId);
		Person p = Persons.getPersonsInstance().getPerson(personId);
		PersonSpinnerClass psc = sc.assignStudentToClass(p, numberOfValidRegistrations);
		return psc;
	}

	public PersonSpinnerClass addCredit(int studentId, int classId, int credit) throws Exception {
		SpinnerClass sc = getSpinnerClass(classId);
		PersonSpinnerClass s = sc.getStudent(studentId);
		s.addCredit(credit);
		return s;
	}
	
	public PersonSpinnerClass updateCredit(int studentId, int classId, int credit) throws Exception{
		SpinnerClass sc = getSpinnerClass(classId);
		PersonSpinnerClass s = sc.getStudent(studentId);
		s.updateCredit(credit);
		return s;
	}

	public void assignInstructorToClass(int personId, int classId) throws Exception {
		SpinnerClass sc = getSpinnerClass(classId);
		Person p = Persons.getPersonsInstance().getPerson(personId);
		sc.assignInstructorToClass(p);
	}
	
	public void assignAdminToClass(int personId, int classId) throws Exception {
		SpinnerClass sc = getSpinnerClass(classId);
		Person p = Persons.getPersonsInstance().getPerson(personId);
		sc.assignAdminToClass(p);
	}

	public void unAssignStudentFromClass(int personId, int classId) throws Exception {
		SpinnerClass sc = getSpinnerClass(classId);
		Person p = Persons.getPersonsInstance().getPerson(personId);
		sc.unAssignStudentFromClass(p);
	}

	public void deletePerson(int personId, int classId) throws Exception {
		SpinnerClass sc = getSpinnerClass(classId);
		sc.deletePerson(personId);
	}

	public void unAssignInstructorFromClass(int personId, int classId) throws Exception {
		SpinnerClass sc = getSpinnerClass(classId);
		Person p = Persons.getPersonsInstance().getPerson(personId);
		sc.unAssignInstructorFromClass(p);
	}
	
	public void unAssignAdminFromClass(int personId, int classId) throws Exception {
		SpinnerClass sc = getSpinnerClass(classId);
		Person p = Persons.getPersonsInstance().getPerson(personId);
		sc.unAssignAdminFromClass(p);
	}

	public void deletePerson(int personId, HashMap<Integer, SpinnerClass> personClassList) throws Exception {
		Iterator it = personClassList.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			int classId = (Integer) pair.getKey();
			SpinnerClass sc = (SpinnerClass) pair.getValue();
			deletePerson(personId, classId);
		}
	}

	public StudentSpinnerEvent registerToSpinnerEvent(int classId, int eventId, int studentId) throws Exception {
		SpinnerClass sc = getSpinnerClass(classId);
		return sc.registerToSpinnerEvent(eventId, studentId);
	}

	public StudentSpinnerEvent unRegisterFromSpinnerEvent(int classId, int eventId, int studentId) throws Exception {
		SpinnerClass sc = getSpinnerClass(classId);
		return sc.unRegisterFromSpinnerEvent(eventId, studentId);
	}

	public void getStudentRegistrationEvents(int classId, int studentId, List<Integer> registered, List<Integer> standBy) throws Exception {
		SpinnerClass spinnerClass = getSpinnerClass(classId);
		spinnerClass.getStudent(studentId).initPersonRegisteration(registered, standBy);
	}

	public ArrayList<StudentSpinnerEvent> getStudentEvents(int classId, int studentId) throws Exception {
		SpinnerClass spinnerClass = getSpinnerClass(classId);
		PersonSpinnerClass s = spinnerClass.getStudent(studentId);
		HashMap<Integer, SpinnerEvent> classEventsList = spinnerClass.getClassEvents().getSpinnerCalendarEventsHashMap();
		List<Integer> registered = new ArrayList<Integer>();
		List<Integer> standBy = new ArrayList<Integer>();
		spinnerClass.getStudent(studentId).initPersonRegisteration(registered, standBy);
		return initStudentEventList(classEventsList, registered, standBy, s.getNumberOfValidRegistrations());
	}

	public StudentSpinnerEvent getStudentEvent(int classId, int studentId, int eventId) throws Exception {
		SpinnerClass sc = getSpinnerClass(classId);
		PersonSpinnerClass s = sc.getStudent(studentId);
		SpinnerEvent se = sc.getClassEvents().getSpinnerEvent(eventId);
		List<Integer> registered = new ArrayList<Integer>();
		List<Integer> standBy = new ArrayList<Integer>();
		sc.getStudent(studentId).initPersonRegisteration(registered, standBy);
		return getStudentSpinnerEvent(se, registered, standBy, s.getNumberOfValidRegistrations());
	}

	private StudentSpinnerEvent getStudentSpinnerEvent(SpinnerEvent se, List<Integer> registered, List<Integer> standby, int numberOfValidRegistrations) {
		int eventId = se.getEventId();
		String sts = Status.NOT_REGISTERED;
		if (registered.contains(eventId)) {
			sts = Status.REGISTERED;
		} else if (standby.contains(eventId)) {
			sts = Status.STANDBY;
		}
		StudentSpinnerEvent studentSpinnerEvent = new StudentSpinnerEvent(se, sts, numberOfValidRegistrations);
		return studentSpinnerEvent;
	}

	private ArrayList<StudentSpinnerEvent> initStudentEventList(HashMap<Integer, SpinnerEvent> classEventsList, List<Integer> registered, List<Integer> standby, int numberOfValidRegistrations) {
		ArrayList<StudentSpinnerEvent> studentSpinnerEventsList = new ArrayList<StudentSpinnerEvent>();
		Iterator it = classEventsList.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			int eventId = (Integer) pair.getKey();
			SpinnerEvent se = (SpinnerEvent) pair.getValue();
			StudentSpinnerEvent studentSpinnerEvent = getStudentSpinnerEvent(se, registered, standby, numberOfValidRegistrations);
			studentSpinnerEventsList.add(studentSpinnerEvent);
		}
		return studentSpinnerEventsList;
	}

	public HashMap<Integer, SpinnerClass> getClassesPersonAssignedTo(int personId) {
		HashMap<Integer, SpinnerClass> personClassesList = new HashMap<Integer, SpinnerClass>();
		Iterator it = spinnerClasses.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			int classId = (Integer) pair.getKey();
			SpinnerClass sc = (SpinnerClass) pair.getValue();
			if (sc.getStudent(personId) != null) {
				personClassesList.put(classId, sc);
			} else if (sc.getInstructor(personId) != null) {
				personClassesList.put(classId, sc);
			}
		}
		return personClassesList;
	}

	public List<Integer> getClassesIdsPersonAssignedTo(int personId) {
		List<Integer> personClassesIdsList = new ArrayList<Integer>();
		Iterator it = spinnerClasses.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			int classId = (Integer) pair.getKey();
			SpinnerClass sc = (SpinnerClass) pair.getValue();
			if (sc.getStudent(personId) != null) {
				personClassesIdsList.add(classId);
			} else if (sc.getInstructor(personId) != null) {
				personClassesIdsList.add(classId);
			}
		}
		return personClassesIdsList;
	}

	public List<Person> getEventRegisteredStudents(int classId, int eventId) throws Exception {
		SpinnerClass sc = getSpinnerClass(classId);
		SpinnerEvent se = sc.getClassEvent(eventId);
		List<Integer> ids = se.getRegistered();
		return getPersons(ids);
	}

	public List<Person> getEventStandbyStudents(int classId, int eventId) throws Exception {
		SpinnerClass sc = getSpinnerClass(classId);
		SpinnerEvent se = sc.getClassEvent(eventId);
		List<Integer> ids = se.getStandby();
		return getPersons(ids);
	}

	private List<Person> getPersons(List<Integer> ids) throws Exception {
		ArrayList<Person> persons = new ArrayList<Person>();
		Iterator<Integer> iterator = ids.iterator();
		while (iterator.hasNext()) {
			int personId = iterator.next();
			Person p = Persons.getPersonsInstance().getPerson(personId);
			persons.add(p);
		}
		return persons;
	}





}
