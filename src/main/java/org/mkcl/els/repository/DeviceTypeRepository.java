package org.mkcl.els.repository;

import java.io.Serializable;
import java.util.List;

import org.mkcl.els.domain.DeviceType;
import org.springframework.stereotype.Repository;

import com.trg.search.Search;

@Repository
public class DeviceTypeRepository extends BaseRepository<DeviceType, Serializable>{

    //get only device types starting with a particular pattern like questions
    public List<DeviceType> findDeviceTypesStartingWith(final String pattern,final String locale){
        Search search=new Search();
        search.addFilterEqual("locale",locale);
        search.addFilterLike("type",pattern+"%");
        return this.search(search);
    }
}
