/*
 ******************************************************************
File: org.mkcl.els.common.util.DatabaseDrivenMessageSource.java
Copyright (c) 2011, vishals, MKCL
All rights reserved.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE
COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT,
INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN
ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.

 ******************************************************************
 */
package org.mkcl.els.common.util;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.mkcl.els.domain.MessageResource;
import org.mkcl.els.service.IMessageResourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

/**
 * The Class DatabaseDrivenMessageSource.
 *
 * @author vishals
 * @version v1.0.0
 */
public class DatabaseDrivenMessageSource extends ReloadableResourceBundleMessageSource{

	/** The Constant logger. */
	private static final Logger logger = LoggerFactory.getLogger(DatabaseDrivenMessageSource.class);

	/** The message resource service. */
	@Autowired
	private IMessageResourceService messageResourceService;

	/** The properties. */
	private final Map<String, String> properties = new HashMap<String, String>();


	/**
	 * Instantiates a new database driven message source.
	 
	public DatabaseDrivenMessageSource(){
		//reload();
	}*/

	/**
	 * After properties set.
	 *
	 * @throws Exception the exception
	 */
	public void afterPropertiesSet() throws Exception {
		if (messageResourceService == null) {
			throw new BeanInitializationException(
			"MessageResourceService should be filled");
		}
	}

	/* (non-Javadoc)
	 * @see org.springframework.context.support.AbstractMessageSource#resolveCode(java.lang.String, java.util.Locale)
	 */
	@Override
	protected MessageFormat resolveCode(String code, Locale locale) {
		String message = getText(code, locale);
		MessageFormat format = createMessageFormat(message, locale);
		return format;
	}

	protected MessageFormat createMessageFormat(String message, Locale locale) {
		return new MessageFormat(message,locale);
	}

	/* (non-Javadoc)
	 * @see org.springframework.context.support.ReloadableResourceBundleMessageSource#resolveCodeWithoutArguments(java.lang.String, java.util.Locale)
	 */
	@Override
	protected String resolveCodeWithoutArguments(String code, Locale locale) {
		if(properties.size()==0){
			reload();
		}
		return getText(code, locale);
	}

	/**
	 * Reload.
	 */
	public void reload() {
		logger.info("Caache reloaded");
		properties.clear();
		properties.putAll(loadTexts());
	}

	/**
	 * Gets the text.
	 *
	 * @param code the code
	 * @param locale the locale
	 * @return the text
	 */
	private String getText(String code, Locale locale) {
		String key = locale.toString() + "_" + code;
		String textForCurrentLanguage = properties.get(key);
		if (textForCurrentLanguage == null) {
			textForCurrentLanguage = properties.get(Locale.ENGLISH.toString() + "_" + code);
		}
		//return textForCurrentLanguage != null ? textForCurrentLanguage : code;
		return textForCurrentLanguage;
	}

	protected Map<String, String> loadTexts() {
		Map<String, String> m = new HashMap<String, String>();
		List<MessageResource> messages = messageResourceService.findAll();
		for(MessageResource message: messages) {
			if(message.getValue()!=null){
				m.put(message.getLocale().toString() + "_" + message.getCode(), message.getValue());
			}
		}
		return m;
	}

}
