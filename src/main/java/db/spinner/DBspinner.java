package db.spinner;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.*;

import charcters.*;
import spinnerCalendar.SpinnerEvent;
import spinnerCalendar.Status;
import spinnerClass.SpinnerClass;
import spinnerClass.SpinnerClasses;

public class DBspinner {
	public static final String TABLE_PERSON = "person";
	public static final String TABLE_EVENTS = "events";
	private static final String TABLE_PERSON_CLASS = "personClass";
	private static final String TABLE_CLASS = "class";
	private static final String TABLE_REGISTRATION = "registration";

	// Events Table

	public static int findEvent(int classId, String eventName, Date fromDate) throws Exception {
		int eventId = 0;
		try (Connection conn = DButils.getDBconnection()) {
			PreparedStatement preparedStmt = prepareSatementFindEvent(classId, eventName, fromDate, conn);

			if (preparedStmt.execute()) {
				ResultSet rs = preparedStmt.getResultSet();
				while (rs.next()) {
					eventId = rs.getInt("eventId");
				}
			}
			return eventId;
		}
	}

	private static PreparedStatement prepareSatementFindEvent(int classId, String eventName, Date fromDate, Connection conn) throws SQLException {
		String query = "select eventId from " + TABLE_EVENTS + " where classId=? and eventName=? and fromDate=?";
		PreparedStatement preparedStmt = conn.prepareStatement(query);
		prepareSatementFindEvent(classId, eventName, fromDate, preparedStmt);
		return preparedStmt;
	}

	private static void prepareSatementFindEvent(int classId, String eventName, Date fromDate, PreparedStatement preparedStmt) throws SQLException {
		preparedStmt.setInt(1, classId);
		preparedStmt.setString(2, eventName);
		preparedStmt.setTimestamp(3, getSqlDate(fromDate));
	}

	public static SpinnerEvent addSpinnerEventInToDB(SpinnerEvent se) throws Exception {
		try (Connection conn = DButils.getDBconnection()) {
			PreparedStatement preparedStmt = prepareSatementInsertEvent(se, conn);
			preparedStmt.executeUpdate();
			ResultSet rs = preparedStmt.getGeneratedKeys();
			if (rs.next()) {
				int eventId = rs.getInt(1);
				se.setEventId(eventId);
			}
			return se;
		}
	}

	public static HashMap<Integer, SpinnerEvent> getSpinnerCalendarEventsFromDB(int classId) throws Exception {
		try (Connection conn = DButils.getDBconnection()) {
			HashMap<Integer, SpinnerEvent> eventsList = new HashMap<Integer, SpinnerEvent>();
			PreparedStatement preparedStmt = prepareSatementGetSpinnerCalendarEventsFromDB(classId, conn);

			if (preparedStmt.execute()) {
				ResultSet rs = preparedStmt.getResultSet();

				while (rs.next()) {
					int eventId = rs.getInt("eventId");
					String recurringId = rs.getString("recurringId");
					String eventName = rs.getString("eventName");
					Date fromDate = rs.getTimestamp("fromDate");
					Date toDate = rs.getTimestamp("toDate");
					int lockTime = rs.getInt("lockTime");
					Date openDate = rs.getTimestamp("openDate");
					int maxCapacity = rs.getInt("maxCapacity");
					int instructorId = rs.getInt("instructorId");
					String address = rs.getString("address");
					String comments = rs.getString("comments");
					String status = rs.getString("status");
					SpinnerEvent se = new SpinnerEvent(classId, eventId, recurringId, eventName, fromDate, toDate, lockTime, openDate, maxCapacity, instructorId, address, comments, status);
					eventsList.put(se.getEventId(), se);
				}
			}
			return eventsList;
		}
	}

	private static PreparedStatement prepareSatementGetSpinnerCalendarEventsFromDB(int classId, Connection conn) throws SQLException {
		String query = "select * from events where classId=?";
		PreparedStatement preparedStmt = conn.prepareStatement(query);
		prepareStatementgetSpinnerCalendarEventsFromDB(classId, preparedStmt);
		return preparedStmt;
	}

	private static void prepareStatementgetSpinnerCalendarEventsFromDB(int classId, PreparedStatement preparedStmt) throws SQLException {
		preparedStmt.setInt(1, classId);
	}

	private static PreparedStatement prepareSatementInsertEvent(SpinnerEvent se, Connection conn) throws Exception {
		String query = " insert into " + TABLE_EVENTS + " (recurringId, classId, eventName, fromDate, toDate, lockTime, openDate, maxCapacity, instructorId, address, comments, status)" + " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?)";
		PreparedStatement preparedStmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		prepareStatementInsertEvent(se, preparedStmt);
		return preparedStmt;
	}

