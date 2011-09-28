/*
******************************************************************
File: org.mkcl.els.service.impl.AssemblyNumberServiceImpl.java
Copyright (c) 2011, amitd, ${company}
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

import org.mkcl.els.domain.AssemblyNumber;
import org.mkcl.els.repository.AssemblyNumberRepository;
import org.mkcl.els.service.IAssemblyNumberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * The Class AssemblyNumberServiceImpl.
 *
 * @author amitd
 * @version v1.0.0
 */
@Service
public class AssemblyNumberServiceImpl 
	extends GenericServiceImpl<AssemblyNumber,Long>
	implements IAssemblyNumberService {

	/** The assembly number repository. */
	private AssemblyNumberRepository assemblyNumberRepository;
	
	/**
	 * Sets the assembly number repository.
	 *
	 * @param assemblyNumberRepository the new assembly number repository
	 */
	@Autowired
	public void setAssemblyNumberRepository(
			AssemblyNumberRepository assemblyNumberRepository) {
		this.dao = assemblyNumberRepository;
		this.assemblyNumberRepository = assemblyNumberRepository;
	}

	/**
	 * Find by assembly number.
	 *
	 * @param assemblyNumber the assemblyNumber
	 * @return the assembly number
	 */
	@Override
	public AssemblyNumber findByAssemblyNo(String assemblyNo) {
		return this.assemblyNumberRepository.findByAssemblyNo(assemblyNo);
	}

	@Override
	public List<AssemblyNumber> findAllSortedByNumber() {
		return assemblyNumberRepository.findAllSortedByNumber();
	}

}
