/*
******************************************************************
File: org.mkcl.els.service.impl.AssemblyServiceImpl.java
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

import java.util.Date;
import java.util.List;

import org.mkcl.els.domain.Assembly;
import org.mkcl.els.domain.AssemblyNumber;
import org.mkcl.els.repository.AssemblyRepository;
import org.mkcl.els.service.IAssemblyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// TODO: Auto-generated Javadoc
/**
 * The Class AssemblyServiceImpl.
 *
 * @author sandeeps
 * @version v1.0.0
 */
@Service
public class AssemblyServiceImpl extends GenericServiceImpl<Assembly,Long>
implements IAssemblyService{
	
	/** The assembly repository. */
	private AssemblyRepository assemblyRepository;
	
	/**
	 * Sets the assembly repository.
	 *
	 * @param assemblyRepository the new assembly repository
	 */
	@Autowired
	public void setAssemblyRepository(AssemblyRepository assemblyRepository) {
		this.dao = assemblyRepository;
		this.assemblyRepository = assemblyRepository;
	}

	/* (non-Javadoc)
	 * @see org.mkcl.els.service.IAssemblyService#findByAssembly(java.lang.String, java.lang.String)
	 */
	@Override
	public Assembly findByAssembly(String assembly) {
		return assemblyRepository.findByAssembly(assembly);
	}

	@Override
	public Assembly findCurrentAssembly(String locale) {
		return assemblyRepository.findCurrentAssembly(locale);
	}

	@Override
	public List<Assembly> findAllSorted(String locale) {
		return assemblyRepository.findAllSorted(locale);
	}

	@Override
	public void updatePreviousCurrentAssembly(String locale) {
		assemblyRepository.updatePreviousCurrentAssembly(locale);
	}

}
