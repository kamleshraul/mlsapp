
package org.mkcl.els.repository;

import java.util.List;

import org.mkcl.els.domain.AssemblyRole;
import org.mkcl.els.domain.CustomParameter;
import org.springframework.stereotype.Repository;

/**
 * The Class AssemblyRoleRepository.
 *
 * @author amitd
 * @version v1.0.0
 */
@Repository
public class AssemblyRoleRepository extends BaseRepository<AssemblyRole, Long> {
//
//    /** The custom parameter repository. */
//    @Autowired
//    private CustomParameterRepository customParameterRepository;
//
    /**
//     * Find unassigned roles.
     *
     * @param locale the locale
     * @param memberId the member id
     * @return the list
     * @author nileshp
     * @since v1.0.0
     */
    @SuppressWarnings("unchecked")
    public List<AssemblyRole> findUnassignedRoles(final String locale, final Long memberId) {
        String select = "SELECT a FROM AssemblyRole a WHERE a.id "
                + "not in(SELECT m.role.id FROM MemberRole m WHERE m.member.id="
                + memberId
                + " and m.status='"
                + ((CustomParameter) CustomParameter.findByName(CustomParameter.class,"MEMBERROLE_ASSIGNED",locale))
                        .getValue() + "') ORDER BY a.name asc";
        return this.em().createQuery(select).getResultList();
    }
}

//    /**
//     * Find by name and locale.
//     *
//     * @param name the name
//     * @param locale the locale
//     * @return the assembly role
//     * @author nileshp
//     * @since v1.0.0
//     */
//    public AssemblyRole findByNameAndLocale(final String name, final String locale) {
//        Search search = new Search();
//        search.addFilterEqual("locale", locale);
//        search.addFilterEqual("name", name);
//        return this.searchUnique(search);
//    }
//}
