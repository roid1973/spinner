package inputRequest;

import java.util.Date;

import utils.DateUtils;

public class PersonDetailsClassInput {
	private int classId;
	private Date fromDate;
	private Date toDate;
	
	public int getClassId() {
		return classId;
	}
	public void setClassId(int classId) {
		this.classId = classId;
	}
	public Date getFromDate() {
		return fromDate;
	}
	public void setFromDate(String fromDate) {
		this.fromDate = DateUtils.stringToStudentBirthDate(fromDate);
	}
	public Date getToDate() {
		return toDate;
	}
	public void setToDate(String toDate) {
		this.toDate = DateUtils.stringToStudentBirthDate(toDate);
	}
	

}
