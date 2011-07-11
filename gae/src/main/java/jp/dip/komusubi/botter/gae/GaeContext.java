/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jp.dip.komusubi.botter.gae;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.ResourceBundle;

import javax.inject.Provider;
import javax.servlet.ServletContext;

import jp.dip.komusubi.botter.BotterException;
import jp.dip.komusubi.botter.Resolver;
import jp.dip.komusubi.botter.UrlUtil;
import jp.dip.komusubi.botter.gae.module.dao.JdoAirportDao;
import jp.dip.komusubi.botter.gae.module.dao.JdoRouteDao;
import jp.dip.komusubi.botter.util.BitlyUrlUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Injector;

/**
 * 
 * @author jun.ozeki 
 * @since 2009/12/26
 * @version $Id: TwitteeContext.java 1357 2011-01-04 14:27:38Z jun $
 */
public enum GaeContext {
	CONTEXT;
	private static final Logger logger = LoggerFactory.getLogger(GaeContext.class);
	private ServletContext servletContext;
	private Properties properties;
	private ResourceBundle resourceBundle;
	private Injector injector;
//	public static final String TWEET_ACCOUNT_ID = "twitter.id";
//	public static final String TWEET_ACCOUNT_PASSWORD = "twitter.passowrd";
	public static final String BITLY_API_LOGIN_ID = "bit.ly.login.id";
	public static final String BITLY_API_KEY = "bit.ly.api.key";
	public static final String JAL5971_INFO_URL = "jal5971.info.url";
	public static final String JAL5971_INFO_HASHTAGS = "jal5971.info.hashtag";
	public static final String AIRPORT_DAO = "airportDao";
	public static final String ROUTE_DAO = "routeDao";
	private static boolean done;
	private HashMap<String, Class<?>> classmap;
	
	private void initialize() {
		if (done) {
			logger.warn("context already initialized.");
			return;
		}
		if (injector == null)
			throw new IllegalStateException("injector must NOT be null.");
		loadProperty();
		loadResourceBundle();
		loadClassmap();
	}
	private void loadClassmap() {
		 classmap = new HashMap<String, Class<?>>();
		 classmap.put(AIRPORT_DAO, JdoAirportDao.class);
		 classmap.put(ROUTE_DAO, JdoRouteDao.class);
	}
	private void loadProperty() {
		if (servletContext == null)
			throw new IllegalStateException("servletContext must NOT be null.");
		properties = new Properties();
		try {
			String configFilepath = servletContext.getRealPath("/WEB-INF/botter.xml");
			if (logger.isDebugEnabled())
				logger.debug("property file path: {}", configFilepath);
			properties.loadFromXML(new FileInputStream(configFilepath));
			done = true;
		} catch (IOException e) {
			logger.error("property file read error: {}", e.getLocalizedMessage());
			throw new BotterException(e);
		}
	}
	private void loadResourceBundle() {
		resourceBundle = ResourceBundle.getBundle("jp.dip.komusubi.botter.gae.messages");
/*		ResourceBundle.Control is restricted class!!
				new ResourceBundle.Control() {
					@Override
					public List<String> getFormats(String baseName) {
						if (baseName == null)
							throw new NullPointerException("baseName is null");
						return Arrays.asList("xml");
					}
					@Override
					public ResourceBundle newBundle(String baseName, Locale locale, String format,
							ClassLoader loader, boolean reload) throws IllegalAccessException,
							InstantiationException, IOException {
						if (baseName == null || locale == null || format == null || loader == null)
							throw new NullPointerException();
						ResourceBundle bundle = null;
						if (format.equals("xml")) {
							String bundleName = toBundleName(baseName, locale);
							String resourceName = toResourceName(bundleName, format);
							InputStream stream = null;
							if (reload) {
								URL url = loader.getResource(resourceName);
								if (url != null) {
									URLConnection connection = url.openConnection();
									if (connection != null) {
										// Disable caches to get fresh data for
										// reloading.
										connection.setUseCaches(false);
										stream = connection.getInputStream();
									}
								}
							} else {
								stream = loader.getResourceAsStream(resourceName);
							}
							if (stream != null) {
								BufferedInputStream bis = new BufferedInputStream(stream);
								bundle = new XmlResourceBundle(bis);
								bis.close();
							}
						}
						return bundle;
					}
				});
*/									
	}

/*
	private static class XmlResourceBundle extends ResourceBundle {
		private Properties props;

		public XmlResourceBundle(InputStream in) throws IOException {
			props = new Properties();
			props.loadFromXML(in);
		}
		@Override
		protected Object handleGetObject(String key) {
			return props.getProperty(key);
		}

		@Override
		public Enumeration<String> getKeys() {
			Set<String> keys = props.stringPropertyNames();
			return Collections.enumeration(keys);
		}
	}
*/
		
//	 getInstance
	public synchronized static GaeContext getInstance() {
		return CONTEXT;
	}

	// package private 
	void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
		initialize();
	}
	// getInstance
	@SuppressWarnings("unchecked")
	public <T> T getInstance(String name) {
		return (T) getInstance(classmap.get(name));
	}
	// getInstance
	@SuppressWarnings("unchecked")
	public <T> T getInstance(Class<T> type) {
		T instance = null;
		if (type != null && type.equals(GaeContext.class))
			instance = (T) CONTEXT;
		else 
			instance = injector.getInstance(type);
		return instance;
	}
	
	public ResolverManager getResolverManager() {
		Provider<ResolverManager> provider = injector.getProvider(ResolverManager.class);
		return provider.get();
	}
	
//	public PersistenceManagerFactory getPersistenceManagerFactory() {
//		return pmFactory;
//	}
	
	public String getProperty(String key) {
		return getProperty(key, null);
	}

	public String getProperty(String key, String aDefault) {
		return properties.getProperty(key, aDefault);
	}

	public static interface ResolverManager {
		Resolver<Date> getDateResolver();
		UrlUtil getUrlUtil();
	}
	
	public static class StaticResolverManager implements ResolverManager {
		private static final Resolver<Date> dateResolver = new Resolver<Date>() {
			@Override
			public Date resolve() {
				return new Date();
			}
		};
		public Resolver<Date> getDateResolver() {
			return dateResolver;
		}
		public UrlUtil getUrlUtil() {
			return new BitlyUrlUtil(CONTEXT.getProperty(BITLY_API_LOGIN_ID), CONTEXT.getProperty(BITLY_API_KEY));
		}
	}
	
	public static class ResolverManagerProvider implements Provider<ResolverManager> {
		private static final ResolverManager RESOLVER_MANAGER = new StaticResolverManager();
		@Override
		public ResolverManager get() {
			return RESOLVER_MANAGER;
		}
	}

	// package private 
	void setInjector(Injector injector) {
		this.injector = injector;
	}
	// message 
	public String getMessage(String key) {
		return resourceBundle.getString(key);
	}
}
