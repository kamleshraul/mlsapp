/*
******************************************************************
File: org.mkcl.els.service.IConstituencyService.java
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
package org.mkcl.els.service;

import java.util.List;

import org.mkcl.els.common.vo.MasterVO;
import org.mkcl.els.domain.Constituency;
import org.mkcl.els.domain.District;
import org.mkcl.els.domain.Reference;

// TODO: Auto-generated Javadoc
/**
 * The Interface IConstituencyService.
 *
 * @author sandeeps
 * @version v1.0.0
 */
public interface IConstituencyService extends IGenericService<Constituency ,Long>{

	/**
	 * Find by name.
	 *
	 * @param name the name
	 * @return the constituency
	 */
	public Constituency findByName(String name);
	
	public List<Constituency> findConstituenciesByDistrictName(String name);
	
	public List<Constituency> findConstituenciesByDistrictId(Long districtId);
	
	public List<Reference> findConstituenciesStartingWith(String param);
	
	public List<Constituency> findAllSorted();

	public List<MasterVO> findAllSortedVO(String locale);
	



}
