package org.mkcl.els;

import static org.junit.Assert.*;
import junit.framework.Assert;

import org.junit.Test;
import org.mkcl.els.domain.BaseDomain;
import org.mkcl.els.domain.Language;
import org.springframework.transaction.annotation.Transactional;

import antlr.collections.List;

public class LanguageTest extends AbstractTest {

	@Test
	@Transactional
	public void testPersist() {
		Language language=new Language("testLanguage");
		language.persist();
	    Assert.assertNotNull("Saved Lanaguage Data ", language);
	}

	@Test
	@Transactional
	public void testMerge() {
		Language language=new Language("testLanguage");
		language.persist();
		language.setName("New Language");
		language.merge();
	    Assert.assertNotNull("updated Language Data ", language);

	}

	@Test
	@Transactional
	public void testRemove() {
		Language language=new Language("testLanguage");
		language.persist();
		language.remove();
	    Assert.assertNotNull("Deleting Language Data ", language);

	}

	@Test
	@Transactional
	public void testFindById() {
		Language language=new Language("testLanguage");
		language.persist();
		Language language1=Language.findById(Language.class,language.getId());
	    Assert.assertNotNull("Finding Language Data  by Id", language1);
	}

	@Test
	@Transactional
	public void testFindByName() {
		Language language=new Language("testLanguage");
		language.persist();
		Language language1=Language.findByName(Language.class,language.getName(),language.getLocale());
	    Assert.assertNotNull("Finding Language Data  by Name", language1);
	}

	@Test
	@Transactional
	public void testFindAll() {
		Language language=new Language("testLanguage");
		language.persist();
	    java.util.List<Language> languages = Language.findAll(Language.class,"name", "desc", "en");
	    Assert.assertNotNull("Finding Language Data  ", languages);

	}

}
