package charcters;

import java.util.Date;

public class Instructor extends Person {

	//public static final String type = "INSTRUCTOR";

	public Instructor(String pn, String fn) throws Exception {
		super(pn, fn);		
	}

	public Instructor(String pn, String fn, String ln, String add, String e, Date bd) throws Exception {
		super(pn, fn, ln, add, e, bd);		
	}
	
	public Instructor(int id, String pn, String fn) throws Exception {
		super(pn, fn);		
	}

	public Instructor(int id, String pn, String fn, String ln, String add, String e, Date bd) throws Exception {
		super(id, pn, fn, ln, add, e, bd);		
	}


}