	private static void prepareStatementInsertEvent(SpinnerEvent se, PreparedStatement preparedStmt) throws Exception {
		preparedStmt.setString(1, se.getRecurringId());
		preparedStmt.setInt(2, se.getClassId());
		preparedStmt.setString(3, se.getName());
		preparedStmt.setTimestamp(4, getSqlDate(se.getFromDate()));
		preparedStmt.setTimestamp(5, getSqlDate(se.getToDate()));
		int lockTime = (int) (se.getFromDate().getTime() - se.getLockDate().getTime()) / (1000 * 60);
		preparedStmt.setInt(6, lockTime);
		preparedStmt.setTimestamp(7, getSqlDate(se.getOpenDate()));
		preparedStmt.setInt(8, se.getMaxCapacity());
		preparedStmt.setInt(9, se.getInstructorId());
		preparedStmt.setString(10, se.getAddress());
		preparedStmt.setString(11, se.getComments());
		preparedStmt.setString(12, se.getStatus());
	}

	private static void prepareStatementUpdateEvent(SpinnerEvent newEvent, PreparedStatement preparedStmt) throws Exception {
		preparedStmt.setString(1, newEvent.getName());
		preparedStmt.setTimestamp(2, getSqlDate(newEvent.getFromDate()));
		preparedStmt.setTimestamp(3, getSqlDate(newEvent.getToDate()));
		int lockTime = (int) (newEvent.getFromDate().getTime() - newEvent.getLockDate().getTime()) / (1000 * 60);
		preparedStmt.setInt(4, lockTime);
		preparedStmt.setTimestamp(5, getSqlDate(newEvent.getOpenDate()));
		preparedStmt.setInt(6, newEvent.getMaxCapacity());
		preparedStmt.setInt(7, newEvent.getInstructorId());
		preparedStmt.setString(8, newEvent.getAddress());
		preparedStmt.setString(9, newEvent.getComments());
		preparedStmt.setString(10, newEvent.getStatus());
		preparedStmt.setInt(11, newEvent.getEventId());
	}

	public static void updateSpinnerEvent(SpinnerEvent newEvent) throws Exception {
		try (Connection conn = DButils.getDBconnection()) {
			PreparedStatement preparedStmt = prepareSatementUpdateEvent(newEvent, conn);
			preparedStmt.execute();
		}
	}

	private static PreparedStatement prepareSatementUpdateEvent(SpinnerEvent newEvent, Connection conn) throws Exception {
		String query = " update " + TABLE_EVENTS + " set eventName=?, fromDate=?, toDate=?, lockTime=?, openDate=?, maxCapacity=?, instructorId=?, address=?, comments=?, status=? where eventId=?";
		PreparedStatement preparedStmt = conn.prepareStatement(query);
		prepareStatementUpdateEvent(newEvent, preparedStmt);
		return preparedStmt;
	}

	// private static void prepareSatementUpdateEvent(SpinnerEvent se,
	// PreparedStatement preparedStmt) throws SQLException {
	// preparedStmt.setString(1, se.getName());
	// preparedStmt.setTimestamp(2, getSqlDate(se.getFromDate()));
	// preparedStmt.setTimestamp(3, getSqlDate(se.getToDate()));
	// int lockTime = (int) (se.getFromDate().getTime() -
	// se.getLockDate().getTime()) / (1000 * 60);
	// preparedStmt.setInt(4, lockTime);
	// preparedStmt.setInt(5, se.getMaxCapacity());
	// preparedStmt.setInt(6, se.getInstructorId());
	// preparedStmt.setString(7, se.getAddress());
	// preparedStmt.setString(8, se.getComments());
	// }

	public static void updateInstructorEvent(int instructorId, int eventId) throws Exception {
		try (Connection conn = DButils.getDBconnection()) {
			PreparedStatement preparedStmt = prepareSatementUpdateInstructorEvent(instructorId, eventId, conn);
			preparedStmt.execute();
		}
	}

	private static PreparedStatement prepareSatementUpdateInstructorEvent(int instructorId, int eventId, Connection conn) throws SQLException {
		String query = " update " + TABLE_EVENTS + " set instructorId=? where eventId=?";
		PreparedStatement preparedStmt = conn.prepareStatement(query);
		prepareSatementUpdateInstructorEvent(instructorId, eventId, preparedStmt);
		return preparedStmt;
	}

	private static void prepareSatementUpdateInstructorEvent(int instructorId, int eventId, PreparedStatement preparedStmt) throws SQLException {
		preparedStmt.setInt(1, instructorId);
		preparedStmt.setInt(2, eventId);
	}

