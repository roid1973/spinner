package junit.spinnerCalendarTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import charcters.Person;
import charcters.PersonSpinnerClass;
import charcters.Persons;
import inputRequest.SpinnerEventInputRequest;
import spinnerCalendar.SpinnerCalendarServices;
import spinnerCalendar.SpinnerEvent;
import spinnerCalendar.Status;
import spinnerCalendar.StudentSpinnerEvent;
import spinnerClass.SpinnerClass;
import spinnerClass.SpinnerClassInputRequest;
import spinnerClass.SpinnerClasses;
import utils.DateUtils;
import utils.SpinnerConstants;

public class SpinnerEventTest {

	private static String spinnerClassName1 = "Spinning KY Test1";
	private static String spinnerClassName2 = "Spinning KY Test2";
	private static String spinnerClassName3 = "Spinning KY Test3";

	private static SpinnerClasses classes = null;
	private static SpinnerClass sc1 = null;
	private static SpinnerClass sc2 = null;
	private static SpinnerClass sc3 = null;

	// private static SpinnerCalendar sc = null;
	private static SpinnerEvent se1 = null;
	private static SpinnerEvent se2 = null;
	private static SpinnerEvent se3 = null;
	private static SpinnerEvent se4 = null;
	private static SpinnerEvent se41 = null;
	private static SpinnerEvent se5 = null;
	private static String se1_name = "SpinningT10";
	private static String se2_name = "SpinningT20";
	private static String se3_name = "SpinningT30";
	private static String se4_name = "SpinningT40";
	private static String se41_name = "SpinningT41";
	private static String se5_name = "SpinningT50";
	private static int se_max_capacity = 2;
	private static String se_address = "מתנס כוכב יאיר";

	// private static int instructorId = 5;
	private static int instructorId_null;
	private static String comments = "this field is saved for comments";

	private static Persons persons = null;
	private static Person instructor = null;
	private static Person p1 = null;
	private static Person p2 = null;
	private static Person p3 = null;
	private static String pn = "0542455820";
	private static String fn1 = "Tzela";
	private static String fn2 = "Beeri";
	private static String fn3 = "Nave";
	private static String ln = "Dayan";
	private static String add = "KY Hanegev 10";
	private static String e = "tzeladayan.un@gmail.com";
	private static String bd = "27/02/1975";
	private static String bd_notvalid = "27-02-1975";

