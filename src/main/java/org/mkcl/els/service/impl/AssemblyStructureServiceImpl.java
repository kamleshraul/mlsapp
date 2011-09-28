/*
******************************************************************
File: org.mkcl.els.service.impl.AssemblyStructureServiceImpl.java
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

import org.mkcl.els.domain.AssemblyStructure;
import org.mkcl.els.repository.AssemblyStructureRepository;
import org.mkcl.els.service.IAssemblyStructureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * The Class AssemblyStructureServiceImpl.
 *
 * @author amitd
 * @version v1.0.0
 */
@Service
public class AssemblyStructureServiceImpl 
	extends GenericServiceImpl<AssemblyStructure,Long>
	implements IAssemblyStructureService {

	/** The assembly structure repository. */
	private AssemblyStructureRepository assemblyStructureRepository;
	
	
	/**
	 * Sets the assembly structure repository.
	 *
	 * @param assemblyStructureRepository the new assembly structure repository
	 */
	@Autowired
	public void setAssemblyStructureRepository(
			AssemblyStructureRepository assemblyStructureRepository) {
		this.dao = assemblyStructureRepository;
		this.assemblyStructureRepository = assemblyStructureRepository;
	}


	/**
	 * Find by name.
	 *
	 * @param name the name
	 * @return the assembly structure
	 */
	@Override
	public AssemblyStructure findByName(String name) {
		return this.assemblyStructureRepository.findByName(name);
	}


	@Override
	public List<AssemblyStructure> findAllSortedByName(String locale) {
		return assemblyStructureRepository.findAllSortedByName(locale);
	}

}
