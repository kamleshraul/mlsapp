package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.mkcl.els.common.vo.ReferenceLinkVO;
import org.mkcl.els.common.vo.TemplateVO;
import org.mkcl.els.common.vo.TestVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.mkcl.els.domain.BaseDomain;
import org.springframework.beans.factory.annotation.Configurable;
import org.mkcl.els.repository.ReferenceLinkRepository;


@Configurable
@Entity
@Table(name = "reference_links")
@JsonIgnoreProperties(value={"houseType","houseType","referenceType"},ignoreUnknown=true)
public class ReferenceLinks extends BaseDomain implements Serializable{

	 private static final transient long serialVersionUID = 1L;
	 
	/** Yaadi Name */ 
	@Column(length = 50) 
	private String name;
	
	/** Yaadi Date */
	@Temporal(TemporalType.DATE)
	private Date date;
	
	/** Yaadi Link */
	@Column(length = 50)
	private String link;
	
	/** houseType **/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "housetype_id")
	private HouseType houseType;
	
	/** Device Type */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "devicetype_id")
	private DeviceType deviceType;
	
	  /** Session Id */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "session_id")
	private Session session;
	
	/** Reference Types */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "documenttype_id")
	private Referencetypes referenceType;
	
	
	/**  Yaadi Pdf Number  Number  */ 
	@Column(length = 50) 
	private Integer number;
	
	

	/** The member repository. */
	 @Autowired
	private transient ReferenceLinkRepository referenceLinkRepository;
	
	
	public ReferenceLinks() {
		super();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public HouseType getHouseType() {
		return houseType;
	}

	public void setHouseType(HouseType houseType) {
		this.houseType = houseType;
	}

	public DeviceType getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(DeviceType deviceType) {
		this.deviceType = deviceType;
	}

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public Referencetypes getReferenceType() {
		return referenceType;
	}

	public void setReferenceType(Referencetypes referenceType) {
		this.referenceType = referenceType;
	}
	
	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public static ReferenceLinkRepository getReferenceLinkRepository() {
		ReferenceLinkRepository referenceLinkRepository = new ReferenceLinks().referenceLinkRepository;
		if (referenceLinkRepository == null) {
			throw new IllegalStateException("ReferenceLinkRepository has not been injected in Reference Link Domain");
		}
		return referenceLinkRepository;
	}
	
	public static List<ReferenceLinkVO> findReferences(final Integer housetype, final Integer devicetype, final Integer documenttype) {
		return getReferenceLinkRepository().findReferences(housetype,devicetype,documenttype);
	}
	
	public static List<TestVO> getSessionAndReferenceNumber(final Integer houseType,final String locale ) {
		return getReferenceLinkRepository().getSessionAndReferenceNumber(houseType,locale);
	}
	
	public static List<TemplateVO> getYaadiLinkAndNumber(final Integer sessionId,final Integer documentTypeId) {
		return getReferenceLinkRepository().getYaadiLinkAndNumber(sessionId, documentTypeId);
	}
	
}