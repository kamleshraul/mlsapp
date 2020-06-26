/**
 * See the file LICENSE for redistribution information.
 *
 * Copyright (c) 2012 MKCL.  All rights reserved.
 *
 * Project: e-Legislature
 * File: org.mkcl.els.repository.MenuItemRepository.java
 * Created On: Jan 6, 2012
 *//*
package org.mkcl.els.repository;

import java.util.List;

import org.mkcl.els.domain.MenuItem;
import org.springframework.stereotype.Repository;

import com.trg.search.Search;

*//**
 * The Class MenuItemRepository.
 *
 * @author vishals
 * @version v1.0.0
 *//*
@Repository
public class MenuItemRepository extends BaseRepository<MenuItem, Long> {

	*//**
	 * List.
	 *
	 * @param parent the parent
	 * @return the list< menu item>
	 * @author meenalw
	 * @since v1.0.0
	 *//*
	public List<MenuItem> findMenuItemsByParent(final MenuItem parent) {
		Search search = new Search();
		if (parent == null) {
			search.addFilterNull("parent").addSort("position", false);
		} else {
			search.addFilterEqual("parent", parent).addSort("position", false);
		}
		return this.search(search);
	}


	*//**
	 * Menu item.
	 *
	 * @param textKey the text key
	 * @return the menu item
	 * @author meenalw
	 * @since v1.0.0
	 *//*
    public MenuItem findMenuItemByTextKey(final String textKey,
                                          final String locale) {
		Search search = new Search();
		search.addFilterEqual("textKey", textKey);
		search.addFilterEqual("locale", locale);
		MenuItem menuItem = this.searchUnique(search);
		return menuItem;
	}

	*//**
	 * List.
	 *
	 * @param locale the locale
	 * @return the list< menu item>
	 * @author meenalw
	 * @since v1.0.0
	 *//*
	public List<MenuItem> findAllByLocale(final String locale) {
		Search search = new Search();
		search.addFilterEqual("locale", locale);
		return this.search(search);
	}

	 *//**
     * Find all records sorted.
     *
     * @param property the property
     * @param locale the locale
     * @param desc the desc
     * @return the list
     * @author sujitas
     * @since v1.0.0
     *//*
	 public List<MenuItem> findAllSorted(final String property,
                                 final String locale,
                                 final boolean desc) {
        final Search search = new Search();
        search.addSort(property, desc);
        search.addFilterEqual("locale", locale);
        final List<MenuItem> records = this.search(search);
        return records;
    }
}
*/


package org.mkcl.els.repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.mkcl.els.domain.MenuItem;
import org.springframework.stereotype.Repository;

import com.trg.search.Search;

/**
 * The Class MenuItemRepository.
 *
 * @author amitd
 * @author sandeeps
 * @since v1.0.0
 */
@Repository
public class MenuItemRepository extends BaseRepository<MenuItem, Long> {

	/**
	 * Find menu items by parent.
	 *
	 * @param parent the parent
	 * @return the list
	 */
	public List<MenuItem> findMenuItemsByParent(final MenuItem parent) {
		StringBuffer buffer= new StringBuffer();
		List<MenuItem> menus = new  ArrayList<MenuItem>();
		try{
			buffer.append("SELECT m FROM MenuItem m ");
			if (parent == null) {
				buffer.append("WHERE m.parent:parent");
			} else {
				buffer.append("WHERE m.parent=:parent ORDER BY m.position");
			}
			TypedQuery<MenuItem> query = this.em().createQuery(buffer.toString(), MenuItem.class);
			if (parent == null) {
				query.setParameter("parent", null);
			} else {
				query.setParameter("parent", parent);
			}
			menus = query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return menus;
	}

	@SuppressWarnings("unchecked")
	public List<MenuItem> findByParents(final String parentIdsDelimitedBYComma,
			final String locale) {
		
		List<MenuItem> menuItemsA = new ArrayList<MenuItem>();
		
		try {
			String strquery=null;
			if(parentIdsDelimitedBYComma.isEmpty()){
				strquery="SELECT m FROM MenuItem m WHERE m.parent=null and m.locale=:locale ORDER BY m.text";	
			}else{
				strquery="SELECT m FROM MenuItem m JOIN m.parent p WHERE p.id IN :parentIdsDelimitedBYComma and m.locale=:locale ORDER BY m.text";
			}	
			Query query=this.em().createQuery(strquery);
			query.setParameter("locale", locale);
			List<Long> menuItems=new ArrayList<Long>();
			if(!parentIdsDelimitedBYComma.isEmpty()){
				String[] menuIds=parentIdsDelimitedBYComma.split(",");
				for(int i=0;i<menuIds.length;i++){
					menuItems.add(Long.parseLong(menuIds[i]));
				}
				query.setParameter("parentIdsDelimitedBYComma", menuItems);
			}
			menuItemsA = query.getResultList();
		} catch (NumberFormatException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		
		return menuItemsA;
	}

	@SuppressWarnings("unchecked")
	public List<MenuItem> findAllByIds(final String menuItemIds,final String sortBy,final String sortOrder,final String locale) {
		
		List<MenuItem> menuItemsA = new ArrayList<MenuItem>();
		try{
			String strquery="SELECT m FROM MenuItem m WHERE m.id IN:menuItemIds ORDER BY "+sortBy+" "+sortOrder;
			Query query=this.em().createQuery(strquery);
			List<Long> menuItems=new ArrayList<Long>();
			String[] menuIds=menuItemIds.split(",");
			for(int i=0;i<menuIds.length;i++){
				menuItems.add(Long.parseLong(menuIds[i]));
			}
			query.setParameter("menuItemIds", menuItems);
			
			menuItemsA = query.getResultList();
		}catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return menuItemsA;
	}

	public MenuItem findByTextKeyAndText(final String textKey, final String text, final String locale) {
		MenuItem menuItem = null;
		
		String strQuery = "SELECT m FROM MenuItem m WHERE m.textKey=:textKey AND m.text=:text AND m.locale=:locale";
		TypedQuery<MenuItem> query = this.em().createQuery(strQuery, MenuItem.class);
		query.setParameter("textKey", textKey);
		query.setParameter("text", text);
		query.setParameter("locale", locale);
		
		try {
			menuItem = query.getSingleResult();
		} catch(NoResultException nre) {
			logger.warn("No MenuItem found for textKey: " + textKey + " with text: " + text);
			System.out.println("No MenuItem found for textKey: " + textKey + " with text: " + text);
			return menuItem;
		}		
		
		return menuItem;
	}
	
}
