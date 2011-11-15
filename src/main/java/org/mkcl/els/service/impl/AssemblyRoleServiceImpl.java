/*
******************************************************************
File: org.mkcl.els.service.impl.AssemblyRoleServiceImpl.java
Copyright (c) 2011, amitd, MKCL
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

import org.mkcl.els.domain.AssemblyRole;
import org.mkcl.els.repository.AssemblyRoleRepository;
import org.mkcl.els.service.IAssemblyRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * The Class AssemblyRoleServiceImpl.
 *
 * @author amitd
 * @version v1.0.0
 */
@Service
public class AssemblyRoleServiceImpl 
	extends GenericServiceImpl<AssemblyRole,Long>
	implements IAssemblyRoleService{

	/** The assembly role repository. */
	private AssemblyRoleRepository assemblyRoleRepository;
	
	/**
	 * Sets the assembly role repository.
	 *
	 * @param assemblyRoleRepository the new assembly role repository
	 */
	@Autowired
	public void setAssemblyRoleRepository(
			AssemblyRoleRepository assemblyRoleRepository) {
		this.dao = assemblyRoleRepository;
		this.assemblyRoleRepository = assemblyRoleRepository;
	}

	/**
	 * Find by name.
	 *
	 * @param name the name
	 * @return the assembly role
	 */
	@Override
	public AssemblyRole findByName(String name) {
		return this.assemblyRoleRepository.findByName(name);
	}

	@Override
	public List<AssemblyRole> findAllSorted(String locale) {
		return assemblyRoleRepository.findAllSorted(locale);
	}

	@Override
	public List<AssemblyRole> findUnassignedRoles(String locale, Long memberId) {
		return assemblyRoleRepository.findUnassignedRoles(locale,memberId);
		
	}


}
