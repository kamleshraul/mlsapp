/*
******************************************************************
File: org.mkcl.els.domain.Document.java
Copyright (c) 2011, sandeeps, ${company}
All rights reserved.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.

******************************************************************
 */

package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


// TODO: Auto-generated Javadoc
/**
 * The Class Document.
 *
 * @author sandeeps
 * @version v1.0.0
 */
@Entity
@Table(name = "documents")
public class Document implements Serializable{

    /** The id. */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;    

    /** The tag. */
    @Column(length=100)
    private String tag;  	

    /** The type. */
    @Column(length=30)
	private String type;    

    /** The original file name. */
    @Column(length=255)
    private String originalFileName;	

    /** The created on. */
    @Temporal( TemporalType.TIMESTAMP)
	private Date createdOn;
    
    /** The file data. */
    @Lob
    private byte[] fileData;
    
    /** The file size. */
    private long fileSize;

	/**
	 * Instantiates a new document.
	 */
	public Document() {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * Instantiates a new document.
	 *
	 * @param tag the tag
	 * @param type the type
	 * @param originalFileName the original file name
	 * @param createdOn the created on
	 * @param fileData the file data
	 * @param fileSize the file size
	 */
	public Document(String tag, String type, String originalFileName,
			Date createdOn, byte[] fileData, long fileSize) {
		super();
		this.tag = tag;
		this.type = type;
		this.originalFileName = originalFileName;
		this.createdOn = createdOn;
		this.fileData = fileData;
		this.fileSize = fileSize;
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(Long id) {
		this.id = id;
	}

	/**
	 * Gets the tag.
	 *
	 * @return the tag
	 */
	public String getTag() {
		return tag;
	}

	/**
	 * Sets the tag.
	 *
	 * @param tag the new tag
	 */
	public void setTag(String tag) {
		this.tag = tag;
	}

	/**
	 * Gets the type.
	 *
	 * @return the type
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the type.
	 *
	 * @param type the new type
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Gets the original file name.
	 *
	 * @return the original file name
	 */
	public String getOriginalFileName() {
		return originalFileName;
	}

	/**
	 * Sets the original file name.
	 *
	 * @param originalFileName the new original file name
	 */
	public void setOriginalFileName(String originalFileName) {
		this.originalFileName = originalFileName;
	}

	/**
	 * Gets the created on.
	 *
	 * @return the created on
	 */
	public Date getCreatedOn() {
		return createdOn;
	}

	/**
	 * Sets the created on.
	 *
	 * @param createdOn the new created on
	 */
	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	/**
	 * Gets the file data.
	 *
	 * @return the file data
	 */
	public byte[] getFileData() {
		return fileData;
	}

	/**
	 * Sets the file data.
	 *
	 * @param fileData the new file data
	 */
	public void setFileData(byte[] fileData) {
		this.fileData = fileData;
	}

	/**
	 * Gets the file size.
	 *
	 * @return the file size
	 */
	public long getFileSize() {
		return fileSize;
	}

	/**
	 * Sets the file size.
	 *
	 * @param fileSize the new file size
	 */
	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	
}
