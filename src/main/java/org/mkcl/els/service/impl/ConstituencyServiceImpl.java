/*
******************************************************************
File: org.mkcl.els.service.impl.ConstituencyServiceImpl.java
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
package org.mkcl.els.service.impl;

import java.util.List;

import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.domain.Constituency;
import org.mkcl.els.domain.District;
import org.mkcl.els.domain.Reference;
import org.mkcl.els.repository.ConstituencyRepository;
import org.mkcl.els.service.IConstituencyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// TODO: Auto-generated Javadoc
/**
 * The Class ConstituencyServiceImpl.
 *
 * @author sandeeps
 * @version v1.0.0
 */
@Service
public class ConstituencyServiceImpl extends GenericServiceImpl<Constituency,Long> implements IConstituencyService{

	/** The Constituency repository. */
	private ConstituencyRepository constituencyRepository;

	/**
	 * Sets the constituency repository.
	 *
	 * @param constituencyRepository the new constituency repository
	 */
	@Autowired
	public void setConstituencyRepository(ConstituencyRepository constituencyRepository) 
	{
		this.dao = constituencyRepository;
		this.constituencyRepository = constituencyRepository;
	}

	/* (non-Javadoc)
	 * @see org.mkcl.els.service.IConstituencyService#findByName(java.lang.String)
	 */
	@Override
	public Constituency findByName(String name) {
		return constituencyRepository.findByName(name);
	}

	@Override
	public List<Constituency> findConstituenciesByDistrictName(String name) {
		return constituencyRepository.findConstituenciesByDistrictName(name);
	}

	@Override
	public List<Constituency> findConstituenciesByDistrictId(Long districtId) {
		return constituencyRepository.findConstituenciesByDistrictId(districtId);

	}

	@Override
	public List<Reference> findConstituenciesStartingWith(String param) {
		// TODO Auto-generated method stub
		return constituencyRepository.findConstituenciesStartingWith(param);
	}

	@Override
	public List<Constituency> findAllSorted() {
		return constituencyRepository.findAllSorted();
	}

	@Override
	public List<MasterVO> findAllSortedVO() {
		return constituencyRepository.findAllSortedVO();
	}



}
