/*
 ******************************************************************
File: org.mkcl.els.service.impl.CustomParameterServiceImpl.java
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

import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.repository.CustomParameterRepository;
import org.mkcl.els.service.ICustomParameterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * The Class CustomParameterServiceImpl.
 * 
 * @author amitd
 * @version v1.0.0
 */
@Service
public class CustomParameterServiceImpl extends GenericServiceImpl<CustomParameter,Long> 
										implements ICustomParameterService
{
	
	/** The repository. */
	private CustomParameterRepository customParameterRepository;
	
	/**
	 * Sets the custom parameter repository.
	 *
	 * @param customParameterRepository the new custom parameter repository
	 */
	@Autowired
	public void setCustomParameterRepository(CustomParameterRepository customParameterRepository) 
	{
		this.dao = customParameterRepository;
		this.customParameterRepository = customParameterRepository;
	}
	
	@Override
	public CustomParameter findByName(String name) 
	{
		return customParameterRepository.findByName(name);
	}
	
}
