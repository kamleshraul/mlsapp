package org.mkcl.els.repository;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.mkcl.els.domain.CustomParameter;
import org.mkcl.els.domain.HouseType;
import org.mkcl.els.domain.Ministry;
import org.mkcl.els.domain.SessionType;
import org.springframework.stereotype.Repository;

@Repository
public class MinistryRepository extends BaseRepository<Ministry, Long> {

	@SuppressWarnings("unchecked")
	public List<Ministry> findUnassignedMinistries(final String locale) {
		CustomParameter dbDateFormat =
			CustomParameter.findByName(CustomParameter.class, "DB_DATEFORMAT", "");
		Date currDate = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(dbDateFormat.getValue());
		String strCurrDate = sdf.format(currDate);

		/**
		 * I am trying to mimic mm.ministryToDate > CURDATE(), but since
		 * CURDATE() is MySQL specific i am using DB_DATEFORMAT from
		 * custom_parameters.
		 */
		String query = "SELECT m " +
		"FROM Ministry m " +
		"WHERE m.locale = '" + locale + "' AND " +
		"m.id NOT IN " +
			"(SELECT m.id " +
			"FROM MemberMinister mm JOIN mm.ministry m " +
			"WHERE mm.ministryToDate IS NULL OR mm.ministryToDate > '" + strCurrDate + "') " +
		"ORDER BY m.name";
		List<Ministry> ministries = this.em().createQuery(query).getResultList();
		return ministries;
	}

    @SuppressWarnings("rawtypes")
    public List<Ministry> findMinistriesAssignedToGroups(final HouseType houseType,
            final Integer sessionYear, final SessionType sessionType,final String locale) {
        String query="SELECT m.id,m.locale,m.version,m.is_expired,m.name,m.remarks FROM ministries as m "+
                     " JOIN groups_ministries as gm JOIN groups as g WHERE m.id=gm.ministry_id AND gm.group_id=g.id AND "+
                     " m.locale='"+locale+"' AND g.housetype_id="+houseType.getId()+" AND g.sessiontype_id="+sessionType.getId()+" AND "+
                     " g.group_year="+sessionYear+"  ORDER BY m.name asc";
        List results=this.em().createNativeQuery(query).getResultList();
        List<Ministry> ministries=new ArrayList<Ministry>();
        for(Object i:results){
            Object[] o=(Object[]) i;
            Ministry ministry=new Ministry();
            ministry.setId(Long.parseLong(o[0].toString()));
            ministry.setLocale(o[1].toString());
            ministry.setVersion(Long.parseLong(o[2].toString()));
            ministry.setIsExpired(Boolean.parseBoolean(o[3].toString()));
            ministry.setName(o[4].toString());
            ministry.setRemarks(o[5].toString());
            ministries.add(ministry);
        }
        return ministries;
    }
}
