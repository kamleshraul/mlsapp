package org.mkcl.els.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.mkcl.els.repository.ProceedingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;

@Entity
@Configurable
@Table(name="proceedings")
public class Proceeding extends BaseDomain implements Serializable{

	/****Attributes****/
	private static final long serialVersionUID = 1L;

	@ManyToOne(fetch=FetchType.LAZY)
	private Slot slot;

	@OneToMany(fetch=FetchType.LAZY,cascade=CascadeType.ALL,mappedBy="proceeding")
	private List<Part> parts;

	@Autowired
    private transient ProceedingRepository proceedingRepository;
	/****Constructors****/

	public Proceeding() {
		super();
	}


	public Proceeding(Slot slot, List<Part> parts) {
		super();
		this.slot = slot;
		this.parts = parts;
	}


	/****Domain Methods****/

	public static ProceedingRepository getProceedingRepository() {
		ProceedingRepository proceedingRepository = new Proceeding().proceedingRepository;
        if (proceedingRepository == null) {
            throw new IllegalStateException(
                    "proceedingRepository has not been injected in Proceeding Domain");
        }
        return proceedingRepository;
    }



	/****Getters and Setters****/

	public Slot getSlot() {
		return slot;
	}


	public void setSlot(Slot slot) {
		this.slot = slot;
	}


	public List<Part> getParts() {
		return parts;
	}


	public void setParts(List<Part> parts) {
		this.parts = parts;
	}


	public static Boolean removePart(Proceeding proceeding, Long partId) {
		return getProceedingRepository().removePart(proceeding, partId);
	}


	public static List<Proceeding> findAllFilledProceedingBySlot(Slot s) {
		return getProceedingRepository().findAllFilledProceedingBySlot(s);
	}




}