	public static void deleteSpinnerEventFromDB(int eventId) throws Exception {
		try (Connection conn = DButils.getDBconnection()) {
			PreparedStatement preparedStmt = prepareSatementDeleteEvent(eventId, conn);
			preparedStmt.execute();
		}
	}

	public static void deleteClassEvents(int classId) throws Exception {
		try (Connection conn = DButils.getDBconnection()) {
			PreparedStatement preparedStmt = prepareSatementDeleteClassEvents(classId, conn);
			preparedStmt.execute();
		}
	}

	private static PreparedStatement prepareSatementDeleteEvent(int eventId, Connection conn) throws SQLException {
		String query = " delete from " + TABLE_EVENTS + " where eventId=?";
		PreparedStatement preparedStmt = conn.prepareStatement(query);
		prepareSatementDeleteEvent(eventId, preparedStmt);
		return preparedStmt;
	}

	private static PreparedStatement prepareSatementDeleteClassEvents(int classId, Connection conn) throws SQLException {
		String query = " delete from " + TABLE_EVENTS + " where classId=?";
		PreparedStatement preparedStmt = conn.prepareStatement(query);
		prepareSatementDeleteClassEvents(classId, preparedStmt);
		return preparedStmt;
	}

	private static void prepareSatementDeleteEvent(int eventId, PreparedStatement preparedStmt) throws SQLException {
		preparedStmt.setInt(1, eventId);
	}

	private static void prepareSatementDeleteClassEvents(int classId, PreparedStatement preparedStmt) throws SQLException {
		preparedStmt.setInt(1, classId);
	}

	// Person Table
	public static Person insertPersonInToDB(Person p) throws Exception {
		try (Connection conn = DButils.getDBconnection()) {
			PreparedStatement preparedStmt = prepareSatementInsertPerson(p, conn);
			preparedStmt.executeUpdate();
			ResultSet rs = preparedStmt.getGeneratedKeys();
			if (rs.next()) {
				int personId = rs.getInt(1);
				p.setId(personId);
			}
			return p;
		}
	}

	private static PreparedStatement prepareSatementInsertPerson(Person p, Connection conn) throws SQLException {
		String query = " insert into " + TABLE_PERSON + " (phoneNumber, firstName, lastName, address, email, birthDate)" + " values (?, ?, ?, ?, ?, ?)";
		PreparedStatement preparedStmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		prepareStatementInsertPerson(p, preparedStmt);
		return preparedStmt;
	}

	private static void prepareStatementInsertPerson(Person p, PreparedStatement preparedStmt) throws SQLException {
		preparedStmt.setString(1, p.getPhoneNumber());
		preparedStmt.setString(2, p.getFirstName());
		preparedStmt.setString(3, p.getLastName());
		preparedStmt.setString(4, p.getAddress());
		preparedStmt.setString(5, p.getEmail());
		preparedStmt.setTimestamp(6, getSqlDate(p.getBirthDay()));
	}

	public static void deletePersonFromDB(int personId) throws Exception {
		try (Connection conn = DButils.getDBconnection()) {
			PreparedStatement prepareSatementDeletePersonFromClass = prepareSatementDeletePerson(TABLE_PERSON_CLASS, personId, conn);
			prepareSatementDeletePersonFromClass.execute();
			PreparedStatement prepareSatementDeletePerson = prepareSatementDeletePerson(TABLE_PERSON, personId, conn);
			prepareSatementDeletePerson.execute();
		}
	}

	public static void removePersonFromClass(int perosnId, int classId) throws Exception {
		try (Connection conn = DButils.getDBconnection()) {
			PreparedStatement prepareSatementDeletePersonFromClass = prepareSatementDeletePersonFromClass(perosnId, classId, conn);
			prepareSatementDeletePersonFromClass.execute();
		}
	}

	private static PreparedStatement prepareSatementDeletePerson(String tableName, int personId, Connection conn) throws SQLException {
		String query = " delete from " + tableName + " where personId=?";
		PreparedStatement preparedStmt = conn.prepareStatement(query);
		prepareSatementDeletePerson(personId, preparedStmt);
		return preparedStmt;
	}

	private static PreparedStatement prepareSatementDeletePersonFromClass(int personId, int classId, Connection conn) throws SQLException {
		String query = " delete from " + TABLE_PERSON_CLASS + " where personId=? and classId=?";
		PreparedStatement preparedStmt = conn.prepareStatement(query);
		prepareSatementDeletePersonFromClass(personId, classId, preparedStmt);
		return preparedStmt;
	}

