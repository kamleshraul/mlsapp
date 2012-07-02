package org.mkcl.els.common.vo;

import java.util.List;

public class ConstituencyCompleteVO {
private String division;
private List<MasterVO> districts;
public String getDivision() {
	return division;
}
public void setDivision(String division) {
	this.division = division;
}
public List<MasterVO> getDistricts() {
	return districts;
}
public void setDistricts(List<MasterVO> districts) {
	this.districts = districts;
}

}
