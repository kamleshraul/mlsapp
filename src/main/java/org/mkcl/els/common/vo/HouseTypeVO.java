package org.mkcl.els.common.vo;

public class HouseTypeVO {
	
	// ---------------------------------Attributes----------------------------------
	/** The type. */
	private String type;
	
	/** The name. */
	private String name;

	// ---------------------------------Constructors--------------------------------
	public HouseTypeVO() {
		super();
	}
	
	public HouseTypeVO(String type, String name) {
		super();
		this.type = type;
		this.name = name;
	}	
	
	// ---------------------------------Getters/Setters------------------------------
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}