	private static void prepareSatementDeletePerson(int personId, PreparedStatement preparedStmt) throws SQLException {
		preparedStmt.setInt(1, personId);

	}

	private static void prepareSatementDeletePersonFromClass(int personId, int classId, PreparedStatement preparedStmt) throws SQLException {
		preparedStmt.setInt(1, personId);
		preparedStmt.setInt(2, classId);
	}

	public static void updatePersonDetails(Person newPerson) throws Exception {
		try (Connection conn = DButils.getDBconnection()) {
			PreparedStatement prepareSatementUpdatePersonDetails = prepareSatementUpdatePersonDetails(newPerson, conn);
			prepareSatementUpdatePersonDetails.execute();
		}
	}

	private static PreparedStatement prepareSatementUpdatePersonDetails(Person newPerson, Connection conn) throws SQLException {
		String query = " update " + TABLE_PERSON + " set phoneNumber=?, firstName=?, lastName=?, address=?, email=?, birthDate=? where personId=?";
		PreparedStatement preparedStmt = conn.prepareStatement(query);
		prepareSatementUpdatePersonDetails(newPerson, preparedStmt);
		return preparedStmt;
	}

	private static void prepareSatementUpdatePersonDetails(Person newPerson, PreparedStatement preparedStmt) throws SQLException {
		preparedStmt.setString(1, newPerson.getPhoneNumber());
		preparedStmt.setString(2, newPerson.getFirstName());
		preparedStmt.setString(3, newPerson.getLastName());
		preparedStmt.setString(4, newPerson.getAddress());
		preparedStmt.setString(5, newPerson.getEmail());
		preparedStmt.setTimestamp(6, getSqlDate(newPerson.getBirthDay()));
		preparedStmt.setInt(7, newPerson.getId());
	}

	public static void unAssignPersonsFromClass(int classId) throws Exception {
		try (Connection conn = DButils.getDBconnection()) {
			PreparedStatement prepareSatementDeletePersonFromClass = prepareSatementUnAssignPersonsFromClass(classId, conn);
			prepareSatementDeletePersonFromClass.execute();
		}
	}

	private static PreparedStatement prepareSatementUnAssignPersonsFromClass(int classId, Connection conn) throws SQLException {
		String query = " delete from " + TABLE_PERSON_CLASS + " where classId=?";
		PreparedStatement preparedStmt = conn.prepareStatement(query);
		prepareSatementUnAssignPersonsFromClass(classId, preparedStmt);
		return preparedStmt;
	}

	private static void prepareSatementUnAssignPersonsFromClass(int classId, PreparedStatement preparedStmt) throws SQLException {
		preparedStmt.setInt(1, classId);
	}

	public static int findPerson(String phoneNumber, String firstName) throws Exception {
		try (Connection conn = DButils.getDBconnection()) {
			int personId = 0;
			PreparedStatement preparedStmt = prepareSatementFindPerson(phoneNumber, firstName, conn);

			if (preparedStmt.execute()) {
				ResultSet rs = preparedStmt.getResultSet();
				while (rs.next()) {
					personId = rs.getInt("personId");
				}
			}
			return personId;
		}
	}

	private static PreparedStatement prepareSatementFindPerson(String phoneNumber, String firstName, Connection conn) throws SQLException {
		String query = "select personId from " + TABLE_PERSON + " where phoneNumber=? and firstName=?";
		PreparedStatement preparedStmt = conn.prepareStatement(query);
		prepareSatementFindPerson(phoneNumber, firstName, preparedStmt);
		return preparedStmt;
	}

	private static void prepareSatementFindPerson(String phoneNumber, String firstName, PreparedStatement preparedStmt) throws SQLException {
		preparedStmt.setString(1, phoneNumber);
		preparedStmt.setString(2, firstName);
	}

	public static HashMap<Integer, Person> getPersonHashMapFromDB() throws Exception {
		try (Connection conn = DButils.getDBconnection()) {
			HashMap<Integer, Person> personList = new HashMap<Integer, Person>();
			PreparedStatement preparedStmt = prepareSatementGetPerson(conn);

			if (preparedStmt.execute()) {
				ResultSet rs = preparedStmt.getResultSet();
				while (rs.next()) {
					int personId = rs.getInt("personId");
					String phoneNumber = rs.getString("phoneNumber");
					String firstName = rs.getString("firstName");
					String lastName = rs.getString("lastName");
					String address = rs.getString("address");
					String email = rs.getString("email");
					Date birthDate = rs.getTimestamp("birthDate");
					Person p = new Person(personId, phoneNumber, firstName, lastName, address, email, birthDate);
					personList.put(p.getId(), p);
				}
			}
			return personList;
		}
	}

