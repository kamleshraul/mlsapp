package org.mkcl.els.common.vo;

public class ActSearchVO{
	/** The id. */
	private Long id;

	/** The number. */
	private String number;

	/** The bill title. */
	private String title;
	
	/** * The Year ***. */
	private String year;
	
	/** The act pdf in english. */	
	private String fileEnglish;   
	
	/** The act pdf in marathi. */	
	private String fileMarathi;
	
	/** The act pdf in hindi. */	
	private String fileHindi;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getFileEnglish() {
		return fileEnglish;
	}

	public void setFileEnglish(String fileEnglish) {
		this.fileEnglish = fileEnglish;
	}

	public String getFileMarathi() {
		return fileMarathi;
	}

	public void setFileMarathi(String fileMarathi) {
		this.fileMarathi = fileMarathi;
	}

	public String getFileHindi() {
		return fileHindi;
	}

	public void setFileHindi(String fileHindi) {
		this.fileHindi = fileHindi;
	}
	
}
