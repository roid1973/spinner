package spinnerCalendar;

import inputRequest.PersonDetailsClassInput;
import inputRequest.SpinnerEventInputRequest;

import java.util.*;
import java.util.Map.Entry;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import outputResponse.PersonClassDetails;
import spinnerClass.*;
import utils.DateUtils;
import charcters.*;

@Path("SpinnerServices")
public class SpinnerCalendarServices {

	// Person services
	@POST
	@Path("/addPerson")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Person addPerson(Map<String, PersonInputRequest> input) throws Exception {
		PersonInputRequest inputStudent = (input.entrySet().iterator().next()).getValue();
		Person p = Persons.getPersonsInstance().addSPerson(inputStudent.getPhoneNumber(), inputStudent.getFirstName(), inputStudent.getLastName(), inputStudent.getAddress(), inputStudent.getEmail(), inputStudent.getBirthDate());
		return p;
	}

	@POST
	@Path("/addPersons")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<Person> addStudents(List<PersonInputRequest> input) throws Exception {
		ArrayList<Person> persons = new ArrayList<Person>();
		Iterator<PersonInputRequest> iterator = input.iterator();
		while (iterator.hasNext()) {
			PersonInputRequest inputPerson = iterator.next();
			Person p = Persons.getPersonsInstance().addSPerson(inputPerson.getPhoneNumber(), inputPerson.getFirstName(), inputPerson.getLastName(), inputPerson.getAddress(), inputPerson.getEmail(), inputPerson.getBirthDate());
			persons.add(p);
		}
		return persons;
	}

	@POST
	@Path("/updatePersonDetails/{personId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Person updatePersonDetails(PersonInputRequest inputPerson, @PathParam("personId") int personId) throws Exception {
		Person p = Persons.getPersonsInstance().updatePersonDetails(personId, inputPerson.getPhoneNumber(), inputPerson.getFirstName(), inputPerson.getLastName(), inputPerson.getAddress(), inputPerson.getEmail(), inputPerson.getBirthDate());
		return p;
	}

	@POST
	@Path("/updatePersonDetails/{classId}/{personId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PersonClassDetails updatePersonDetails(PersonInputRequest inputPerson, @PathParam("classId") int classId, @PathParam("personId") int personId) throws Exception {
		PersonSpinnerClass psc = null;
		PersonClassDetails pcd = null;
		Person p = Persons.getPersonsInstance().updatePersonDetails(personId, inputPerson.getPhoneNumber(), inputPerson.getFirstName(), inputPerson.getLastName(), inputPerson.getAddress(), inputPerson.getEmail(), inputPerson.getBirthDate());
		SpinnerClasses classes = SpinnerClasses.getSpinnerClassesInstance();
		if (p != null) {
			psc = classes.updateCredit(personId, classId, inputPerson.getCredit());
			pcd = new PersonClassDetails(p.getId(), p.getFirstName(), p.getLastName(), p.getPhoneNumber(), p.getAddress(), p.getEmail(), DateUtils.dateToString(p.getBirthDay()), psc.getNumberOfValidRegistrations());
		}
		return pcd;
	}

