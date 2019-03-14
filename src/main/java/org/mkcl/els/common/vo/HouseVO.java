package org.mkcl.els.common.vo;

public class HouseVO {
	
	// ---------------------------------Attributes----------------------------------
	/** The id. */
	private Long id;
	
	/** The displayName. */
	private String displayName;

	// ---------------------------------Constructors--------------------------------
	public HouseVO() {
		super();
	}
	
	public HouseVO(Long id, String displayName) {
		super();
		this.id = id;
		this.displayName = displayName;
	}	
	
	// ---------------------------------Getters/Setters------------------------------
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
}