package org.mkcl.els.repository;

import org.mkcl.els.domain.MemberRole;
import org.springframework.stereotype.Repository;

@Repository
public class MemberRoleRepository extends BaseRepository<MemberRole, Long> {

    public MemberRole findByNameHouseTypeLocale(String roleName,
            Long houseTypeId, String locale) {
        String query = "SELECT m FROM MemberRole m WHERE m.name='" + roleName
                + "' AND m.houseType.id=" + houseTypeId + " AND m.locale='"
                + locale + "'";
        return (MemberRole) this.em().createQuery(query).getSingleResult();
    }
}