	@POST
	@Path("/addStudentsToClass/{classId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<PersonSpinnerClass> addStudentsToClass(List<PersonInputRequest> input, @PathParam("classId") int classId) throws Exception {
		System.out.println("addStudentsToClass");
		ArrayList<PersonSpinnerClass> pscList = new ArrayList<PersonSpinnerClass>();
		try {
			Iterator<PersonInputRequest> iterator = input.iterator();
			while (iterator.hasNext()) {
				PersonInputRequest inputPerson = iterator.next();
				// Person p = Persons.getPersonsInstance().addSPerson(inputPerson.getPhoneNumber(), inputPerson.getFirstName(), inputPerson.getLastName(), inputPerson.getAddress(), inputPerson.getEmail(), inputPerson.getBirthDate());
				// PersonSpinnerClass psc = assignStudentToClass(classId, p.getId(), inputPerson.getCredit());
				PersonSpinnerClass psc = addStudentToClass(inputPerson, classId);
				pscList.add(psc);
			}
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return pscList;
	}

	@POST
	@Path("/addStudentToClass/{classId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public PersonSpinnerClass addStudentToClass(PersonInputRequest inputPerson, @PathParam("classId") int classId) throws Exception {
		Person p = Persons.getPersonsInstance().addSPerson(inputPerson.getPhoneNumber(), inputPerson.getFirstName(), inputPerson.getLastName(), inputPerson.getAddress(), inputPerson.getEmail(), inputPerson.getBirthDate());
		PersonSpinnerClass psc = assignStudentToClass(classId, p.getId(), inputPerson.getCredit());
		return psc;
	}

	@GET
	@Path("/getStudentsClass/{classId}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<PersonSpinnerClass> getStudentsClass(@PathParam("classId") int classId) throws Exception {
		SpinnerClasses classes = SpinnerClasses.getSpinnerClassesInstance();
		SpinnerClass sc = classes.getSpinnerClass(classId);
		List<PersonSpinnerClass> pscList = sc.studentsList();
		return pscList;
	}

	@POST
	@Path("/getStudentsDetailsClass")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<PersonClassDetails> getStudentsDetailsClass(PersonDetailsClassInput input) throws Exception {
		List<PersonClassDetails> pcdList = new ArrayList<PersonClassDetails>();
		SpinnerClasses classes = SpinnerClasses.getSpinnerClassesInstance();
		int classId = input.getClassId();
		SpinnerClass sc = classes.getSpinnerClass(classId);
		List<PersonSpinnerClass> pscList = sc.studentsList();
		Iterator<PersonSpinnerClass> it = pscList.iterator();
		while (it.hasNext()) {
			PersonSpinnerClass psc = it.next();
			PersonClassDetails pcd = getPersonClassDetails(input.getFromDate(), input.getToDate(), classId, psc);
			pcdList.add(pcd);
		}
		return pcdList;
	}

	@GET
	@Path("/getThisWeekStudentsDetailsClass/{classId}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<PersonClassDetails> getThisWeekStudentsDetailsClass(@PathParam("classId") int classId) throws Exception {
		List<PersonClassDetails> pcdList = new ArrayList<PersonClassDetails>();
		SpinnerClasses classes = SpinnerClasses.getSpinnerClassesInstance();
		SpinnerClass sc = classes.getSpinnerClass(classId);
		List<PersonSpinnerClass> pscList = sc.studentsList();
		Iterator<PersonSpinnerClass> it = pscList.iterator();
		Date from = DateUtils.getFirstDateOfTheCurrentWeek();
		Date to = DateUtils.getLastDateOfTheCurrentWeek();
		while (it.hasNext()) {
			PersonSpinnerClass psc = it.next();
			PersonClassDetails pcd = getPersonClassDetails(from, to, classId, psc);
			pcdList.add(pcd);
		}
		return pcdList;
	}

	@GET
	@Path("/getStudentsDetailsClass/{classId}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<PersonClassDetails> getStudentsDetailsClass(@PathParam("classId") int classId) throws Exception {
		List<PersonClassDetails> pcdList = new ArrayList<PersonClassDetails>();
		SpinnerClasses classes = SpinnerClasses.getSpinnerClassesInstance();
		SpinnerClass sc = classes.getSpinnerClass(classId);
		List<PersonSpinnerClass> pscList = sc.studentsList();
		Iterator<PersonSpinnerClass> it = pscList.iterator();
		while (it.hasNext()) {
			PersonSpinnerClass psc = it.next();
			Person p = psc.getPerson();
			PersonClassDetails pcd = new PersonClassDetails(p.getId(), p.getFirstName(), p.getLastName(), p.getPhoneNumber(), p.getAddress(), p.getEmail(), DateUtils.dateToString(p.getBirthDay()), psc.getNumberOfValidRegistrations());
			pcdList.add(pcd);
		}
		Collections.sort(pcdList);
		return pcdList;
	}

	private PersonClassDetails getPersonClassDetails(Date from, Date to, int classId, PersonSpinnerClass psc) throws Exception {
		int personId = psc.getPersonId();
		ArrayList<StudentSpinnerEvent> events = getStudentEvents(personId, classId);
		Iterator<StudentSpinnerEvent> itEvents = events.iterator();
		int registered = 0;
		int standby = 0;
		while (itEvents.hasNext()) {
			StudentSpinnerEvent se = itEvents.next();
			Date seFromDate = se.getSpinnerEvent().getFromDate();
			if (seFromDate.after(from) && seFromDate.before(to)) {
				Status s = se.getStudentRegisterationStatus();
				if (s.getStatus().equals(Status.REGISTERED)) {
					registered++;
				} else if (s.getStatus().equals(Status.STANDBY)) {
					standby++;
				}
			}
		}
		Person p = psc.getPerson();
		PersonClassDetails pcd = new PersonClassDetails(p.getId(), p.getFirstName(), p.getLastName(), p.getPhoneNumber(), p.getAddress(), p.getEmail(), DateUtils.dateToString(p.getBirthDay()), psc.getNumberOfValidRegistrations(), registered, standby);
		return pcd;
	}

	@POST
	@Path("/addCreditToStudents/{classId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<PersonSpinnerClass> addCreditToStudents(List<PersonInputRequest> input, @PathParam("classId") int classId) throws Exception {
		ArrayList<PersonSpinnerClass> pscList = new ArrayList<PersonSpinnerClass>();
		Iterator<PersonInputRequest> itPIR = input.iterator();
		while (itPIR.hasNext()) {
			PersonInputRequest inputPerson = itPIR.next();
			Person p = Persons.getPersonsInstance().addSPerson(inputPerson.getPhoneNumber(), inputPerson.getFirstName(), inputPerson.getLastName(), inputPerson.getAddress(), inputPerson.getEmail(), inputPerson.getBirthDate());
			PersonSpinnerClass psc = addCreditToStudent(classId, p.getId(), inputPerson.getCredit());
			pscList.add(psc);
		}
		return pscList;
	}

	@POST
	@Path("/addCreditToStudent/{classId}/{studentId}/{credit}")
	@Produces(MediaType.APPLICATION_JSON)
	public PersonSpinnerClass addCreditToStudent(@PathParam("classId") int classId, @PathParam("studentId") int studentId, @PathParam("credit") int credit) throws Exception {
		PersonSpinnerClass psc = null;
		SpinnerClasses classes = SpinnerClasses.getSpinnerClassesInstance();
		SpinnerClass sc = classes.getSpinnerClass(classId);
		PersonSpinnerClass t = sc.getStudent(studentId);
		t.getPersonId();
		Person s = Persons.getPersonsInstance().getPerson(studentId);
		if (sc != null && s != null) {
			psc = classes.addCredit(studentId, classId, credit);

		}
		return psc;
	}

	@POST
	@Path("/updateCreditToStudent/{classId}/{studentId}/{credit}")
	@Produces(MediaType.APPLICATION_JSON)
	public PersonSpinnerClass updateCreditToStudent(@PathParam("classId") int classId, @PathParam("studentId") int studentId, @PathParam("credit") int credit) throws Exception {
		PersonSpinnerClass psc = null;
		SpinnerClasses classes = SpinnerClasses.getSpinnerClassesInstance();
		SpinnerClass sc = classes.getSpinnerClass(classId);
		PersonSpinnerClass t = sc.getStudent(studentId);
		t.getPersonId();
		Person s = Persons.getPersonsInstance().getPerson(studentId);
		if (sc != null && s != null) {
			psc = classes.updateCredit(studentId, classId, credit);

		}
		return psc;
	}

	@POST
	@Path("/addInstructorsToClass/{classId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<Person> addInstructorsToClass(List<PersonInputRequest> input, @PathParam("classId") int classId) throws Exception {
		ArrayList<Person> persons = new ArrayList<Person>();
		Iterator<PersonInputRequest> iterator = input.iterator();
		while (iterator.hasNext()) {
			PersonInputRequest inputPerson = iterator.next();
			Person p = addInstructorToClass(inputPerson, classId);
			persons.add(p);
		}
		return persons;
	}

	@POST
	@Path("/addInstructorToClass/{classId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Person addInstructorToClass(PersonInputRequest inputPerson, @PathParam("classId") int classId) throws Exception {
		Person p = Persons.getPersonsInstance().addSPerson(inputPerson.getPhoneNumber(), inputPerson.getFirstName(), inputPerson.getLastName(), inputPerson.getAddress(), inputPerson.getEmail(), inputPerson.getBirthDate());
		assignInstructorToClass(classId, p.getId());
		return p;
	}

	@POST
	@Path("/addAdminsToClass/{classId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<Person> addAdminsToClass(List<PersonInputRequest> input, @PathParam("classId") int classId) throws Exception {
		ArrayList<Person> persons = new ArrayList<Person>();
		Iterator<PersonInputRequest> iterator = input.iterator();
		while (iterator.hasNext()) {
			PersonInputRequest inputPerson = iterator.next();
			Person p = addAdminToClass(inputPerson, classId);
			persons.add(p);
		}
		return persons;
	}

	@POST
	@Path("/addAdminToClass/{classId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Person addAdminToClass(PersonInputRequest inputPerson, @PathParam("classId") int classId) throws Exception {
		Person p = Persons.getPersonsInstance().addSPerson(inputPerson.getPhoneNumber(), inputPerson.getFirstName(), inputPerson.getLastName(), inputPerson.getAddress(), inputPerson.getEmail(), inputPerson.getBirthDate());
		assignAdminToClass(classId, p.getId());
		return p;
	}

	@POST
	@Path("/deletePerson/{personId}")
	@Produces(MediaType.APPLICATION_JSON)
	public void deletePerson(@PathParam("personId") int personId) throws Exception {
		Persons.getPersonsInstance().deletePerson(personId);
	}

	// Class services
	@POST
	@Path("/addClass")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public SpinnerClass addClass(Map<String, SpinnerClassInputRequest> input) throws Exception {
		SpinnerClassInputRequest inputClass = (input.entrySet().iterator().next()).getValue();
		SpinnerClass sc = SpinnerClasses.getSpinnerClassesInstance().addSpinnerClass(inputClass.getClassName(), inputClass.getOpenForRegistrationMode(), inputClass.getLockForRegistration(), inputClass.getHyperLink());
		return sc;
	}

	@POST
	@Path("/deleteClass/{classId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Status deleteClass(@PathParam("classId") int classId) throws Exception {
		Status sts = SpinnerClasses.getSpinnerClassesInstance().deleteSpinnerClass(classId);
		return sts;
	}

	@POST
	@Path("/assignStudentToClass/{classId}/{studentId}/{numberOfValidRegistrations}")
	@Produces(MediaType.APPLICATION_JSON)
	public PersonSpinnerClass assignStudentToClass(@PathParam("classId") int classId, @PathParam("studentId") int studentId, @PathParam("numberOfValidRegistrations") int numberOfValidRegistrations) throws Exception {
		PersonSpinnerClass psc = null;
		SpinnerClasses classes = SpinnerClasses.getSpinnerClassesInstance();
		SpinnerClass sc = classes.getSpinnerClass(classId);
		Person s = Persons.getPersonsInstance().getPerson(studentId);
		if (sc != null && s != null) {
			psc = classes.assignStudentToClass(studentId, classId, numberOfValidRegistrations);
		}
		return psc;
	}

	@POST
	@Path("/assignInstructorToClass/{classId}/{instructorId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Status assignInstructorToClass(@PathParam("classId") int classId, @PathParam("instructorId") int instructorId) throws Exception {
		SpinnerClasses classes = SpinnerClasses.getSpinnerClassesInstance();
		Status sts = new Status(Status.FAILURE);
		SpinnerClass sc = classes.getSpinnerClass(classId);
		Person s = Persons.getPersonsInstance().getPerson(instructorId);
		if (sc != null && s != null) {
			classes.assignInstructorToClass(instructorId, classId);
			sts.setStatus(Status.ASSIGNED);
		}
		return sts;
	}

	@POST
	@Path("/assignAdminToClass/{classId}/{personId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Status assignAdminToClass(@PathParam("classId") int classId, @PathParam("personId") int personId) throws Exception {
		SpinnerClasses classes = SpinnerClasses.getSpinnerClassesInstance();
		Status sts = new Status(Status.FAILURE);
		SpinnerClass sc = classes.getSpinnerClass(classId);
		Person admin = Persons.getPersonsInstance().getPerson(personId);
		if (sc != null && admin != null) {
			classes.assignAdminToClass(personId, classId);
			sts.setStatus(Status.ASSIGNED);
		}
		return sts;
	}

	@GET
	@Path("/getClassInstructors/{classId}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Person> getClassInstructors(@PathParam("classId") int classId) throws Exception {
		SpinnerClasses classes = SpinnerClasses.getSpinnerClassesInstance();
		SpinnerClass sc = classes.getSpinnerClass(classId);
		return sc.instructorList();
	}

	@POST
	@Path("/unAssignStudentFromClass/{classId}/{studentId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Status unAssignStudentFromClass(@PathParam("classId") int classId, @PathParam("studentId") int studentId) throws Exception {
		SpinnerClasses classes = SpinnerClasses.getSpinnerClassesInstance();
		Status sts = new Status(Status.FAILURE);
		SpinnerClass sc = classes.getSpinnerClass(classId);
		Person s = Persons.getPersonsInstance().getPerson(studentId);
		if (sc != null && s != null) {
			classes.unAssignStudentFromClass(studentId, classId);
			sts.setStatus(Status.UNASSIGNED);
		}
		return sts;
	}

	@POST
	@Path("/unAssignInstructorFromClass/{classId}/{personId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Status unAssignInstructorFromClass(@PathParam("classId") int classId, @PathParam("personId") int personId) throws Exception {
		SpinnerClasses classes = SpinnerClasses.getSpinnerClassesInstance();
		Status sts = new Status(Status.FAILURE);
		SpinnerClass sc = classes.getSpinnerClass(classId);
		Person s = Persons.getPersonsInstance().getPerson(personId);
		if (sc != null && s != null) {
			classes.unAssignInstructorFromClass(personId, classId);
			sts.setStatus(Status.UNASSIGNED);
		}
		return sts;
	}

	@POST
	@Path("/unAssignAdminFromClass/{classId}/{personId}")
	@Produces(MediaType.APPLICATION_JSON)
	public Status unAssignAdminFromClass(@PathParam("classId") int classId, @PathParam("personId") int personId) throws Exception {
		SpinnerClasses classes = SpinnerClasses.getSpinnerClassesInstance();
		Status sts = new Status(Status.FAILURE);
		SpinnerClass sc = classes.getSpinnerClass(classId);
		Person s = Persons.getPersonsInstance().getPerson(personId);
		if (sc != null && s != null) {
			classes.unAssignAdminFromClass(personId, classId);
			sts.setStatus(Status.UNASSIGNED);
		}
		return sts;
	}

	@GET
	@Path("/getPersonClasses/{personId}")
	@Produces(MediaType.APPLICATION_JSON)
	public HashMap<Integer, SpinnerClass> getPersonClasses(@PathParam("personId") int personId) throws Exception {
		return Persons.getPersonsInstance().getPersonSpinnerClasses(personId);
	}

	@GET
	@Path("/getPersonSpinnerClasses/{personId}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<PersonSpinnerClassResponse> getPersonSpinnerClasses(@PathParam("personId") int personId) throws Exception {
		ArrayList<PersonSpinnerClassResponse> classes = new ArrayList<PersonSpinnerClassResponse>();
		HashMap<Integer, SpinnerClass> scHash = Persons.getPersonsInstance().getPersonSpinnerClasses(personId);
		for (Map.Entry<Integer, SpinnerClass> entry : scHash.entrySet()) {
			PersonSpinnerClassResponse pscr = new PersonSpinnerClassResponse(personId, entry.getValue());
			classes.add(pscr);
		}
		return classes;
	}

	// Events services
	@GET
	@Path("/getStudentEvents/{personId}/{classId}")
	@Produces(MediaType.APPLICATION_JSON)
	public ArrayList<StudentSpinnerEvent> getStudentEvents(@PathParam("personId") int personId, @PathParam("classId") int classId) throws Exception {
		SpinnerClasses classes = SpinnerClasses.getSpinnerClassesInstance();
		return classes.getStudentEvents(classId, personId);
	}

	@GET
	@Path("/getEventRegisteredStudents/{classId}/{eventId}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Person> getEventRegisteredStudents(@PathParam("classId") int classId, @PathParam("eventId") int eventId) throws Exception {
		SpinnerClasses classes = SpinnerClasses.getSpinnerClassesInstance();
		return classes.getEventRegisteredStudents(classId, eventId);
	}

	@GET
	@Path("/getEventStandbyStudents/{classId}/{eventId}")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Person> getEventStandbyStudents(@PathParam("classId") int classId, @PathParam("eventId") int eventId) throws Exception {
		SpinnerClasses classes = SpinnerClasses.getSpinnerClassesInstance();
		return classes.getEventStandbyStudents(classId, eventId);
	}

	@GET
	@Path("/getStudentEvent/{personId}/{classId}/{eventId}")
	@Produces(MediaType.APPLICATION_JSON)
	public StudentSpinnerEvent getStudentEvent(@PathParam("personId") int personId, @PathParam("classId") int classId, @PathParam("eventId") int eventId) throws Exception {
		SpinnerClasses classes = SpinnerClasses.getSpinnerClassesInstance();
		return classes.getStudentEvent(classId, personId, eventId);
	}

	@POST
	@Path("/registerToEvent/{classId}/{eventId}/{studentId}")
	@Produces(MediaType.APPLICATION_JSON)
	public StudentSpinnerEvent registerToSpinnerEvent(@PathParam("classId") int classId, @PathParam("eventId") int eventId, @PathParam("studentId") int studentId) throws Exception {
		SpinnerClasses classes = SpinnerClasses.getSpinnerClassesInstance();
		StudentSpinnerEvent studentEvent = classes.registerToSpinnerEvent(classId, eventId, studentId);
		return studentEvent;
	}

	@POST
	@Path("/unRegisterFromEvent/{classId}/{eventId}/{studentId}")
	@Produces(MediaType.APPLICATION_JSON)
	public StudentSpinnerEvent unRegisterFromSpinnerEvent(@PathParam("classId") int classId, @PathParam("eventId") int eventId, @PathParam("studentId") int studentId) throws Exception {
		SpinnerClasses classes = SpinnerClasses.getSpinnerClassesInstance();
		StudentSpinnerEvent studentEvent = classes.unRegisterFromSpinnerEvent(classId, eventId, studentId);
		return studentEvent;
	}

	// @POST
	// @Path("/addEvents/{classId}")
	// @Consumes(MediaType.APPLICATION_JSON)
	// @Produces(MediaType.APPLICATION_JSON)
	// public List<SpinnerEvent> addEvents(@PathParam("classId") int classId, List<SpinnerEventInputRequest> input) throws Exception {
	// ArrayList<SpinnerEvent> events = new ArrayList<SpinnerEvent>();
	// Iterator<SpinnerEventInputRequest> iterator = input.iterator();
	// while (iterator.hasNext()) {
	// SpinnerEventInputRequest inputEvent = iterator.next();
	// SpinnerEvent se = addEvent(classId, inputEvent);
	// events.add(se);
	// }
	// return events;
	// }

	@POST
	@Path("/addEvents/{classId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<SpinnerEvent> addEvents(@PathParam("classId") int classId, List<SpinnerEventInputRequest> input) throws Exception {
		ArrayList<SpinnerEvent> events = new ArrayList<SpinnerEvent>();
		Iterator<SpinnerEventInputRequest> iterator = input.iterator();
		while (iterator.hasNext()) {
			SpinnerEventInputRequest inputEvent = iterator.next();
			if (inputEvent.getNumberOfOccurrences() > 0) {
				events.addAll(addRecurringEvent(classId, inputEvent));
			} else {
				events.add(addEvent(classId, inputEvent));
			}
		}
		return events;
	}

	private List<SpinnerEvent> addRecurringEvent(int classId, SpinnerEventInputRequest inputEvent) throws Exception {
		ArrayList<SpinnerEvent> events = new ArrayList<SpinnerEvent>();
		Date fromDate = inputEvent.getFromDate();
		String recurringId = UUID.randomUUID().toString();
		Date toDate = inputEvent.getToDate();
		String openForRegistrationMode = SpinnerClasses.getSpinnerClassesInstance().getSpinnerClass(classId).getOpenForRegistrationMode();
		Date openDate = DateUtils.calcOpenDate(openForRegistrationMode, fromDate);
		int lockForRegistration = SpinnerClasses.getSpinnerClassesInstance().getSpinnerClass(classId).getLockForRegistration();
		int interval = inputEvent.getInterval();
		for (int i = 0; i < inputEvent.getNumberOfOccurrences(); i++) {
			SpinnerEvent se = new SpinnerEvent(classId, recurringId, inputEvent.getEventName(), fromDate, toDate, lockForRegistration, openDate, inputEvent.getMaxCapacity(), inputEvent.getInstructorId(), inputEvent.getAddress(), inputEvent.getComments(), inputEvent.getStatus());
			se = SpinnerClasses.getSpinnerClassesInstance().addSpinnerEventToSpinnerCalendar(classId, se);
			events.add(se);
			fromDate = DateUtils.addDaysToDate(fromDate, interval);
			toDate = DateUtils.addDaysToDate(toDate, interval);
			openDate = DateUtils.calcOpenDate(openForRegistrationMode, fromDate);
		}
		return events;
	}

	private SpinnerEvent addEvent(@PathParam("classId") int classId, SpinnerEventInputRequest inputEvent) throws Exception {
		String openForRegistrationMode = SpinnerClasses.getSpinnerClassesInstance().getSpinnerClass(classId).getOpenForRegistrationMode();
		Date openDate = DateUtils.calcOpenDate(openForRegistrationMode, inputEvent.getFromDate());
		int lockForRegistration = SpinnerClasses.getSpinnerClassesInstance().getSpinnerClass(classId).getLockForRegistration();
		SpinnerEvent se = new SpinnerEvent(classId, "NA", inputEvent.getEventName(), inputEvent.getFromDate(), inputEvent.getToDate(), lockForRegistration, openDate, inputEvent.getMaxCapacity(), inputEvent.getInstructorId(), inputEvent.getAddress(), inputEvent.getComments(), inputEvent.getStatus());
		se = SpinnerClasses.getSpinnerClassesInstance().addSpinnerEventToSpinnerCalendar(classId, se);
		return se;
	}

	@POST
	@Path("/updateEvent/{classId}/{eventId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public SpinnerEvent updateEvent(@PathParam("classId") int classId, @PathParam("eventId") int eventId, SpinnerEventInputRequest inputEvent) throws Exception {
		String openForRegistrationMode = SpinnerClasses.getSpinnerClassesInstance().getSpinnerClass(classId).getOpenForRegistrationMode();
		Date openDate = DateUtils.calcOpenDate(openForRegistrationMode, inputEvent.getFromDate());
		int lockForRegistration = SpinnerClasses.getSpinnerClassesInstance().getSpinnerClass(classId).getLockForRegistration();
		SpinnerEvent se = new SpinnerEvent(classId, "NA", inputEvent.getEventName(), inputEvent.getFromDate(), inputEvent.getToDate(), lockForRegistration, openDate, inputEvent.getMaxCapacity(), inputEvent.getInstructorId(), inputEvent.getAddress(), inputEvent.getComments(), inputEvent.getStatus());
		se = SpinnerClasses.getSpinnerClassesInstance().updateSpinnerEventInSpinnerCalendar(classId, eventId, se);
		return se;
	}

/*	@POST
	@Path("/updateRecurringEvent/{classId}/{eventId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public List<SpinnerEvent> updateRecurringEvent(@PathParam("classId") int classId, @PathParam("recurringId") String recurringId, SpinnerEventInputRequest inputEvent) throws Exception {
		List<SpinnerEvent> newEvents = new ArrayList<SpinnerEvent>();
		HashMap<Integer, SpinnerEvent> events = SpinnerClasses.getSpinnerClassesInstance().getSpinnerClass(classId).getClassEvents().getSpinnerCalendarEventsHashMap();
		Iterator<Entry<Integer, SpinnerEvent>> it = events.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			SpinnerEvent se = (SpinnerEvent) pair.getValue();
			if (se.getRecurringId().equals(recurringId)) {
				SpinnerEvent newEvent = SpinnerClasses.getSpinnerClassesInstance().updateSpinnerEventInSpinnerCalendar(classId, se.getEventId(), newSE);
				newEvents.add(newEvent);
			}
		}
		return newEvents;
	}
*/
	@POST
	@Path("/deleteEvent/{classId}/{eventId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public SpinnerEvent deleteEvent(@PathParam("classId") int classId, @PathParam("eventId") int eventId) throws Exception {
		SpinnerEvent se = SpinnerClasses.getSpinnerClassesInstance().deleteSpinnerEventFromSpinnerCalendar(classId, eventId);
		return se;
	}

	@POST
	@Path("/deleteRecurringEvent/{classId}/{recurringId}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public SpinnerEvent deleteEvent(@PathParam("classId") int classId, @PathParam("recurringId") String recurringId) throws Exception {
		SpinnerEvent see = null;
		HashMap<Integer, SpinnerEvent> events = SpinnerClasses.getSpinnerClassesInstance().getSpinnerClass(classId).getClassEvents().getSpinnerCalendarEventsHashMap();
		Iterator it = events.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			SpinnerEvent se = (SpinnerEvent) pair.getValue();
			if (se.getRecurringId().equals(recurringId)) {
				see = SpinnerClasses.getSpinnerClassesInstance().deleteSpinnerEventFromSpinnerCalendar(classId, se.getEventId());
			}
		}
		return see;
	}

}
