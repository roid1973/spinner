package spinnerCalendar;

public class StudentSpinnerEvent {

	private Status studentRegisterationStatus;
	private SpinnerEvent spinnerEvent;
	private int numberOfValidRegistrations;
	private int credit;

	public StudentSpinnerEvent(SpinnerEvent se, String sts, int credit) {
		spinnerEvent = se;
		studentRegisterationStatus = new Status(sts);
		this.numberOfValidRegistrations = credit;
		this.credit = credit;
	}

	public Status getStudentRegisterationStatus() {
		return studentRegisterationStatus;
	}

	public SpinnerEvent getSpinnerEvent() {
		return spinnerEvent;
	}

	public int getNumberOfValidRegistrations() {
		return numberOfValidRegistrations;
	}
	
	public int getCredit() {
		return credit;
	}
}
