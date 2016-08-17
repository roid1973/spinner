package inputRequest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import utils.DateUtils;
import utils.SpinnerConstants;

public class SpinnerEventInputRequest {
	private int classId;
	private String eventName;
	private String fromDate; // dd.MM.yyyy HH:mm:ss
	private String toDateString; // dd.MM.yyyy HH:mm:ss
	private String timeZone;
	//private int lockTime;
	//private String openDate;
	private int maxCapacity;
	private int instructorId;
	private String address;
	private String comments;
	private String status;
	private int interval;
	private int numberOfOccurrences = 0;
	private String duration;

	public int getClassId() {
		return classId;
	}

	public void setClassId(int classId) {
		this.classId = classId;
	}

	public String getEventName() {
		return eventName;
	}

	public void setEventName(String eventName) {
		this.eventName = eventName;
	}

	public Date getFromDate() throws ParseException {
		return DateUtils.stringToSpinnerEventDate(fromDate, timeZone);
	}

	public void setFromDate(String fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() throws ParseException{
		Date toDate = new Date();
		if(toDateString == null || toDateString.equals("")){			
			toDate = DateUtils.addDuration(getFromDate(), duration);
		}else{
			toDate = DateUtils.stringToSpinnerEventDate(toDateString, timeZone);
		}
		return toDate;
	}

	public void setToDate(String toDate) {
		this.toDateString = toDate;
	}

	public int getMaxCapacity() {
		return maxCapacity;
	}

	public void setMaxCapacity(int maxCapacity) {
		this.maxCapacity = maxCapacity;
	}

	public int getInstructorId() {
		return instructorId;
	}

	public void setInstructorId(int instructorId) {
		this.instructorId = instructorId;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}
/*
	public int getLockTime() {
		return lockTime;
	}

	public void setLockTime(int lockTime) {
		this.lockTime = lockTime;
	}

	public Date getOpenDate() throws ParseException {
		Date openD = null;
		if (openDate != null && openDate.compareTo("") != 0) {
			openD = DateUtils.stringToSpinnerEventDate(openDate, timeZone);
		}
		return openD;
	}

	public void setOpenDate(String openDate) {
		this.openDate = openDate;
	}
*/
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getInterval() {
		return interval;
	}

	public int setInterval(String intervalString) {
		interval = SpinnerConstants.getIntervalInt(intervalString);
		return interval;
	}

	public int getNumberOfOccurrences() {
		return numberOfOccurrences;
	}

	public void setNumberOfOccurrences(int numberOfOccurrences) {
		this.numberOfOccurrences = numberOfOccurrences;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}
}
