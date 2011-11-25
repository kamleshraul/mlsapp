package org.mkcl.els.repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.sql.DataSource;

import org.mkcl.els.common.vo.GridData;
import org.mkcl.els.common.vo.MemberInRoleVO;
import org.mkcl.els.domain.Assembly;
import org.mkcl.els.domain.AssemblyRole;
import org.mkcl.els.domain.MemberDetails;
import org.mkcl.els.domain.MemberRole;
import org.mkcl.els.service.IAssemblyService;
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
	private CustomParameterRepository customParameterRepository;
	
	@Autowired
	private AssemblyRoleRepository assemblyRoleRepository;
	
	
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
		String query="SELECT md.id,md.first_name,md.middle_name,md.last_name from member_details as md where md.id not in(select member from member_roles where role="+roleId+" and status='Unassigned')";
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
		String query="SELECT md.id,md.first_name,md.middle_name,md.last_name from member_details as md where md.id in(select member from member_roles where role="+roleId+" and status='Assigned')";
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
		search.addFilterEqual("role",assemblyRoleRepository.find(roleId));
		return this.search(search);
	}
	
	public GridData getUnAssignedMembers(Long roleId, Integer rows,
			Integer page, String sidx, String order, String sQl, Locale locale) {
		// TODO Auto-generated method stub
		return null;
	}
	public GridData getUnAssignedMembers(Long roleId, Integer rows,
			Integer page, String sidx, String order, Locale locale) {
		String select="SELECT m FROM MemberDetails m WHERE m.id not in(SELECT mr.member.id FROM MemberRole mr WHERE mr.role.id="+roleId+" and status='"+customParameterRepository.findByName("MEMBERROLE_ASSIGNED").getValue()+"') and m.locale='"+locale+"' ORDER BY " + sidx + " " + order;
		String count_select ="SELECT count(m) FROM MemberDetails m WHERE m.id not in(SELECT mr.member.id FROM MemberRole mr WHERE mr.role.id="+roleId+" and status='"+customParameterRepository.findByName("MEMBERROLE_ASSIGNED").getValue()+"') and m.locale='"+locale+"'";
		Query countQuery=this.em().createQuery(count_select);
		Query query = this.em().createQuery(select);		
		Long count =  (Long) countQuery.getSingleResult();
		Integer total_pages=0;
		if( count >0 ) { 
			total_pages = (int) Math.ceil((float)count/rows); 
		} 
		if (page > total_pages){
			page = total_pages;
		}
		int start = (int) (rows * page - rows);
		if(start<0){
			start=0;
		}
		query.setFirstResult(start);
		query.setMaxResults((int)(count>rows?count:rows));		
		List<Map<String,Object>> records = query.getResultList();		
		GridData gridVO = new GridData(page,total_pages,count,records);
		return gridVO;
	}	
	
	public GridData getAssignedUnassignedMembers(Long roleId, Integer rows, Integer page,
			String sidx, String order, String sQl, Locale locale) {
		String count_select = "SELECT count(m) FROM MemberRole m WHERE m.role.id="+ roleId+" and m.status='"+ customParameterRepository.findByName("MEMBERROLE_ASSIGNED").getValue() +"'"+ sQl + " ORDER BY " + sidx + " " + order;
		String select= "SELECT m,role,assembly,member FROM MemberRole m JOIN m.member member JOIN m.role role JOIN m.assembly assembly WHERE m.role.id=" + roleId+  sQl +" ORDER BY " + sidx + " " + order;
		Query countQuery=this.em().createQuery(count_select);
		Query query = this.em().createQuery(select);		
		Long count =  (Long) countQuery.getSingleResult();
		Integer total_pages=0;
		if( count >0 ) { 
			total_pages = (int) Math.ceil((float)count/rows); 
		} 
		if (page > total_pages){
			page = total_pages;
		}
		int start = (int) (rows * page - rows);
		if(start<0){
			start=0;
		}
		query.setFirstResult(start);
		query.setMaxResults((int)(count>rows?count:rows));		
		List<Map<String,Object>> records = query.getResultList();
		GridData gridVO = new GridData(page,total_pages,count,records);
		return gridVO;
	}	
	public GridData getAssignedUnassignedMembers(Long roleId, Integer rows, Integer page,
			String sidx, String order, Locale locale) {		
		String count_select = "SELECT count(m) FROM MemberRole m WHERE m.role.id="+roleId+" and m.status='"+ customParameterRepository.findByName("MEMBERROLE_ASSIGNED").getValue() +"' ORDER BY " + sidx + " " + order;
		String select= "SELECT m FROM MemberRole m  WHERE m.role.id="+roleId+"  ORDER BY " + sidx + " " + order;
		Query countQuery=this.em().createQuery(count_select);
		Query query = this.em().createQuery(select);		
		Long count =  (Long) countQuery.getSingleResult();
		Integer total_pages=0;
		if( count >0 ) { 
			total_pages = (int) Math.ceil((float)count/rows); 
		} 
		if (page > total_pages){
			page = total_pages;
		}
		int start = (int) (rows * page - rows);
		if(start<0){
			start=0;
		}
		query.setFirstResult(start);
		query.setMaxResults((int)(count>rows?count:rows));		
		List<Map<String,Object>> records = query.getResultList();		
		GridData gridVO = new GridData(page,total_pages,count,records);
		return gridVO;
	}
	
	public GridData getAssignedUnassignedRoles(Long memberId, Integer rows, Integer page,
			String sidx, String order, String sQl, Locale locale) {
		String count_select = "SELECT count(m) FROM MemberRole m WHERE m.member.id="+ memberId+" and m.status='"+ customParameterRepository.findByName("MEMBERROLE_ASSIGNED").getValue() +"'"+ sQl + " ORDER BY " + sidx + " " + order;
		String select= "SELECT m,role,assembly,member FROM MemberRole m JOIN m.member member JOIN m.role role JOIN m.assembly assembly WHERE m.member.id=" + memberId+ sQl +" ORDER BY " + sidx + " " + order;
		Query countQuery=this.em().createQuery(count_select);
		Query query = this.em().createQuery(select);		
		Long count =  (Long) countQuery.getSingleResult();
		Integer total_pages=0;
		if( count >0 ) { 
			total_pages = (int) Math.ceil((float)count/rows); 
		} 
		if (page > total_pages){
			page = total_pages;
		}
		int start = (int) (rows * page - rows);
		if(start<0){
			start=0;
		}
		query.setFirstResult(start);
		query.setMaxResults((int)(count>rows?count:rows));		
		List<Map<String,Object>> records = query.getResultList();
		GridData gridVO = new GridData(page,total_pages,count,records);
		return gridVO;
		}
	
	@SuppressWarnings("unchecked")
	public GridData getAssignedUnassignedRoles(Long memberId, Integer rows, Integer page,
			String sidx, String order, Locale locale) {		
		String count_select = "SELECT count(m) FROM MemberRole m WHERE m.member.id="+memberId+" and m.status='"+ customParameterRepository.findByName("MEMBERROLE_ASSIGNED").getValue() +"' ORDER BY " + sidx + " " + order;
		String select= "SELECT m FROM MemberRole m  WHERE m.member.id="+memberId+"  ORDER BY " + sidx + " " + order;
		Query countQuery=this.em().createQuery(count_select);
		Query query = this.em().createQuery(select);		
		Long count =  (Long) countQuery.getSingleResult();
		Integer total_pages=0;
		if( count >0 ) { 
			total_pages = (int) Math.ceil((float)count/rows); 
		} 
		if (page > total_pages){
			page = total_pages;
		}
		int start = (int) (rows * page - rows);
		if(start<0){
			start=0;
		}
		query.setFirstResult(start);
		query.setMaxResults((int)(count>rows?count:rows));		
		List<Map<String,Object>> records = query.getResultList();		
		GridData gridVO = new GridData(page,total_pages,count,records);
		return gridVO;
	}
	
	public MemberRole checkForDuplicateMemberRole(MemberRole memberRole) {	
		MemberRole tempMemberRole=null;
		try{
		SimpleDateFormat format=new SimpleDateFormat(customParameterRepository.findByName("SERVER_DATEFORMAT").getValue());
		SimpleDateFormat formatDB=new SimpleDateFormat(customParameterRepository.findByName("DB_DATEFORMAT").getValue());
		Date fromDate=format.parse(memberRole.getFromDate());
		Date toDate=format.parse(memberRole.getToDate());
		String strFromDate=formatDB.format(fromDate);
		String strToDate=formatDB.format(toDate);
		String searchQuery="SELECT m FROM MemberRole m WHERE m.member.id="+memberRole.getMember().getId()+" " +
				"and m.assembly.id="+memberRole.getAssembly().getId()+" "+
				"and m.role.id="+memberRole.getRole().getId()+" "+
				"and m.locale='"+memberRole.getLocale()+"' "+
				"and (" +
				"(STR_TO_DATE(m.fromDate,concat('%d','/','%m','/','%Y'))='"+strFromDate+"' and STR_TO_DATE(m.toDate,concat('%d','/','%m','/','%Y'))='"+strToDate+"') or"+
				" (STR_TO_DATE(m.fromDate,concat('%d','/','%m','/','%Y'))<='"+strFromDate+"' and STR_TO_DATE(m.toDate,concat('%d','/','%m','/','%Y'))>='"+strToDate+"') or"+
				" (STR_TO_DATE(m.fromDate,concat('%d','/','%m','/','%Y'))<='"+strFromDate+"' and STR_TO_DATE(m.toDate,concat('%d','/','%m','/','%Y'))>='"+strFromDate+"' and STR_TO_DATE(m.toDate,concat('%d','/','%m','/','%Y'))<='"+strToDate+"') or"+
				" (STR_TO_DATE(m.fromDate,concat('%d','/','%m','/','%Y'))<='"+strFromDate+"' and STR_TO_DATE(m.toDate,concat('%d','/','%m','/','%Y'))>='"+strToDate+"' and STR_TO_DATE(m.fromDate,concat('%d','/','%m','/','%Y'))>='"+strFromDate+"') or"+
				" (STR_TO_DATE(m.fromDate,concat('%d','/','%m','/','%Y'))>='"+strFromDate+"' and STR_TO_DATE(m.toDate,concat('%d','/','%m','/','%Y'))<='"+strToDate+"')"+
				")";		
		tempMemberRole=(MemberRole) this.em().createQuery(searchQuery).getSingleResult();
		}catch(NoResultException ex){
			return new MemberRole();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return tempMemberRole;
	}
	public boolean isMember(MemberDetails memberDetails, Assembly assembly, String fromdate, String todate) {
		boolean status=false;
		try {
			int count=0;
			SimpleDateFormat format=new SimpleDateFormat(customParameterRepository.findByName("SERVER_DATEFORMAT").getValue());
			SimpleDateFormat formatDB=new SimpleDateFormat(customParameterRepository.findByName("DB_DATEFORMAT").getValue());
			Date fromDate=format.parse(fromdate);
			Date toDate=format.parse(todate);
			String strFromDate=formatDB.format(fromDate);
			String strToDate=formatDB.format(toDate);
			String searchQuery="SELECT m FROM MemberRole m WHERE m.member.id="+memberDetails.getId()+" and m.assembly.id="+assembly.getId()+" and m.role.id="+assemblyRoleRepository.findByName("Member").getId()+" and m.status='"+customParameterRepository.findByName("MEMBERROLE_ASSIGNED").getValue()+"' and " +
					"((STR_TO_DATE(m.fromDate,concat('%d','/','%m','/','%Y'))<='"+strFromDate+"' and STR_TO_DATE(m.toDate,concat('%d','/','%m','/','%Y'))>='"+strToDate+"') or (m.fromDate='"+fromdate+"' and m.toDate='"+todate+"'))";
			count=this.em().createQuery(searchQuery).getResultList().size();
			if(count>0){
				status=true;
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return status;
		
	}
	public List<AssemblyRole> getUnassignedRoles(MemberDetails memberDetails,
			Assembly assembly, String locale) {
		Date assignmentDate=new Date();
		SimpleDateFormat format=new SimpleDateFormat(customParameterRepository.findByName("DB_DATEFORMAT").getValue());
		String strAssignmentDate=format.format(assignmentDate);
		String select=null;
		if(assembly.isCurrentAssembly()){
			/*
			 * Roles that have not been assigned for selected assembly,role,member
			 * Roles that have been unassigned or assigned and the date of assignment is >to date or date of assignment <from date
			 * 
			 * 
			 */
			select="SELECT ar FROM AssemblyRole ar WHERE ar.id NOT IN(SELECT mr.role.id FROM MemberRole mr WHERE mr.member.id="+memberDetails.getId()+" and mr.assembly.id="+assembly.getId()+" and mr.status='"+customParameterRepository.findByName("MEMBERROLE_ASSIGNED").getValue()+"')"+
							"OR ar.id IN(SELECT mr.role.id FROM MemberRole mr " +"WHERE mr.member.id="+memberDetails.getId()+"and mr.assembly.id="+assembly.getId()+"and (mr.status='"+customParameterRepository.findByName("MEMBERROLE_UNASSIGNED").getValue()+"' or mr.status='"+customParameterRepository.findByName("MEMBERROLE_ASSIGNED").getValue()+"') and (STR_TO_DATE(mr.fromDate,concat('%d','/','%m','/','%Y'))>'"+strAssignmentDate+"' or STR_TO_DATE(mr.toDate,concat('%d','/','%m','/','%Y'))< '"+strAssignmentDate+"') ) and ar.locale='"+locale+"' ORDER BY ar.name";
		}else{
			select="SELECT ar FROM AssemblyRole ar WHERE ar.id NOT IN(SELECT mr.role.id FROM MemberRole mr WHERE mr.member.id="+memberDetails.getId()+" and mr.assembly.id="+assembly.getId()+" and mr.status='"+customParameterRepository.findByName("MEMBERROLE_ASSIGNED").getValue()+"') and ar.locale='"+locale+"' ORDER BY ar.name";
		}
		return this.em().createQuery(select).getResultList();
	}

}