	private static PreparedStatement prepareSatementGetPerson(Connection conn) throws SQLException {
		String query = "select * from " + TABLE_PERSON;
		PreparedStatement preparedStmt = conn.prepareStatement(query);
		return preparedStmt;
	}

	// Class
	public static int addClass(SpinnerClass sc) throws Exception {
		try (Connection conn = DButils.getDBconnection()) {
			int id = 0;
			PreparedStatement preparedStmt = prepareSatementAddClassName(sc, conn);
			preparedStmt.executeUpdate();
			ResultSet rs = preparedStmt.getGeneratedKeys();
			if (rs.next()) {
				id = rs.getInt(1);
			}
			return id;
		}
	}

	private static PreparedStatement prepareSatementAddClassName(SpinnerClass sc, Connection conn) throws SQLException {
		String query = " insert into " + TABLE_CLASS + " (className, openForRegistrationMode, lockForRegistration, hyperLink)" + " values (?,?,?,?)";
		PreparedStatement preparedStmt = conn.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
		prepareStatementAddClassName(sc, preparedStmt);
		return preparedStmt;
	}

	private static void prepareStatementAddClassName(SpinnerClass sc, PreparedStatement preparedStmt) throws SQLException {
		preparedStmt.setString(1, sc.getSpinnerClassName());
		preparedStmt.setString(2, sc.getOpenForRegistrationMode());
		preparedStmt.setInt(3, sc.getLockForRegistration());
		preparedStmt.setString(4, sc.getHyperLink());
	}

	public static void deleteSpinnerClass(int classId) throws Exception {
		try (Connection conn = DButils.getDBconnection()) {
			PreparedStatement prepareSatement = prepareSatementDeleteClassName(classId, conn);
			prepareSatement.execute();
		}

	}

	private static PreparedStatement prepareSatementDeleteClassName(int classId, Connection conn) throws SQLException {
		String query = " delete from " + TABLE_CLASS + " where classId=?";
		PreparedStatement preparedStmt = conn.prepareStatement(query);
		prepareSatementDeleteClassName(classId, preparedStmt);
		return preparedStmt;
	}

	private static void prepareSatementDeleteClassName(int classId, PreparedStatement preparedStmt) throws SQLException {
		preparedStmt.setInt(1, classId);
	}

	public static HashMap<Integer, SpinnerClass> getSpinnerClassListFromDB() throws Exception {
		try (Connection conn = DButils.getDBconnection()) {
			HashMap<Integer, SpinnerClass> spinnerClasses = new HashMap<Integer, SpinnerClass>();
			PreparedStatement preparedStmt = prepareSatementGetClasses(conn);

			if (preparedStmt.execute()) {
				ResultSet rs = preparedStmt.getResultSet();
				while (rs.next()) {
					SpinnerClass sc = generateSpinnerClass(rs);
					spinnerClasses.put(sc.getId(), sc);
				}
			}
			return spinnerClasses;
		}
	}

	private static PreparedStatement prepareSatementGetClasses(Connection conn) throws SQLException {
		String query = "select * from " + TABLE_CLASS;
		PreparedStatement preparedStmt = conn.prepareStatement(query);
		return preparedStmt;
	}

	private static SpinnerClass generateSpinnerClass(ResultSet rs) throws Exception {
		String className = rs.getString("className");
		int classId = rs.getInt("classId");
		String openForRegistrationMode = rs.getString("openForRegistrationMode");
		int lockForRegistration = rs.getInt("lockForRegistration");
		String hyperLink = rs.getString("hyperLink");
		SpinnerClass sc = new SpinnerClass(className, openForRegistrationMode, lockForRegistration, hyperLink);
		sc.setId(classId);
		return sc;
	}

	public static void assignPersonToClass(SpinnerClass sc, PersonSpinnerClass psc) throws Exception {
		try (Connection conn = DButils.getDBconnection()) {
			PreparedStatement preparedStmt = prepareSatementAssignPersonToClass(sc, psc, conn);
			preparedStmt.execute();
		}
	}

	private static PreparedStatement prepareSatementAssignPersonToClass(SpinnerClass sc, PersonSpinnerClass psc, Connection conn) throws Exception {
		String query = " insert into " + TABLE_PERSON_CLASS + " (classId, personId, type, numberOfValidRegistrations)" + " values (?,?,?,?)";
		PreparedStatement preparedStmt = conn.prepareStatement(query);
		prepareSatementAssignPersonToClass(sc, psc, preparedStmt);
		return preparedStmt;
	}

