package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.mkcl.els.repository.BookmarkRepository;
import org.mkcl.els.repository.ClubbedEntityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Entity
@Configurable
@Table(name="bookmarks")
public class Bookmark extends BaseDomain implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	/****Attributes****/
	
	private String bookmarkKey; 
	
	@ManyToOne
	private Language language;
	
	@ManyToOne(cascade=CascadeType.REMOVE)
	private Part masterPart;
	
	@ManyToOne(cascade=CascadeType.REMOVE)
	private Part slavePart;
	
	@OneToOne
	private Reporter bookmarkReplacedBy;
	
	private Date bookmarkReplacedDate;
	
	@Column(length=30000)
	private String previousText;
	
	@Column(length=30000)
	private String textToBeReplaced;
	
	@ManyToOne(cascade=CascadeType.REMOVE)
	private Slot slot;
	
	@Autowired
    private transient BookmarkRepository bookmarkRepository;

	/****Constructors****/
	public Bookmark() {
		super();
	}

	public Bookmark(final String locale) {
		super(locale);
	}

	public Bookmark(String bookmarkKey, Language language, Part masterPart,
			Part slavePart, Reporter bookmarkReplacedBy,
			Date bookmarkReplacedDate, String previousText,
			String textToBeReplaced,Slot slot) {
		super();
		this.bookmarkKey = bookmarkKey;
		this.language = language;
		this.masterPart = masterPart;
		this.slavePart = slavePart;
		this.bookmarkReplacedBy = bookmarkReplacedBy;
		this.bookmarkReplacedDate = bookmarkReplacedDate;
		this.previousText = previousText;
		this.textToBeReplaced = textToBeReplaced;
		this.slot=slot;
	}
	
	/****Domain Methods****/
	
	  public static BookmarkRepository getBookmarkRepository() {
		  BookmarkRepository clubbedEntityRepository = new Bookmark().bookmarkRepository;
	        if (clubbedEntityRepository == null) {
	            throw new IllegalStateException(
	                    "ClubbedEntityRepository has not been injected in Clubbed Entity Domain");
	        }
	        return clubbedEntityRepository;
	    }

	public static List<Bookmark> findBookmarkBySlotPartAndKey(Slot slot, Part part,
			String strBookmarkKey) {
		return getBookmarkRepository().findBookmarkBySlotPartAndKey(slot,part,strBookmarkKey);
	}
	

	

	/****Getters and Setters****/

	public String getBookmarkKey() {
		return bookmarkKey;
	}

	public void setBookmarkKey(String bookmarkKey) {
		this.bookmarkKey = bookmarkKey;
	}

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	public Part getMasterPart() {
		return masterPart;
	}

	public void setMasterPart(Part masterPart) {
		this.masterPart = masterPart;
	}

	public Part getSlavePart() {
		return slavePart;
	}

	public void setSlavePart(Part slavePart) {
		this.slavePart = slavePart;
	}

	public Reporter getBookmarkReplacedBy() {
		return bookmarkReplacedBy;
	}

	public void setBookmarkReplacedBy(Reporter bookmarkReplacedBy) {
		this.bookmarkReplacedBy = bookmarkReplacedBy;
	}

	public Date getBookmarkReplacedDate() {
		return bookmarkReplacedDate;
	}

	public void setBookmarkReplacedDate(Date bookmarkReplacedDate) {
		this.bookmarkReplacedDate = bookmarkReplacedDate;
	}

	public String getPreviousText() {
		return previousText;
	}

	public void setPreviousText(String previousText) {
		this.previousText = previousText;
	}

	public String getTextToBeReplaced() {
		return textToBeReplaced;
	}

	public void setTextToBeReplaced(String textToBeReplaced) {
		this.textToBeReplaced = textToBeReplaced;
	}

	public Slot getSlot() {
		return slot;
	}

	public void setSlot(Slot slot) {
		this.slot = slot;
	}

	
	
	
}
