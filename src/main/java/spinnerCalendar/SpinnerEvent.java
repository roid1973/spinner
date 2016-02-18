package spinnerCalendar;

import java.util.*;
import charcters.PersonSpinnerClass;
import charcters.Persons;
import db.spinner.DBspinner;

public class SpinnerEvent {
	private int eventId;
	private int classId;
	private String eventName = null;
	private Date fromDate = null;
	private Date toDate = null;
	private Date lockDate = null;
	private Date openDate = null;
	private int maxCapacity;
	private boolean isEventFull;
	private List<Integer> registered = null;
	private List<Integer> standby = null;
	private int instructorId;
	private String address = null;
	private String comments = null;
	private String status = null;

	public SpinnerEvent(int classId, int eventId, String seName, Date seFrom, Date seTo, int lockTime, Date seOpenDate, int seMaxCapacity, int seInstructorId, String address, String comments, String sts) throws Exception {
		validateInputs(seName, seFrom, seTo, seMaxCapacity);
		this.classId = classId;
		this.eventId = eventId;
		eventName = seName;
		fromDate = seFrom;
		toDate = seTo;
		lockDate = new Date(fromDate.getTime() - (lockTime * 60 * 1000));
		openDate = seOpenDate;
		maxCapacity = seMaxCapacity;
		setIsEventFull();
		if (seInstructorId == 0) {
			seInstructorId = -1;
		}
		instructorId = seInstructorId;
		setAddress(address);
		setComments(comments);
		status = sts;
		calculateStatus();
	}

	public SpinnerEvent(int classId, String seName, Date seFrom, Date seTo, int lockTime, Date seOpenDate, int seMaxCapacity, int seInstructorId, String address, String comments, String sts) throws Exception {
		validateInputs(seName, seFrom, seTo, seMaxCapacity);
		this.classId = classId;
		eventName = seName;
		fromDate = seFrom;
		toDate = seTo;
		lockDate = new Date(fromDate.getTime() - (lockTime * 60 * 1000));
		openDate = seOpenDate;
		maxCapacity = seMaxCapacity;
		setIsEventFull();
		if (seInstructorId == 0) {
			seInstructorId = -1;
		}
		instructorId = seInstructorId;
		setAddress(address);
		setComments(comments);
		status = sts;
		calculateStatus();
	}

	public void updateEventDetails(SpinnerEvent newEvent) throws Exception {
		eventName = newEvent.getName();
		fromDate = newEvent.getFromDate();
		toDate = newEvent.getToDate();
		lockDate = newEvent.getLockDate();
		openDate = newEvent.getOpenDate();
		maxCapacity = newEvent.getMaxCapacity();
		instructorId = newEvent.getInstructorId();
		address = newEvent.getAddress();
		comments = newEvent.getComments();
		status = newEvent.getStatus();
		// calculateStatus();
		DBspinner.updateSpinnerEvent(this);
	}

	// Setters&Getters

	public String getStatus() throws Exception {
		calculateStatus();
		return status;
	}

	private void setStatus(String sts) throws Exception {
		status = sts;
		DBspinner.updateSpinnerEvent(this);
	}

	public int getEventId() {
		return eventId;
	}

	public void setEventId(int id) {
		eventId = id;
	}

	public int getClassId() {
		return classId;
	}

	public int getNumberOfRegistered() throws Exception {
		return (getRegistered()).size();
	}

	public int getNumberOfStandBy() throws Exception {
		return getStandby().size();
	}