	private static void prepareSatementAssignPersonToClass(SpinnerClass sc, PersonSpinnerClass psc, PreparedStatement preparedStmt) throws Exception {
		preparedStmt.setInt(1, sc.getId());
		preparedStmt.setInt(2, psc.getPerson().getId());
		preparedStmt.setString(3, psc.getType());
		preparedStmt.setInt(4, psc.getNumberOfValidRegistrations());
	}

	public static void unAssignPersonFromClass(SpinnerClass sc, PersonSpinnerClass psc) throws Exception {
		try (Connection conn = DButils.getDBconnection()) {
			PreparedStatement preparedStmt = prepareSatementUnAssignPersonFromClass(sc, psc, conn);
			preparedStmt.execute();
		}
	}

	private static PreparedStatement prepareSatementUnAssignPersonFromClass(SpinnerClass sc, PersonSpinnerClass psc, Connection conn) throws Exception {
		String query = " delete from " + TABLE_PERSON_CLASS + " where classId=? and personId=? and type=?";
		PreparedStatement preparedStmt = conn.prepareStatement(query);
		prepareSatementUnAssignPersonFromClass(sc, psc, preparedStmt);
		return preparedStmt;
	}

	private static void prepareSatementUnAssignPersonFromClass(SpinnerClass sc, PersonSpinnerClass psc, PreparedStatement preparedStmt) throws Exception {
		preparedStmt.setInt(1, sc.getId());
		preparedStmt.setInt(2, psc.getPerson().getId());
		preparedStmt.setString(3, psc.getType());
	}

	public static HashMap<Integer, PersonSpinnerClass> getClassPersonsListFromDB(int classId, String type) throws Exception {
		try (Connection conn = DButils.getDBconnection()) {
			HashMap<Integer, PersonSpinnerClass> classPerons = new HashMap<Integer, PersonSpinnerClass>();
			PreparedStatement preparedStmt = prepareSatementGetClassPersonsList(conn, classId, type);

			if (preparedStmt.execute()) {
				ResultSet rs = preparedStmt.getResultSet();
				while (rs.next()) {
					int personId = rs.getInt("personId");
					int numberOfValidRegistrations = rs.getInt("numberOfValidRegistrations");
					PersonSpinnerClass psc = new PersonSpinnerClass(classId, personId, type, numberOfValidRegistrations);
					classPerons.put(psc.getPersonId(), psc);
				}
			}
			return classPerons;
		}
	}

	private static PreparedStatement prepareSatementGetClassPersonsList(Connection conn, int classId, String type) throws Exception {
		String query = "select * from " + TABLE_PERSON_CLASS + " where classId=? and type=?";
		PreparedStatement preparedStmt = conn.prepareStatement(query);
		prepareSatementGetClassPersonsList(classId, type, preparedStmt);
		return preparedStmt;
	}

	private static void prepareSatementGetClassPersonsList(int classId, String type, PreparedStatement preparedStmt) throws SQLException {
		preparedStmt.setInt(1, classId);
		preparedStmt.setString(2, type);
	}

	public static void updatePersonNumberOfValidRegistrations(int classId, int personId, int numberOfValidRegistrations) throws Exception {
		try (Connection conn = DButils.getDBconnection()) {
			PreparedStatement preparedStmt = prepareSatementUpdatePersonNumberOfValidRegistrations(classId, personId, numberOfValidRegistrations, conn);
			preparedStmt.execute();
		}
	}

	private static PreparedStatement prepareSatementUpdatePersonNumberOfValidRegistrations(int classId, int personId, int numberOfValidRegistrations, Connection conn) throws SQLException {
		String query = "update " + TABLE_PERSON_CLASS + " set numberOfValidRegistrations=? where classId=? and personId=?";
		PreparedStatement preparedStmt = conn.prepareStatement(query);
		prepareSatementUpdatePersonNumberOfValidRegistrations(classId, personId, numberOfValidRegistrations, preparedStmt);
		return preparedStmt;
	}

	private static void prepareSatementUpdatePersonNumberOfValidRegistrations(int classId, int personId, int numberOfValidRegistrations, PreparedStatement preparedStmt) throws SQLException {
		preparedStmt.setInt(1, numberOfValidRegistrations);
		preparedStmt.setInt(2, classId);
		preparedStmt.setInt(3, personId);
	}

	// Registeration
	public static void updateRegistration(int classId, int eventId, int personId, String status) throws Exception {
		try (Connection conn = DButils.getDBconnection()) {
			PreparedStatement preparedStmt = prepareSatementRegisterStudentToSpinnerEvent(classId, eventId, personId, status, conn);
			preparedStmt.execute();
		}
	}

