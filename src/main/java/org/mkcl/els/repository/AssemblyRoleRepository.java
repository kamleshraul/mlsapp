/*
******************************************************************
File: org.mkcl.els.repository.AssemblyRoleRepository.java
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
package org.mkcl.els.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import org.mkcl.els.common.vo.MemberInfo;
import org.mkcl.els.domain.AssemblyRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.trg.search.Search;

/**
 * The Class AssemblyRoleRepository.
 *
 * @author amitd
 * @version v1.0.0
 */
@Repository
public class AssemblyRoleRepository 
	extends BaseRepository<AssemblyRole, Long>{

	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Autowired
	private CustomParameterRepository customParameterRepository;

	/**
	 * Find by name.
	 *
	 * @param name the name
	 * @return the assembly role
	 */
	public AssemblyRole findByName(String name){
		Search search = new Search();
		search.addFilterEqual("name", name);
		AssemblyRole assemblyRole = this.searchUnique(search);
		return assemblyRole;
	}

	public List<AssemblyRole> findAllSorted(String locale) {
		Search search = new Search();
		search.addFilterEqual("locale", locale);
		search.addSort("name",false);
		return this.search(search);
	}

	@SuppressWarnings("unchecked")
	public List<AssemblyRole> findUnassignedRoles(String locale, Long memberId) {
		String select="SELECT a FROM AssemblyRole a WHERE a.id not in(SELECT m.role.id FROM MemberRole m WHERE m.member.id="+memberId+" and m.status='"+customParameterRepository.findByName("MEMBERROLE_ASSIGNED").getValue()+"') ORDER BY a.name asc";
		return 	this.em().createQuery(select).getResultList();
	}

	public AssemblyRole findByNameAndLocale(String name, String locale) {
		Search search=new Search();
		search.addFilterEqual("locale",locale);
		search.addFilterEqual("name",name);
		return this.searchUnique(search);
	}
}
