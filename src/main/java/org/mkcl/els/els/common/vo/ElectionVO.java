package org.mkcl.els.common.vo;

public class ElectionVO {
private Long id;
private String name;
public ElectionVO(Long id, String name) {
	super();
	this.id = id;
	this.name = name;
}
public Long getId() {
	return id;
}
public void setId(Long id) {
	this.id = id;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}

}
