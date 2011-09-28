/*
******************************************************************
File: org.mkcl.els.service.impl.TehsilServiceImpl.java
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

import org.mkcl.els.domain.Tehsil;
import org.mkcl.els.repository.TehsilRepository;
import org.mkcl.els.service.ITehsilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// TODO: Auto-generated Javadoc
/**
 * The Class TehsilServiceImpl.
 *
 * @author sandeeps
 * @version v1.0.0
 */
@Service
public class TehsilServiceImpl extends GenericServiceImpl<Tehsil,Long> implements ITehsilService{

	/** The Tehsil repository. */
	private TehsilRepository TehsilRepository;

	/**
	 * Sets the tehsil repository.
	 *
	 * @param TehsilRepository the new tehsil repository
	 */
	@Autowired
	public void setTehsilRepository(TehsilRepository TehsilRepository) 
	{
		this.dao = TehsilRepository;
		this.TehsilRepository = TehsilRepository;
	}

	/* (non-Javadoc)
	 * @see org.mkcl.els.service.ITehsilService#findByName(java.lang.String)
	 */
	@Override
	public Tehsil findByName(String name) {
		return TehsilRepository.findByName(name);
	}

	@Override
	public List<Tehsil> findTehsilsByDistrictName(String name) {
		return TehsilRepository.findTehsilsByDistrictName(name);
	}

	@Override
	public List<Tehsil> findTehsilsByDistrictId(Long districtId) {
		return TehsilRepository.findTehsilsByDistrictId(districtId);

	}
}
