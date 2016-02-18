package charcters;


import java.util.Date;
import java.util.List;

import spinnerCalendar.SpinnerEvent;
import spinnerCalendar.Status;
import db.spinner.DBspinner;

public class PersonSpinnerClass {
	private int classId;
	private int personId;
	private String type = null;
	private int numberOfValidRegistrations = -999;

	public PersonSpinnerClass(int classId, int personId, String t, int numberOfValidRegistrations) {
		this.classId = classId;
		this.personId = personId;
		type = t;
		this.numberOfValidRegistrations = numberOfValidRegistrations;
	}

	public String getType() {
		return type;
	}

	public int getPersonId() {
		return personId;
	}

	public int getNumberOfValidRegistrations() {
		return numberOfValidRegistrations;
	}

	public Person getPerson() throws Exception {
		return Persons.getPersonsInstance().getPerson(personId);
	}

	public void deleteStudentRegisterationFromClassEvents() throws Exception {
		DBspinner.deleteStudentRegisterationFromClassEvents(classId, personId);
	}

	public void initPersonRegisteration(List<Integer> registered, List<Integer> standBy) throws Exception {
		registered.clear();
		standBy.clear();
		DBspinner.initPersonRegistrations(personId, registered, standBy);
	}

	public void registerCredit(SpinnerEvent event) throws Exception {		
		if (event.getStatus().equals(Status.EVENT_OPEN)) {
			numberOfValidRegistrations = numberOfValidRegistrations - 1;
		} else if(event.getStatus().equals(Status.EVENT_LOCKED_FOR_UNREGISTRATION)) {
			boolean noNeedToUpdateCredit = checkIfUnRegisteredAfterLockTime(event);
			if(!noNeedToUpdateCredit){
				numberOfValidRegistrations = numberOfValidRegistrations - 1;
			}
		}		
		DBspinner.updatePersonNumberOfValidRegistrations(classId, personId, numberOfValidRegistrations);
	}

	private boolean checkIfUnRegisteredAfterLockTime(SpinnerEvent event) throws Exception {
		boolean unRegisterAfterLockTime = false;
		Date unregisterTime = DBspinner.getUnRegisterTime(personId, event.getEventId());
		if(unregisterTime!=null && unregisterTime.after(event.getLockDate())){
			unRegisterAfterLockTime = true;
		}
		return unRegisterAfterLockTime;
	}

	public void unRegisterCredit(SpinnerEvent event) throws Exception {
		if (event.getStatus().equals(Status.EVENT_OPEN)) {
			numberOfValidRegistrations = numberOfValidRegistrations + 1;
			DBspinner.updatePersonNumberOfValidRegistrations(classId, personId, numberOfValidRegistrations);
		}
	}

	public int addCredit(int credit) throws Exception {
		if (numberOfValidRegistrations <= -999) {
			numberOfValidRegistrations = 0;
		}
		numberOfValidRegistrations = numberOfValidRegistrations + credit;
		DBspinner.updatePersonNumberOfValidRegistrations(classId, personId, numberOfValidRegistrations);
		return numberOfValidRegistrations;
	}

	public boolean validCredit() {
		boolean valid = false;
		if (numberOfValidRegistrations <= -999 || numberOfValidRegistrations > 0) {
			valid = true;
		}
		return valid;
	}

	public int updateCredit(int credit) throws Exception {
		numberOfValidRegistrations = credit;
		DBspinner.updatePersonNumberOfValidRegistrations(classId, personId, numberOfValidRegistrations);
		return numberOfValidRegistrations;
	}

}
