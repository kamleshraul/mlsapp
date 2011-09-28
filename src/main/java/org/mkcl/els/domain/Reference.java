package org.mkcl.els.domain;

public class Reference {
	
	public String id;	
	
	public String name;
	
	public Reference() {
		super();
	}

	public Reference(String id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	

	
}