	public String getName() {
		return eventName;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public Date getLockDate() {
		return lockDate;
	}

	public Date getOpenDate() {
		return openDate;
	}

	public int getMaxCapacity() {
		return maxCapacity;
	}

	public boolean getIsEventFull() throws Exception {
		setIsEventFull();
		return isEventFull;
	}

	public int getInstructorId() {
		return instructorId;
	}

	public String getInstructorFirstName() throws Exception {
		return Persons.getPersonsInstance().getPerson(instructorId).getFirstName();
	}

	public String getInstructorLastName() throws Exception {
		return Persons.getPersonsInstance().getPerson(instructorId).getLastName();
	}

	public void updateInstructor(int newInstructorId) throws Exception {
		instructorId = newInstructorId;
		DBspinner.updateInstructorEvent(instructorId, eventId);
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public List<Integer> getRegistered() throws Exception {
		if (registered == null) {
			initRegistration();
		}
		return registered;
	}

	public List<Integer> getStandby() throws Exception {
		if (standby == null) {
			initRegistration();
		}
		return standby;
	}

	// Public Methods

	public String registerToSpinnerEvent(PersonSpinnerClass s) throws Exception {
		initRegistration();
		String sts = Status.FAILURE;
		if (getStatus().equals(Status.EVENT_OPEN) || getStatus().equals(Status.EVENT_LOCKED_FOR_UNREGISTRATION)) {
			if (registered.contains(s.getPerson().getId())) {
				sts = Status.REGISTERED;
			} else {
				synchronized (this) {
					if (isEventFull) {
						sts = registerToStandByList(s);
					} else {
						sts = registerToRegisteredList(s);
					}
				}
			}
		}
		return sts;
	}

	public String unRegisterFromSpinnerEvent(PersonSpinnerClass s) throws Exception {
		initRegistration();
		String sts = Status.FAILURE;
		if (getStatus().equals(Status.EVENT_OPEN) || getStatus().equals(Status.EVENT_LOCKED_FOR_UNREGISTRATION)) {
			boolean notifyStanby = unregister(s);
			sts = Status.NOT_REGISTERED;
			if (notifyStanby) {
				notifyStanby();
			}
		}
		return sts;
	}

	// Private Methods

	private void calculateStatus() throws Exception {
		String sts = Status.EVENT_OPEN;
		Date current = new Date();
		if (fromDate.before(current)) { // check if event is history
			// sts = Status.EVENT_HISTORY;
			sts = Status.EVENT_LOCKED;
		} else if (lockDate.before(current)) { // check if the event is locked for un-registration
			sts = Status.EVENT_LOCKED_FOR_UNREGISTRATION;
		} else if (openDate.after(current)) {
			sts = Status.EVENT_LOCKED;
		}

		if (!sts.equals(status)) {
			setStatus(sts);
		}
	}

	private void setIsEventFull() throws Exception {
		synchronized (getRegistered()) {
			if (registered.size() >= maxCapacity) {
				isEventFull = true;
			} else {
				isEventFull = false;
			}
		}
	}

	private String registerToRegisteredList(PersonSpinnerClass s) throws Exception {
		String sts;
		unregister(s);
		if (s.validCredit()) {
			register(s);
			s.registerCredit(this);
			setIsEventFull();
			sts = Status.REGISTERED;
		} else {
			sts = Status.NO_VALID_CREDIT;
		}
		return sts;
	}

	private void initRegistration() throws Exception {
		if (registered == null && standby == null) {
			registered = new ArrayList<Integer>();
			standby = new ArrayList<Integer>();
			DBspinner.initEventRegistrations(eventId, registered, standby);
		}
	}

	private void validateInputs(String e_name, Date e_from, Date e_to, int e_max_capacity) {

	}

	private void register(PersonSpinnerClass s) throws Exception {
		DBspinner.updateRegistration(classId, eventId, s.getPerson().getId(), Status.REGISTERED);
		registered.add(s.getPerson().getId());
	}

	private boolean unregister(PersonSpinnerClass s) throws Exception {
		boolean notifyStanby = false;
		if (registered.contains(s.getPerson().getId())) {
			synchronized (this) {
				if (getIsEventFull()) {
					notifyStanby = true;
				}
				DBspinner.updateRegistration(classId, eventId, s.getPerson().getId(), Status.UNREGISTERED);
				registered.remove((Integer) s.getPerson().getId());
				s.unRegisterCredit(this);
				setIsEventFull();
			}
		}
		if (standby.contains(s.getPerson().getId())) {
			DBspinner.updateRegistration(classId, eventId, s.getPerson().getId(), Status.CANCEL_STANDBY);
			standby.remove((Integer) s.getPerson().getId());
		}
		return notifyStanby;
	}

	private String registerToStandByList(PersonSpinnerClass s) throws Exception {
		String sts = Status.FAILURE;
		if (!standby.contains(s.getPerson().getId())) {
			sts = Status.STANDBY;
			DBspinner.updateRegistration(classId, eventId, s.getPerson().getId(), Status.STANDBY);
			standby.add(s.getPerson().getId());
		} else {
			sts = Status.STANDBY;
		}
		return sts;
	}

	private void notifyStanby() throws Exception {
		if (!standby.isEmpty()) {
			spinner.notifications.Notify.notifyEventAvailablePlaces(this);
		}

	}

}
