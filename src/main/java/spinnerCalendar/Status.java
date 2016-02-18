package spinnerCalendar;

public class Status {
	public static final String REGISTERED = "REGISTERED";
	public static final String STANDBY = "STANDBY";
	public static final String UNREGISTERED = "UNREGISTERED";
	public static final String NOT_REGISTERED = "NOT REGISTERED";
	public static final String CANCEL_STANDBY = "CANCEL_STANDBY";
	public static final String FAILURE = "FAILURE";
	public static final String ASSIGNED = "ASSIGNED";
	public static final String UNASSIGNED = "UNASSIGNED";
	public static final String CLASS_DELETED = "CLASS_DELETED";
	public static final String CLASS_NOT_EXIST = "CLASS_NOT_EXIST";
	public static final String EVENT_LOCKED_FOR_UNREGISTRATION = "EVENT_LOCKED_FOR_UNREGISTRATION";	
	public static final String EVENT_OPEN = "EVENT_OPEN";
	public static final String EVENT_LOCKED = "EVENT_LOCKED";
	public static final String EVENT_HISTORY = "EVENT_HISTORY";
	public static final String NO_VALID_CREDIT = "NO_VALID_CREDIT";
	
	private String status;
	
	public Status(String sts){
		status = sts;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	

}
