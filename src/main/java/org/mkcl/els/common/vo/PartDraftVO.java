package org.mkcl.els.common.vo;

public class PartDraftVO {
	
	private Long id;

    /** The name. */
    private String content;
    
    private String mainHeading;
    
    private String pageHeading;

	public PartDraftVO() {
		super();
		// TODO Auto-generated constructor stub
	}

	public PartDraftVO(Long id, String content, String mainHeading,
			String pageHeading) {
		super();
		this.id = id;
		this.content = content;
		this.mainHeading = mainHeading;
		this.pageHeading = pageHeading;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getMainHeading() {
		return mainHeading;
	}

	public void setMainHeading(String mainHeading) {
		this.mainHeading = mainHeading;
	}

	public String getPageHeading() {
		return pageHeading;
	}

	public void setPageHeading(String pageHeading) {
		this.pageHeading = pageHeading;
	}
    
    

}
