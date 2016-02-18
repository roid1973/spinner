package charcters;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import db.spinner.DBspinner;
import spinnerCalendar.SpinnerEvent;
//import com.fasterxml.jackson.annotation;
import spinnerClass.SpinnerClass;

//@JsonIgnoreProperties(ignoreUnknown = true)
public class Student extends Person {

	//public static final String type = "STUDENT";
	
	public Student(){
		//dummy constructor for restservice
		super();
	}
/*
	protected Student(String pn, String fn) throws Exception {
		super(pn, fn);
	}

	public Student(String pn, String fn, String ln, String add, String e, Date bd) throws Exception {
		super(pn, fn, ln, add, e, bd);
	}
	
	protected Student(int id, String pn, String fn) throws Exception {
		super(id, pn, fn);
	}

	public Student(int id, String pn, String fn, String ln, String add, String e, Date bd) throws Exception {
		super(id, pn, fn, ln, add, e, bd);
	}*/
/*
	public List<String> getStudentSpinnerEventsRegisterdIds() throws Exception {
		List<String> registered = DBspinner.getStudentSpinnerEventsRegisterations("registered", this);
		return registered;
	}

	public List<String> getStudentSpinnerEventsStandbyIds() throws Exception {
		List<String> registered = DBspinner.getStudentSpinnerEventsRegisterations("standby", this);
		return registered;
	}*/

}