	@BeforeClass
	public static void setUP() throws Exception {
		System.out.println("Spinner Test Start");

		//
		// System.out.println("print person classes");
		// printPersonClasses(1);
		// printPersonClasses(2);
		// printPersonClasses(3);
		// printPersonClasses(4);
		// printPersonClasses(5);
		//
		// System.out.println("print person events");
		// printEvents(1, 1);

		SimpleDateFormat spinnerEventDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
		spinnerEventDateFormat.setTimeZone(TimeZone.getTimeZone("Israel"));

		Date now = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(now);
		int nextYear = cal.get(Calendar.YEAR) + 1;

		Date from1 = spinnerEventDateFormat.parse("22.11." + nextYear + " 12:23:00");
		Date to1 = spinnerEventDateFormat.parse("22.11." + nextYear + " 13:00:00");

		Date from2 = spinnerEventDateFormat.parse("23.11.2016 12:00:00");
		Date to2 = spinnerEventDateFormat.parse("23.11.2016 13:00:00");

		Date from3 = spinnerEventDateFormat.parse("24.11.2000 12:00:00");
		Date to3 = spinnerEventDateFormat.parse("24.11.2000 13:00:00");

		Date from4 = new Date();
		from4.setTime(from4.getTime() + 1000000);
		Date to4 = new Date();
		to4.setTime(to4.getTime() + 3000000);

		Date from5 = new Date();
		from5.setTime(from5.getTime() + 1000000);
		Date to5 = new Date();
		to5.setTime(to5.getTime() + 3000000);

		Date current = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(current);
		c.add(Calendar.DATE, 1);
		Date openDate5 = c.getTime();

		System.out.println("adding classes to DB");
		classes = SpinnerClasses.getSpinnerClassesInstance();
		sc1 = classes.addSpinnerClass(setSpinnerClassInputRequest(spinnerClassName1, "WEEKLY", 1, "http://www.google.com"));
		sc2 = classes.addSpinnerClass(setSpinnerClassInputRequest(spinnerClassName2, "MONTHLY", 1, "http://www.google.com"));
		sc3 = classes.addSpinnerClass(setSpinnerClassInputRequest(spinnerClassName3, "WEEKLY", 1, "http://www.google.com"));

		System.out.println("adding students to DB");
		persons = Persons.getPersonsInstance();
		p1 = persons.addSPerson(pn, fn1, ln, add, e, DateUtils.stringToStudentBirthDate(bd));
		p2 = persons.addSPerson(pn, fn2, ln, add, e, null);
		p3 = persons.addSPerson(pn, fn3, ln, add, e, DateUtils.stringToStudentBirthDate(bd_notvalid));

		instructor = persons.addSPerson("(972)123456789", "instructor_first_name", "instructor_first_name", "instructor address", null, null);

		System.out.println("assigning students to classes");
		classes.assignStudentToClass(p1.getId(), sc1.getId(), 20);
		classes.assignStudentToClass(p2.getId(), sc1.getId(), 20);
		classes.assignStudentToClass(p3.getId(), sc1.getId(), 20);

		System.out.println("assigning instructor to classes");
		classes.assignInstructorToClass(instructor.getId(), sc1.getId());

		System.out.println("generating events");
		// TODO - add instructor to DB

		se1 = new SpinnerEvent(sc1.getId(), "NA", se1_name, from1, to1, (10 * 60), current, se_max_capacity, instructorId_null, se_address, comments, Status.EVENT_OPEN);
		se2 = new SpinnerEvent(sc1.getId(), "NA", se2_name, from2, to2, 0, current, se_max_capacity, instructor.getId(), se_address, comments, Status.EVENT_OPEN);
		se3 = new SpinnerEvent(sc1.getId(), "NA", se3_name, from3, to3, 0, current, se_max_capacity, instructor.getId(), se_address, comments, Status.EVENT_OPEN);
		se4 = new SpinnerEvent(sc1.getId(), "NA", se4_name, from4, to4, (10 * 60), current, se_max_capacity, instructor.getId(), se_address, comments, Status.EVENT_OPEN);
		se41 = new SpinnerEvent(sc1.getId(), "NA", se41_name, from4, to4, (10 * 60), current, se_max_capacity, instructor.getId(), se_address, comments, Status.EVENT_OPEN);
		se5 = new SpinnerEvent(sc1.getId(), "NA", se5_name, from5, to5, 0, openDate5, se_max_capacity, instructor.getId(), se_address, comments, Status.EVENT_OPEN);

		// System.out.println(spinnerEventDateFormat.format(se4.getFromDate()));
		// System.out.println(spinnerEventDateFormat.format(se4.getToDate()));
		// System.out.println(spinnerEventDateFormat.format(se4.getLockDate()));

		System.out.println("adding events to DB");
		se1 = classes.addSpinnerEventToSpinnerCalendar(sc1.getId(), se1);
		se2 = classes.addSpinnerEventToSpinnerCalendar(sc1.getId(), se2);
		se3 = classes.addSpinnerEventToSpinnerCalendar(sc1.getId(), se3);
		se4 = classes.addSpinnerEventToSpinnerCalendar(sc1.getId(), se4);
		se41 = classes.addSpinnerEventToSpinnerCalendar(sc1.getId(), se41);
		se5 = classes.addSpinnerEventToSpinnerCalendar(sc1.getId(), se5);

	}

	private static SpinnerClassInputRequest setSpinnerClassInputRequest(String className, String openForRegistrationMode, int lockForRegistration, String hyperLink) throws Exception {
		SpinnerClassInputRequest input = new SpinnerClassInputRequest();
		input.setClassName(className);
		input.setHyperLink(hyperLink);
		input.setLockForRegistration(lockForRegistration);
		input.setOpenForRegistrationMode(openForRegistrationMode);
		return input;
	}

