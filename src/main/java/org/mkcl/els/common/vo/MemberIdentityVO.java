package org.mkcl.els.common.vo;

public class MemberIdentityVO {
	
	// ---------------------------------Attributes----------------------------------
	/** The title. */
	private String title;
	
	/** The first name. */
	private String firstName;
	
	/** The middle name. */
	private String middleName;
	
	/** The last name. */
	private String lastName;
	
	/** The full display name. */
	private String fullDisplayName;
	
	/** The constituency name. */
	private String constituencyName;
	
	/** The username. */
	private String username;

	// ---------------------------------Constructors--------------------------------
	public MemberIdentityVO() {
		super();
	}

	public MemberIdentityVO(String title, String firstName, String middleName,
			String lastName, String fullDisplayName, String constituencyName, String username) {
		super();
		this.title = title;
		this.firstName = firstName;
		this.middleName = middleName;
		this.lastName = lastName;
		this.fullDisplayName = fullDisplayName;
		this.constituencyName = constituencyName;
		this.username = username;
	}

	// ---------------------------------Getters/Setters------------------------------
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getMiddleName() {
		return middleName;
	}

	public void setMiddleName(String middleName) {
		this.middleName = middleName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getFullDisplayName() {
		return fullDisplayName;
	}

	public void setFullDisplayName(String fullDisplayName) {
		this.fullDisplayName = fullDisplayName;
	}

	public String getConstituencyName() {
		return constituencyName;
	}

	public void setConstituencyName(String constituencyName) {
		this.constituencyName = constituencyName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
}