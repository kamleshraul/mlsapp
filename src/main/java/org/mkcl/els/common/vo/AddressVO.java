package org.mkcl.els.common.vo;

public class AddressVO {
	
	// ---------------------------------Attributes----------------------------------
	private String details;
	
	private String city;

    private String pincode;
    
    private String tehsil;
    
    private String district;
    
    private String state;

    private String briefAddress; // Address in brief comprising of all the provided fields 

    // ---------------------------------Constructors--------------------------------
	public AddressVO() {
		super();
	}
	
	// ---------------------------------Getters/Setters------------------------------
	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public String getBriefAddress() {
		return briefAddress;
	}

	public void setBriefAddress(String briefAddress) {
		this.briefAddress = briefAddress;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getPincode() {
		return pincode;
	}

	public void setPincode(String pincode) {
		this.pincode = pincode;
	}

	public String getTehsil() {
		return tehsil;
	}

	public void setTehsil(String tehsil) {
		this.tehsil = tehsil;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}
	
}