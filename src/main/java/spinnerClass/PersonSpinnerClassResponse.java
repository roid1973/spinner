package spinnerClass;

public class PersonSpinnerClassResponse {
	private int personId;
	private String className = null;
	private int classId;
	private String classHyperLink = null;
	private boolean admin = false;
	private boolean instructor = false;
	private boolean student = false;
	
	public PersonSpinnerClassResponse(int personId, SpinnerClass sc) {
		this.personId = personId;
		className = sc.getSpinnerClassName();
		classId = sc.getId();
		classHyperLink = sc.getHyperLink();
		admin = sc.isAdmin(personId);
		instructor = sc.isInstructor(personId);
		student = sc.isStudent(personId);		
	}
	
	public int getPersonId() {
		return personId;
	}

	public String getClassName() {
		return className;
	}

	public int getClassId() {
		return classId;
	}

	public String getClassHyperLink() {
		return classHyperLink;
	}

	public boolean isAdmin() {
		return admin;
	}

	public boolean isInstructor() {
		return instructor;
	}

	public boolean isStudent() {
		return student;
	}

	
}
