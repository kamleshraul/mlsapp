package org.mkcl.els.repository;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Query;

import org.mkcl.els.domain.Bookmark;
import org.mkcl.els.domain.Part;
import org.mkcl.els.domain.Slot;
import org.springframework.stereotype.Repository;

@Repository
public class BookmarkRepository extends BaseRepository<Bookmark, Serializable>{

	public List<Bookmark> findBookmarkBySlotPartAndKey(Slot slot, Part part,
			String strBookmarkKey) {
		String strQuery="SELECT m FROM Bookmark m WHERE " +
				"m.slot=:slotId AND m.masterPart=:partId";
		Query query=this.em().createQuery(strQuery);
		query.setParameter("slotId", slot);
		query.setParameter("partId", part);
		//query.setParameter("bookmarkKey", strBookmarkKey);
		List<Bookmark> bookmarks= query.getResultList();
		return bookmarks;
	}

}