	private static PreparedStatement prepareSatementRegisterStudentToSpinnerEvent(int classId, int eventId, int personId, String status, Connection conn) throws SQLException {
		String query = " insert into " + TABLE_REGISTRATION + " (classId, eventId, personId, status)" + "values (?,?,?,?)";
		PreparedStatement preparedStmt = conn.prepareStatement(query);
		prepareSatementRegisterStudentToSpinnerEvent(classId, eventId, personId, status, preparedStmt);
		return preparedStmt;
	}

	private static void prepareSatementRegisterStudentToSpinnerEvent(int classId, int eventId, int personId, String status, PreparedStatement preparedStmt) throws SQLException {
		preparedStmt.setInt(1, classId);
		preparedStmt.setInt(2, eventId);
		preparedStmt.setInt(3, personId);
		preparedStmt.setString(4, status);
	}

	public static void deleteStudentRegisterationFromClassEvents(int classId, int personId) throws Exception {
		try (Connection conn = DButils.getDBconnection()) {
			PreparedStatement preparedStmt = deleteStudentRegisterationFromClassEvents(classId, personId, conn);
			preparedStmt.execute();
		}
	}

	private static PreparedStatement deleteStudentRegisterationFromClassEvents(int classId, int personId, Connection conn) throws SQLException {
		String query = " delete from " + TABLE_REGISTRATION + " where classId=? and personId=?";
		PreparedStatement preparedStmt = conn.prepareStatement(query);
		deleteStudentRegisterationFromClassEvents(classId, personId, preparedStmt);
		return preparedStmt;
	}

	private static void deleteStudentRegisterationFromClassEvents(int classId, int personId, PreparedStatement preparedStmt) throws SQLException {
		preparedStmt.setInt(1, classId);
		preparedStmt.setInt(2, personId);
	}

	public static void deleteStudentRegisterationFromClassEvent(int classId, int personId, int eventId) throws Exception {
		try (Connection conn = DButils.getDBconnection()) {
			PreparedStatement preparedStmt = deleteStudentRegisterationFromClassEvent(classId, personId, eventId, conn);
			preparedStmt.execute();
		}
	}

	private static PreparedStatement deleteStudentRegisterationFromClassEvent(int classId, int personId, int eventId, Connection conn) throws SQLException {
		String query = " delete from " + TABLE_REGISTRATION + " where classId=? and personId=? and eventId=?";
		PreparedStatement preparedStmt = conn.prepareStatement(query);
		deleteStudentRegisterationFromClassEvent(classId, personId, eventId, preparedStmt);
		return preparedStmt;
	}

	private static void deleteStudentRegisterationFromClassEvent(int classId, int personId, int eventId, PreparedStatement preparedStmt) throws SQLException {
		preparedStmt.setInt(1, classId);
		preparedStmt.setInt(2, personId);
		preparedStmt.setInt(3, eventId);
	}

	public static void initEventRegistrations(int eventId, List<Integer> registered, List<Integer> standBy, List<Integer> unregistered) throws Exception {
		List<Integer> allRegisterationsPersonIds = getPersonIdRegistrations(eventId);
		Iterator<Integer> allRegisterationsPersonIdsIterator = allRegisterationsPersonIds.iterator();
		while (allRegisterationsPersonIdsIterator.hasNext()) {
			int personId = allRegisterationsPersonIdsIterator.next();
			String status = getLastUpdateRegistrationStatus(eventId, personId);
			if (status.equals(Status.REGISTERED)) {
				registered.add(personId);
			}
			if (status.equals(Status.STANDBY)) {
				standBy.add(personId);
			}
			if (status.equals(Status.UNREGISTERED)) {
				unregistered.add(personId);
			}
		}
	}

	public static void initPersonRegistrations(int personId, List<Integer> registered, List<Integer> standBy) throws Exception {
		List<Integer> allRegisterationsEventIds = getEventIdRegistrations(personId);
		Iterator<Integer> allRegisterationsEventIdsIterator = allRegisterationsEventIds.iterator();
		while (allRegisterationsEventIdsIterator.hasNext()) {
			int eventId = allRegisterationsEventIdsIterator.next();
			String status = getLastUpdateRegistrationStatus(eventId, personId);
			if (status.equals(Status.REGISTERED)) {
				registered.add(eventId);
			}
			if (status.equals(Status.STANDBY)) {
				standBy.add(eventId);
			}
		}
	}

	private static String getLastUpdateRegistrationStatus(int eventId, int personId) throws Exception {
		try (Connection conn = DButils.getDBconnection()) {
			String status = Status.NOT_REGISTERED;
			PreparedStatement preparedStmt = prepareSatementGetLastUpdateRegistrationStatus(eventId, personId, conn);
			if (preparedStmt.execute()) {
				ResultSet rs = preparedStmt.getResultSet();
				while (rs.next()) {
					status = rs.getString("status");
				}
			}
			return status;
		}
	}

