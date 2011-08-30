/*
******************************************************************
File: org.mkcl.els.service.impl.GridServiceImpl.java
Copyright (c) 2011, vishals, MKCL
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
package org.mkcl.els.service.impl;

import java.util.Locale;

import org.mkcl.els.common.vo.GridConfig;
import org.mkcl.els.common.vo.GridData;
import org.mkcl.els.domain.Grid;
import org.mkcl.els.repository.GridRepository;
import org.mkcl.els.service.IGridService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * The Class GridServiceImpl.
 *
 * @author vishals
 * @version v1.0.0
 */
/**
 * @author vishals
 *
 */
@Service
public class GridServiceImpl extends GenericServiceImpl<Grid,Long> implements IGridService{

	
	/** The grid repository. */
	private GridRepository gridRepository;
	
	/**
	 * Sets the repository.
	 *
	 * @param gridRepository the new repository
	 */
	@Autowired
	public void setRepository(GridRepository gridRepository) {
		this.dao = gridRepository;
		this.gridRepository = gridRepository;
	}
	
	/* (non-Javadoc)
	 * @see org.mkcl.els.service.IGridService#getData(java.lang.Long)
	 */
	public GridData getData(Long gridId, Integer rows, Integer page, String sidx, String order) {
		return gridRepository.getData(gridId,rows,page,sidx,order);
	}

	
	/* (non-Javadoc)
	 * @see org.mkcl.els.service.IGridService#getConfig(java.lang.Long)
	 */
	public GridConfig getConfig(Long gridId) {
		Grid grid = gridRepository.find(gridId);
		GridConfig gridConfig = new GridConfig(grid.getTitle(),
				grid.getColNames(), grid.getColModel(), grid.getHeight(),
				grid.getWidth(), grid.getPageSize(), grid.getSortOrder(),
				grid.getDetailView());
		return gridConfig;
	}

	/* (non-Javadoc)
	 * @see org.mkcl.els.service.IGridService#findByName(java.lang.String)
	 */
	public Grid findByName(String name) {
		return gridRepository.findByName(name);
	}

	
	/* (non-Javadoc)
	 * @see org.mkcl.els.service.IGridService#getData(java.lang.Long, java.lang.Integer, java.lang.Integer, java.lang.String, java.lang.String, java.util.Locale)
	 */
	public GridData getData(Long gridId, Integer limit, Integer page, String sidx,
			String order, Locale locale) {
		return gridRepository.getData(gridId,limit,page,sidx,order,locale);
	}

	/* (non-Javadoc)
	 * @see org.mkcl.els.service.IGridService#getData(java.lang.Long, java.lang.Integer, java.lang.Integer, java.lang.String, java.lang.String, java.lang.String, java.util.Locale)
	 */
	public GridData getData(Long gridId, Integer limit, Integer page,
			String sidx, String order, String filterSql, Locale locale) {
		return gridRepository.getData(gridId,limit,page,sidx,order,filterSql,locale);
	}

}
