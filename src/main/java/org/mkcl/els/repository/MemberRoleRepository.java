package org.mkcl.els.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.mkcl.els.common.vo.MemberInRoleVO;
import org.mkcl.els.domain.Assembly;
import org.mkcl.els.domain.AssemblyRole;
import org.mkcl.els.domain.MemberRole;
import org.mkcl.els.service.IAssemblyRoleService;
import org.mkcl.els.service.IAssemblyService;
import org.mkcl.els.service.ICustomParameterService;
import org.mkcl.els.service.IMemberDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.trg.search.Search;

@Repository
public class MemberRoleRepository extends BaseRepository<MemberRole, Long>{

	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private IMemberDetailsService memberDetailsService;
	
	@Autowired
	private IAssemblyService assemblyService;
	
	@Autowired
	private ICustomParameterService customParameterService;
	
	@Autowired
	private IAssemblyRoleService assemblyRoleService;
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}	
	public void createMemberRole(long role, Long memberId, MemberRole memberRole) {
		String insertQuery="insert into member_roles(locale,remarks,to_date,from_date,version,assembly,member,role) values("+
		"'"+memberRole.getLocale()+"',"+
		"'"+memberRole.getRemarks()+"',"+
		"'"+memberRole.getToDate()+"',"+
		"'"+memberRole.getFromDate()+"',"+
		memberRole.getAssembly().getId()+","+
		memberId+","+
		role+")";
		jdbcTemplate.update(insertQuery);
	}
	public List<MemberRole> findByMemberId(Long memberId) {
		Search search=new Search();
		search.addFilterEqual("member",memberDetailsService.findById(memberId));
		return this.search(search);
	}
	public List<MemberInRoleVO> findUnassignedMembers(Long roleId) {
		String query="SELECT md.id,md.first_name,md.middle_name,md.last_name from member_details as md where md.id not in(select member from member_roles where role="+roleId+")";
		RowMapper<MemberInRoleVO> mapper = new RowMapper<MemberInRoleVO>() {
			@Override
			public MemberInRoleVO mapRow(ResultSet rs, int arg1) throws SQLException {
				MemberInRoleVO memberInRoleVO = new MemberInRoleVO();
				memberInRoleVO.setId(rs.getLong("id"));
				memberInRoleVO.setName(rs.getString("first_name")+" "+rs.getString("middle_name")+" "+rs.getString("last_name"));
				return memberInRoleVO;
			}
		};		
		return jdbcTemplate.query(query,mapper,new Object[]{});
	}
	public List<MemberInRoleVO> findAssignedMembers(Long roleId) {
		String query="SELECT md.id,md.first_name,md.middle_name,md.last_name from member_details as md where md.id in(select member from member_roles where role="+roleId+")";
		RowMapper<MemberInRoleVO> mapper = new RowMapper<MemberInRoleVO>() {
			@Override
			public MemberInRoleVO mapRow(ResultSet rs, int arg1) throws SQLException {
				MemberInRoleVO memberInRoleVO = new MemberInRoleVO();
				memberInRoleVO.setId(rs.getLong("id"));
				memberInRoleVO.setName(rs.getString("first_name")+" "+rs.getString("middle_name")+" "+rs.getString("last_name"));
				return memberInRoleVO;
			}
		};		
		return jdbcTemplate.query(query,mapper,new Object[]{});
	}
	public List<MemberRole> findByRoleId(Long roleId) {
		Search search=new Search();
		search.addFilterEqual("role",assemblyRoleService.findById(roleId));
		return this.search(search);
	}

}
