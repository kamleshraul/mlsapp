
package org.mkcl.els.common.vo;

import java.util.List;

/**
 * The Class WorkflowConfigVO.
 */
public class WorkflowConfigVO {

	/** The id. */
	private String id;

	/** The actors. */
	private List<MasterVO> actors;

	/**
	 * Sets the id.
	 *
	 * @param id the new id
	 */
	public void setId(final String id) {
		this.id = id;
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Sets the actors.
	 *
	 * @param actors the new actors
	 */
	public void setActors(final List<MasterVO> actors) {
		this.actors = actors;
	}

	/**
	 * Gets the actors.
	 *
	 * @return the actors
	 */
	public List<MasterVO> getActors() {
		return actors;
	}

}
