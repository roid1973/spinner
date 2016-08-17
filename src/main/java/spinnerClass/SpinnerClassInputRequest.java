package spinnerClass;

public class SpinnerClassInputRequest {
	private String className;
	private String openForRegistrationMode;
	private int lockForRegistration;
	private String hyperLink;

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getOpenForRegistrationMode() {
		return openForRegistrationMode;
	}

	public void setOpenForRegistrationMode(String openForRegistrationMode) {
		this.openForRegistrationMode = openForRegistrationMode;
	}

	public int getLockForRegistration() {
		return lockForRegistration;
	}

	public void setLockForRegistration(int lockForRegistration) {
		this.lockForRegistration = lockForRegistration;
	}

	public String getHyperLink() {
		return hyperLink;
	}

	public void setHyperLink(String hyperLink) {
		this.hyperLink = hyperLink;
	}

}