	private static void printPersonClasses(int personId) throws Exception {
		HashMap<Integer, SpinnerClass> scMap = Persons.getPersonsInstance().getPersonSpinnerClasses(personId);
		printSprintClassList(scMap, personId);
	}

	private static void printSprintClassList(HashMap<Integer, SpinnerClass> scMap, int personId) {
		Iterator it = scMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			int scId = (Integer) pair.getKey();
			SpinnerClass sc = (SpinnerClass) pair.getValue();
			System.out.println("Person " + personId + " class: " + scId + " = " + sc.getSpinnerClassName());
			it.remove(); // avoids a ConcurrentModificationException
		}
	}

	private static void printEvents(int classId, int personId) throws Exception {
		SpinnerClasses scs = SpinnerClasses.getSpinnerClassesInstance();
		ArrayList<StudentSpinnerEvent> events = scs.getStudentEvents(classId, personId);
		printClassEvents(events, personId);
	}

	private static void printClassEvents(ArrayList<StudentSpinnerEvent> events, int personId) {
		Iterator it = events.iterator();
		while (it.hasNext()) {
			StudentSpinnerEvent se = (StudentSpinnerEvent) it.next();
			int eventId = se.getSpinnerEvent().getEventId();
			String eventName = se.getSpinnerEvent().getName();
			System.out.println("Person " + personId + " event: " + eventId + " = " + eventName);
			it.remove(); // avoids a ConcurrentModificationException
		}

	}

	@AfterClass
	public static void cleanDB() throws Exception {
		System.out.println("Clean Persons");
		persons.deletePerson(p1.getId());
		persons.deletePerson(p2.getId());
		persons.deletePerson(p3.getId());

		System.out.println("Clean Classes");
		classes.deleteSpinnerClass(sc1.getId());
		classes.deleteSpinnerClass(sc2.getId());
		classes.deleteSpinnerClass(sc3.getId());

		// persons.deletePerson(instructor.getId());

		System.out.println("tests finished");
	}

	@Test
	public void lockTimeTest() throws Exception {
		System.out.println("lockTimeTest");
		Date fromDate = se1.getFromDate();
		Date lockDate = se1.getLockDate();
		assertTrue(lockDate.before(fromDate));
	}

	@Test
	public void updateEventDetails() throws Exception {
		System.out.println("updateEventDetails");
		String eventNewName = "EVENT NEW_NAME";
		SpinnerEvent se1_new = new SpinnerEvent(sc1.getId(), "NA", eventNewName, se1.getFromDate(), se1.getToDate(), (10 * 60), se1.getOpenDate(), se_max_capacity, instructorId_null, se_address, comments, Status.EVENT_OPEN);
		se1 = classes.updateSpinnerEventInSpinnerCalendar(sc1.getId(), se1.getEventId(), se1_new);
		assertTrue(se1.getName().equals(eventNewName));
		SpinnerEvent se2_new = new SpinnerEvent(sc1.getId(), "NA", se1_name, se1.getFromDate(), se1.getToDate(), (10 * 60), se1.getOpenDate(), se_max_capacity, instructorId_null, se_address, comments, Status.EVENT_OPEN);
		se1 = classes.updateSpinnerEventInSpinnerCalendar(sc1.getId(), se1.getEventId(), se2_new);
	}

	@Test
	public void deleteEvent() throws Exception {
		System.out.println("deleteEvent");
		StudentSpinnerEvent studentEvent = null;
		assertEquals(20, classes.getSpinnerClass(sc1.getId()).getStudent(p3.getId()).getNumberOfValidRegistrations());
		studentEvent = classes.registerToSpinnerEvent(sc1.getId(), se41.getEventId(), p3.getId());
		assertTrue((studentEvent.getStudentRegisterationStatus().getStatus()).equals(Status.REGISTERED));
		assertEquals(19, classes.getSpinnerClass(sc1.getId()).getStudent(p3.getId()).getNumberOfValidRegistrations());
		SpinnerClasses.getSpinnerClassesInstance().deleteSpinnerEventFromSpinnerCalendar(sc1.getId(), se41.getEventId());
		assertEquals(20, classes.getSpinnerClass(sc1.getId()).getStudent(p3.getId()).getNumberOfValidRegistrations());
		se4 = classes.addSpinnerEventToSpinnerCalendar(sc1.getId(), se41);
	}

	@Test
	public void updatePersonDetails() throws Exception {
		System.out.println("updatePersonDetails");
		String newPhoneNumber = "(newPhoneNumber)";
		Person newp1 = persons.updatePersonDetails(p1.getId(), newPhoneNumber, fn1, ln, add, e, DateUtils.stringToStudentBirthDate(bd));
		assertTrue(newp1.getPhoneNumber().equals(newPhoneNumber));
		persons.updatePersonDetails(p1.getId(), pn, fn1, ln, add, e, DateUtils.stringToStudentBirthDate(bd));
	}

	@Test
	public void registerToHistoryEvent() throws Exception {
		System.out.println("registerToHistoryEvent");
		StudentSpinnerEvent studentEvent = null;
		List<Integer> registered = new ArrayList<Integer>();
		List<Integer> standBy = new ArrayList<Integer>();
		List<Person> persons;

		studentEvent = classes.registerToSpinnerEvent(sc1.getId(), se3.getEventId(), p1.getId());
		assertTrue((studentEvent.getStudentRegisterationStatus().getStatus()).equals(Status.FAILURE));
		classes.getStudentRegistrationEvents(sc1.getId(), p1.getId(), registered, standBy);
		assertFalse(registered.contains(se3.getEventId()));
		persons = classes.getEventRegisteredStudents(sc1.getId(), se3.getEventId());
		assertFalse(persons.contains(p1));
	}

	@Test
	public void unRegisterFromLockedForUnregister() throws Exception {
		System.out.println("unRegisterFromLockedForUnregister");
		StudentSpinnerEvent studentEvent = null;
		List<Integer> registered = new ArrayList<Integer>();
		List<Integer> standBy = new ArrayList<Integer>();
		List<Person> persons;

		studentEvent = classes.registerToSpinnerEvent(sc1.getId(), se4.getEventId(), p1.getId());
		assertTrue((studentEvent.getStudentRegisterationStatus().getStatus()).equals(Status.REGISTERED));
		assertEquals(19, studentEvent.getCredit());
		classes.getStudentRegistrationEvents(sc1.getId(), p1.getId(), registered, standBy);
		assertTrue(registered.contains(se4.getEventId()));
		persons = classes.getEventRegisteredStudents(sc1.getId(), se4.getEventId());
		assertTrue(persons.contains(p1));

		TimeUnit.SECONDS.sleep(1);

		studentEvent = classes.unRegisterFromSpinnerEvent(sc1.getId(), se4.getEventId(), p1.getId());
		assertTrue((studentEvent.getStudentRegisterationStatus().getStatus()).equals(Status.NOT_REGISTERED));
		assertEquals(19, studentEvent.getCredit());
		registered.clear();
		standBy.clear();
		classes.getStudentRegistrationEvents(sc1.getId(), p1.getId(), registered, standBy);
		assertFalse(registered.contains(se4.getEventId()));

		TimeUnit.SECONDS.sleep(1);

		studentEvent = classes.registerToSpinnerEvent(sc1.getId(), se4.getEventId(), p1.getId());
		assertTrue((studentEvent.getStudentRegisterationStatus().getStatus()).equals(Status.REGISTERED));
		assertEquals(19, studentEvent.getCredit());
		classes.getStudentRegistrationEvents(sc1.getId(), p1.getId(), registered, standBy);
		assertTrue(registered.contains(se4.getEventId()));

		TimeUnit.SECONDS.sleep(1);

		studentEvent = classes.unRegisterFromSpinnerEvent(sc1.getId(), se4.getEventId(), p1.getId());
		assertTrue((studentEvent.getStudentRegisterationStatus().getStatus()).equals(Status.NOT_REGISTERED));
		assertEquals(19, studentEvent.getCredit());
		classes.getStudentRegistrationEvents(sc1.getId(), p1.getId(), registered, standBy);
		assertFalse(registered.contains(se4.getEventId()));

		classes.addCredit(p1.getId(), sc1.getId(), 1);
		// assertEquals(20, studentEvent.getCredit());
		assertEquals(20, classes.getSpinnerClass(sc1.getId()).getStudent(p1.getId()).getNumberOfValidRegistrations());

	}

	@Test
	public void addEventToWeeklyClassTest() throws Exception {
		System.out.println("addEventToWeeklyClassTest");		
		String eventName = "addEventW";
		int classId = sc1.getId();
		assertTrue("WEEKLY".compareTo(sc1.getOpenForRegistrationMode()) == 0);
		String tmz = "Israel";
		String fromDate = "31/10/2020 22:00";
		String toDate = "31/10/2020 22:30";
		String openDate = "22/10/2020 17:00";		
		addEventTest(eventName, classId, tmz, fromDate, toDate, openDate);
	}
	
	@Test
	public void addEventToMonthlyClassTest() throws Exception {
		System.out.println("addEventToMonthlyClassTest");		
		String eventName = "addEventM";
		int classId = sc2.getId();		
		assertTrue("MONTHLY".compareTo(sc2.getOpenForRegistrationMode()) == 0);
		String tmz = "Israel";
		String fromDate = "15/10/2020 22:00";
		String toDate = "15/10/2020 22:30";
		String openDate = "30/09/2020 08:00";		
		addEventTest(eventName, classId, tmz, fromDate, toDate, openDate);
	}

	private void addEventTest(String eventName, int classId, String tmz, String fromDate, String toDate, String openDate) throws Exception {
		SpinnerEventInputRequest event = new SpinnerEventInputRequest();
		event.setEventName(eventName);
		event.setTimeZone(tmz);
		event.setFromDate(fromDate);
		event.setToDate(toDate);
		event.setMaxCapacity(23);
		event.setInstructorId(instructor.getId());
		List<SpinnerEventInputRequest> eventsListInput = new ArrayList<SpinnerEventInputRequest>();
		eventsListInput.add(event);
		SpinnerCalendarServices scs = new SpinnerCalendarServices();
		List<SpinnerEvent> eventsListOutput = scs.addEvents(classId, eventsListInput);
		Iterator<SpinnerEvent> iterator = eventsListOutput.iterator();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YYYY HH:mm");
		sdf.setTimeZone(TimeZone.getTimeZone(event.getTimeZone()));
		while (iterator.hasNext()) {
			SpinnerEvent eventResult = iterator.next();			
			System.out.println("getFromDate: " + sdf.format(eventResult.getFromDate()));			
			System.out.println("getOpenDate: " + sdf.format(eventResult.getOpenDate()));			
			assertTrue(fromDate.compareTo(sdf.format(eventResult.getFromDate())) == 0);
			assertTrue(toDate.compareTo(sdf.format(eventResult.getToDate())) == 0);
			assertTrue(openDate.compareTo(sdf.format(eventResult.getOpenDate())) == 0);
		}
	}
	
	@Test
	public void recuringEventsTest() throws Exception {
		System.out.println("recuringEventsTest");		
		String eventName = "addRecEvent";
		int classId = sc1.getId();
		assertTrue("WEEKLY".compareTo(sc1.getOpenForRegistrationMode()) == 0);
		String tmz = "Israel";
		String fromDate = "15/10/2020 22:00";
		String toDate = "15/10/2020 22:30";
		SpinnerEventInputRequest event = new SpinnerEventInputRequest();
		event.setEventName(eventName);
		event.setTimeZone(tmz);
		event.setFromDate(fromDate);
		event.setToDate(toDate);
		event.setMaxCapacity(23);
		event.setInstructorId(instructor.getId());
		event.setInterval("WEEKLY");
		event.setNumberOfOccurrences(3);
		List<SpinnerEventInputRequest> eventsListInput = new ArrayList<SpinnerEventInputRequest>();
		eventsListInput.add(event);
		SpinnerCalendarServices scs = new SpinnerCalendarServices();
		List<SpinnerEvent> eventsListOutput = scs.addEvents(classId, eventsListInput);
		assertTrue(eventsListOutput.size()==3);
		
		Iterator<SpinnerEvent> iterator = eventsListOutput.iterator();
		SpinnerEvent eventResult = iterator.next();	
		String recurringId = eventResult.getRecurringId();
		assertTrue(recurringId.compareTo("NA")!=0);		
		
		assertTrue(eventResult.getMaxCapacity()==23);
		event.setMaxCapacity(20);
		eventsListOutput = scs.updateRecurringEvent(classId, recurringId, event);
		Iterator<SpinnerEvent> updateIterator = eventsListOutput.iterator();
		SpinnerEvent updateEventResult = updateIterator.next();	
		assertTrue(updateEventResult.getMaxCapacity()==20);		
		
		scs.deleteRecurringEvent(classId, recurringId);		
	}

	@Test
	public void registerToLockedEvent() throws Exception {
		System.out.println("registerToLockedEvent");
		StudentSpinnerEvent studentEvent = null;
		List<Integer> registered = new ArrayList<Integer>();
		List<Integer> standBy = new ArrayList<Integer>();
		List<Person> persons;

		studentEvent = classes.registerToSpinnerEvent(sc1.getId(), se5.getEventId(), p1.getId());
		assertTrue((studentEvent.getStudentRegisterationStatus().getStatus()).equals(Status.FAILURE));
		classes.getStudentRegistrationEvents(sc1.getId(), p1.getId(), registered, standBy);
		assertFalse(registered.contains(se5.getEventId()));
		persons = classes.getEventRegisteredStudents(sc1.getId(), se5.getEventId());
		assertFalse(persons.contains(p1));
	}

	@Test
	public void addExistingPerson() throws Exception {
		Person ep1 = persons.addSPerson("1234567890", "existingPerson", "existingPerson_LN1", add, e, DateUtils.stringToStudentBirthDate(bd));
		Person ep2 = persons.addSPerson("1234567890", "existingPerson", "existingPerson_LN2", add, e, DateUtils.stringToStudentBirthDate(bd));
		assertTrue(ep1.getId() == ep2.getId());
		persons.deletePerson(ep1.getId());
	}

	@Test
	public void registerTest() throws Exception {
		System.out.println("registerTest Started");

		StudentSpinnerEvent studentEvent = null;
		List<Integer> registered = new ArrayList<Integer>();
		List<Integer> standBy = new ArrayList<Integer>();
		List<Person> persons;

		studentEvent = classes.registerToSpinnerEvent(sc1.getId(), se1.getEventId(), p1.getId());
		assertTrue((studentEvent.getStudentRegisterationStatus().getStatus()).equals(Status.REGISTERED));
		assertEquals(1, se1.getNumberOfRegistered());
		classes.getStudentRegistrationEvents(sc1.getId(), p1.getId(), registered, standBy);
		assertTrue(registered.contains(se1.getEventId()));
		assertTrue(registered.size() == 1);
		persons = classes.getEventRegisteredStudents(sc1.getId(), se1.getEventId());
		assertTrue(persons.contains(p1));

		studentEvent = classes.registerToSpinnerEvent(sc1.getId(), se1.getEventId(), p1.getId());
		assertTrue((studentEvent.getStudentRegisterationStatus().getStatus()).equals(Status.REGISTERED));
		assertEquals(1, se1.getNumberOfRegistered());

		studentEvent = classes.registerToSpinnerEvent(sc1.getId(), se1.getEventId(), p2.getId());
		assertTrue((studentEvent.getStudentRegisterationStatus().getStatus()).equals(Status.REGISTERED));
		assertEquals(19, studentEvent.getCredit());
		assertEquals(2, se1.getNumberOfRegistered());
		assertTrue(se1.getIsEventFull());
		persons = classes.getEventRegisteredStudents(sc1.getId(), se1.getEventId());
		assertTrue(persons.contains(p1));
		assertTrue(persons.contains(p2));

		studentEvent = classes.registerToSpinnerEvent(sc1.getId(), se1.getEventId(), p3.getId());
		assertTrue((studentEvent.getStudentRegisterationStatus().getStatus()).equals(Status.STANDBY));
		assertEquals(2, se1.getNumberOfRegistered());
		assertEquals(1, se1.getNumberOfStandBy());
		classes.getStudentRegistrationEvents(sc1.getId(), p3.getId(), registered, standBy);
		assertFalse(registered.contains(se1.getEventId()));
		assertTrue(standBy.contains(se1.getEventId()));

		TimeUnit.SECONDS.sleep(1);

		studentEvent = classes.unRegisterFromSpinnerEvent(sc1.getId(), se1.getEventId(), p2.getId());
		assertTrue((studentEvent.getStudentRegisterationStatus().getStatus()).equals(Status.NOT_REGISTERED));
		assertEquals(20, studentEvent.getCredit());
		assertEquals(1, se1.getNumberOfRegistered());
		assertEquals(1, se1.getNumberOfStandBy());
		assertFalse(se1.getIsEventFull());
		classes.getStudentRegistrationEvents(sc1.getId(), p2.getId(), registered, standBy);
		assertFalse(registered.contains(se1.getEventId()));

		studentEvent = classes.registerToSpinnerEvent(sc1.getId(), se1.getEventId(), p3.getId());
		assertTrue((studentEvent.getStudentRegisterationStatus().getStatus()).equals(Status.REGISTERED));
		assertEquals(2, se1.getNumberOfRegistered());
		assertEquals(0, se1.getNumberOfStandBy());
		classes.getStudentRegistrationEvents(sc1.getId(), p3.getId(), registered, standBy);
		assertTrue(registered.contains(se1.getEventId()));

		studentEvent = classes.registerToSpinnerEvent(sc1.getId(), se1.getEventId(), p2.getId());
		assertTrue((studentEvent.getStudentRegisterationStatus().getStatus()).equals(Status.STANDBY));
		assertEquals(2, se1.getNumberOfRegistered());
		studentEvent = classes.registerToSpinnerEvent(sc1.getId(), se1.getEventId(), p2.getId());
		assertTrue((studentEvent.getStudentRegisterationStatus().getStatus()).equals(Status.STANDBY));
		assertEquals(2, se1.getNumberOfRegistered());
		classes.getStudentRegistrationEvents(sc1.getId(), p2.getId(), registered, standBy);
		assertFalse(registered.contains(se1.getEventId()));
		assertTrue(standBy.contains(se1.getEventId()));
		persons = classes.getEventStandbyStudents(sc1.getId(), se1.getEventId());
		assertTrue(persons.contains(p2));
		persons = classes.getEventRegisteredStudents(sc1.getId(), se1.getEventId());
		assertFalse(persons.contains(p2));

		studentEvent = classes.unRegisterFromSpinnerEvent(sc1.getId(), se1.getEventId(), p1.getId());
		studentEvent = classes.unRegisterFromSpinnerEvent(sc1.getId(), se1.getEventId(), p3.getId());
		System.out.println("registerTest Finished");
	}

	@Test
	public void addCredit() throws Exception {
		PersonSpinnerClass psc = classes.addCredit(p1.getId(), sc1.getId(), 30);
		assertTrue(psc.getNumberOfValidRegistrations() >= 30);
		classes.addCredit(p1.getId(), sc1.getId(), -30);
	}

	@Test
	public void validCredit() throws Exception {
		PersonSpinnerClass psc = classes.assignStudentToClass(p3.getId(), sc1.getId(), 1);
		psc.updateCredit(1);

		StudentSpinnerEvent studentEvent = null;
		studentEvent = classes.registerToSpinnerEvent(sc1.getId(), se1.getEventId(), p3.getId());
		assertTrue(psc.getNumberOfValidRegistrations() == 0);
		assertTrue((studentEvent.getStudentRegisterationStatus().getStatus()).equals(Status.REGISTERED));
		studentEvent = classes.registerToSpinnerEvent(sc1.getId(), se2.getEventId(), p3.getId());
		assertTrue(psc.getNumberOfValidRegistrations() == 0);
		assertTrue((studentEvent.getStudentRegisterationStatus().getStatus()).equals(Status.NO_VALID_CREDIT));
		studentEvent = classes.unRegisterFromSpinnerEvent(sc1.getId(), se1.getEventId(), p3.getId());
		assertTrue(psc.getNumberOfValidRegistrations() == 1);
		assertTrue((studentEvent.getStudentRegisterationStatus().getStatus()).equals(Status.NOT_REGISTERED));

		psc.updateCredit(20);
	}

	@Test
	public void checkSpinnerEventFields() throws Exception {
		System.out.println("checkSpinnerEventFields");
		assertNotNull(se2);
		assertEquals(se2_name, se2.getName());
		assertEquals(0, se2.getNumberOfRegistered());
	}

	@Test
	public void getSpinnerClass() throws Exception {
		System.out.println("getSpinnerClass");
		assertNotNull(classes.getSpinnerClass(sc1.getId()));
	}

	@Test
	public void testAddRemovePersonFromClass() throws Exception {
		System.out.println("testAddRemovePersonFromClass");

		assertPersonClassNotNull(p1, sc1);

		classes.unAssignStudentFromClass(p1.getId(), sc1.getId());
		assertPersonClassNull(p1, sc1);

		classes.assignStudentToClass(p1.getId(), sc1.getId(), 20);
		assertPersonClassNotNull(p1, sc1);

		classes.assignStudentToClass(p1.getId(), sc2.getId(), 20);
		assertPersonClassNotNull(p1, sc2);

		assertPersonClassNull(p1, sc3);
	}

	public void assertPersonClassNotNull(Person person, SpinnerClass spinnerClass) throws Exception {
		HashMap<Integer, SpinnerClass> personSpinnerClasses = null;
		personSpinnerClasses = persons.getPersonSpinnerClasses(person.getId());
		SpinnerClass sc = personSpinnerClasses.get(spinnerClass.getId());
		assertNotNull(sc);
		sc = classes.getSpinnerClass(spinnerClass.getId());
		PersonSpinnerClass psc = sc.getStudent(person.getId());
		assertNotNull(psc);
	}

	public void assertPersonClassNull(Person person, SpinnerClass spinnerClass) throws Exception {
		HashMap<Integer, SpinnerClass> personSpinnerClasses = null;
		personSpinnerClasses = persons.getPersonSpinnerClasses(person.getId());
		SpinnerClass sc = personSpinnerClasses.get(spinnerClass.getId());
		assertNull(sc);
		sc = classes.getSpinnerClass(spinnerClass.getId());
		PersonSpinnerClass psc = sc.getStudent(person.getId());
		assertNull(psc);
	}

	@Test
	public void testAddDeleteClass() throws Exception {
		SpinnerClass sc = classes.addSpinnerClass(setSpinnerClassInputRequest("testAddDeleteClass", "WEEKLY", 1, "http://www.google.com"));
		assertNotNull(classes.getSpinnerClass(sc.getId()));
		classes.deleteSpinnerClass(sc.getId());
		assertNull(classes.getSpinnerClass(sc.getId()));
	}

	@Test
	public void getSpinnerCalendarEvents() throws Exception {
		System.out.println("getSpinnerCalendarEvents");
		ArrayList<StudentSpinnerEvent> sc_events = classes.getStudentEvents(sc1.getId(), p1.getId());
		assertNotNull(sc_events);

		boolean se1_valid = false;

		Iterator<StudentSpinnerEvent> it = sc_events.iterator();
		while (it.hasNext()) {
			StudentSpinnerEvent sse = it.next();
			SpinnerEvent se = sse.getSpinnerEvent();
			if (se.getName().equals(se1_name) && se.getFromDate() == se1.getFromDate() && se.getToDate() == se1.getToDate() && se.getMaxCapacity() == se_max_capacity) {
				se1_valid = true;
			}
		}

		assertTrue(se1_valid);
	}

}
