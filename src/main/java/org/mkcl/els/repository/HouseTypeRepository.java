package org.mkcl.els.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.mkcl.els.common.exception.ELSException;
import org.mkcl.els.domain.HouseType;
import org.springframework.stereotype.Repository;

@Repository
public class HouseTypeRepository extends BaseRepository<HouseType, Serializable> {

    @SuppressWarnings({ "rawtypes" })
    @Override
    public List<HouseType> findAll(final Class persistenceClass,
            final String sortBy, final String sortOrder, final String locale) {
        List<HouseType> houseTypes= super.findAll(persistenceClass, sortBy, sortOrder, locale);
        List<HouseType> selectedHouseTypes=new ArrayList<HouseType>();
        for(HouseType i:houseTypes){
            if(!i.getType().trim().equals("bothhouse")){
                selectedHouseTypes.add(i);
            }
        }
        return selectedHouseTypes;
    }

    public List<HouseType> findAllNoExclude(final String sortBy, final String sortOrder, final String locale) {
        return super.findAll(HouseType.class, sortBy, sortOrder, locale);
    }
}