	private static PreparedStatement prepareSatementGetLastUpdateRegistrationStatus(int eventId, int personId, Connection conn) throws SQLException {
		String query = "select * FROM " + TABLE_REGISTRATION + " where eventId=? and personId=? ORDER BY lastUpdated DESC LIMIT 1";
		PreparedStatement preparedStmt = conn.prepareStatement(query);
		prepareSatementGetLastUpdateRegistrationStatus(eventId, personId, preparedStmt);
		return preparedStmt;
	}

	private static void prepareSatementGetLastUpdateRegistrationStatus(int eventId, int personId, PreparedStatement preparedStmt) throws SQLException {
		preparedStmt.setInt(1, eventId);
		preparedStmt.setInt(2, personId);
	}

	private static List<Integer> getPersonIdRegistrations(int eventId) throws Exception {
		try (Connection conn = DButils.getDBconnection()) {
			ArrayList<Integer> persons = new ArrayList<Integer>();
			PreparedStatement preparedStmt = prepareSatementGetPersonIdRegistrations(eventId, conn);

			if (preparedStmt.execute()) {
				ResultSet rs = preparedStmt.getResultSet();
				while (rs.next()) {
					int personId = rs.getInt("personId");
					persons.add(personId);
				}
			}
			return persons;
		}
	}

	private static PreparedStatement prepareSatementGetPersonIdRegistrations(int eventId, Connection conn) throws SQLException {
		String query = "select DISTINCT personId FROM " + TABLE_REGISTRATION + " where eventId=?";
		PreparedStatement preparedStmt = conn.prepareStatement(query);
		prepareSatementGetPersonIdRegistrations(eventId, preparedStmt);
		return preparedStmt;
	}

	private static void prepareSatementGetPersonIdRegistrations(int eventId, PreparedStatement preparedStmt) throws SQLException {
		preparedStmt.setInt(1, eventId);
	}

	private static List<Integer> getEventIdRegistrations(int personId) throws Exception {
		try (Connection conn = DButils.getDBconnection()) {
			ArrayList<Integer> events = new ArrayList<Integer>();

			PreparedStatement preparedStmt = prepareSatementGetEventIdRegistrations(personId, conn);

			if (preparedStmt.execute()) {
				ResultSet rs = preparedStmt.getResultSet();
				while (rs.next()) {
					int eventId = rs.getInt("eventId");
					events.add(eventId);
				}
			}
			return events;
		}
	}

	private static PreparedStatement prepareSatementGetEventIdRegistrations(int personId, Connection conn) throws SQLException {
		String query = "select DISTINCT eventId FROM " + TABLE_REGISTRATION + " where personId=?";
		PreparedStatement preparedStmt = conn.prepareStatement(query);
		prepareSatementGetEventIdRegistrations(personId, preparedStmt);
		return preparedStmt;
	}

	private static void prepareSatementGetEventIdRegistrations(int personId, PreparedStatement preparedStmt) throws SQLException {
		preparedStmt.setInt(1, personId);
	}

	// =====================Private methods
	private static Timestamp getSqlDate(Date date) {
		Timestamp sqlFromDate = null;
		if (date != null) {
			sqlFromDate = new Timestamp(date.getTime());
		}
		return sqlFromDate;
	}

	public static Date getUnRegisterTime(int personId, int eventId) throws Exception {
		try (Connection conn = DButils.getDBconnection()) {
			Date lastUnRegisterTime = null;
			PreparedStatement preparedStmt = prepareSatementGetUnRegisterTime(personId, eventId, conn);
			if (preparedStmt.execute()) {
				ResultSet rs = preparedStmt.getResultSet();
				while (rs.next()) {
					lastUnRegisterTime = rs.getTimestamp(1);
				}
			}
			return lastUnRegisterTime;
		}
	}

	private static PreparedStatement prepareSatementGetUnRegisterTime(int personId, int eventId, Connection conn) throws Exception {
		String query = "select max(lastUpdated) from registration where personId=? and eventId=? and  status='UNREGISTERED';";
		PreparedStatement preparedStmt = conn.prepareStatement(query);
		prepareSatementGetUnRegisterTime(personId, eventId, preparedStmt);
		return preparedStmt;
	}

	private static void prepareSatementGetUnRegisterTime(int personId, int eventId, PreparedStatement preparedStmt) throws SQLException {
		preparedStmt.setInt(1, personId);
		preparedStmt.setInt(2, eventId);

	}

}
