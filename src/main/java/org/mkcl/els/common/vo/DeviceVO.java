package org.mkcl.els.common.vo;

public class DeviceVO {
	// =============== ATTRIBUTES ====================
	/** The id. */
	private Long id;

	/** The number. */
	private Integer number;

	/** The formatted number **/
	private String formattedNumber;

	/** The content **/
	private String content;

	/** The status. */
	private String status;

	private String localisedStatus;

	private Boolean hasParent;

	private String parent;

	private String kids;

	private Boolean isFactualRecieved;

	public DeviceVO() {
		super();
	}

	public DeviceVO(final Long id, final Integer number, final String status) {
		super();
		this.id = id;
		this.number = number;
		this.status = status;
	}

	public DeviceVO(final Long id, final Integer number, final String status,
			final String localisedStatus) {
		super();
		this.id = id;
		this.number = number;
		this.status = status;
		this.localisedStatus = localisedStatus;
	}

	public DeviceVO(final Long id, final Integer number, final String status,
			final String localisedStatus, final Boolean isFactualRecieved) {
		super();
		this.id = id;
		this.number = number;
		this.status = status;
		this.isFactualRecieved = isFactualRecieved;
		this.localisedStatus = localisedStatus;
	}

	public DeviceVO(final Long id, final Integer number, final String status,
			final Boolean hasParent, final String parent, final String kids) {
		super();
		this.id = id;
		this.number = number;
		this.status = status;
		this.hasParent = hasParent;
		this.parent = parent;
		this.kids = kids;
	}

	public DeviceVO(final String formattedNumber, final String content) {
		super();
		this.formattedNumber = formattedNumber;
		this.content = content;
	}

	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(final Integer number) {
		this.number = number;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(final String status) {
		this.status = status;
	}

	public Boolean getHasParent() {
		return hasParent;
	}

	public void setHasParent(final Boolean hasParent) {
		this.hasParent = hasParent;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(final String parent) {
		this.parent = parent;
	}

	public String getKids() {
		return kids;
	}

	public void setKids(final String kids) {
		this.kids = kids;
	}

	public Boolean getIsFactualRecieved() {
		return isFactualRecieved;
	}

	public void setIsFactualRecieved(final Boolean isFactualRecieved) {
		this.isFactualRecieved = isFactualRecieved;
	}

	public String getFormattedNumber() {
		return formattedNumber;
	}

	public void setFormattedNumber(final String formattedNumber) {
		this.formattedNumber = formattedNumber;
	}

	public String getContent() {
		return content;
	}

	public void setContent(final String content) {
		this.content = content;
	}

	public String getLocalisedStatus() {
		return localisedStatus;
	}

	public void setLocalisedStatus(final String localisedStatus) {
		this.localisedStatus = localisedStatus;
	}

}
