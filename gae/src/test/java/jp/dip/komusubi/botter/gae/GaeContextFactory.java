/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.    
 */
package jp.dip.komusubi.botter.gae;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;
import java.util.Enumeration;
import java.util.Set;

import javax.inject.Provider;
import javax.inject.Singleton;
import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import jp.dip.komusubi.botter.api.Resolver;
import jp.dip.komusubi.botter.api.UrlUtil;
import jp.dip.komusubi.botter.gae.GaeContext.ResolverManager;

import org.apache.commons.lang.time.DateUtils;

import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * @author jun.ozeki
 * @version $Id$
 * @since 2011/03/21
 */
public class GaeContextFactory {
	private static Class<?> testCaseClass;
	
	public static GaeContext getMockContext(Injector injector, ServletContext servletContext) {
		GaeContext.CONTEXT.setInjector(injector);
		GaeContext.CONTEXT.setServletContext(servletContext);
		return GaeContext.CONTEXT;
	}
	public static void initializeContext() {
		initializeContext(null);
	}
	public static void initializeContext(Class<?> clazz) {
		if (clazz != null)
			testCaseClass = clazz;
		GaeContext.CONTEXT.setInjector(getDefaultInjector());
		GaeContext.CONTEXT.setServletContext(getDefaultServletContext());
	}

	/**
	 * @return
	 */
	private static Injector getDefaultInjector() {
		return Guice.createInjector(new Module() {

			@Override
			public void configure(Binder binder) {
				binder.bind(ResolverManager.class).toProvider(MockResolverManagerProvider.class).in(Singleton.class);
			}
		});
	}

	public static class MockResolverManagerProvider implements Provider<ResolverManager> {
		@Override
		public ResolverManager get() {
			return new ResolverManager() {

				@Override
				public Resolver<Date> getDateResolver() {
					return new Resolver<Date>() {

						@Override
						public Date resolve() {
							try {
								return DateUtils.parseDate("2011/03/24 22:36:00", new String[]{"yyyy/MM/dd HH:mm:ss"});
							} catch (ParseException e) {
								return new Date();
							}
						}
						
					};
				}

				@Override
				public UrlUtil getUrlUtil() {
					return new UrlUtil() {

						@Override
						public String shorten(String url) {
							return url;
						}

						@Override
						public String shorten(URL url) {
							return url.toString();
						}
						
					};
				}
				
			};
		}
	}
	/**
	 * @return
	 */
	private static ServletContext getDefaultServletContext() {
		return new ServletContext() {

			@Override
			public Object getAttribute(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Enumeration getAttributeNames() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public ServletContext getContext(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getContextPath() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getInitParameter(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Enumeration getInitParameterNames() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public int getMajorVersion() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public String getMimeType(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public int getMinorVersion() {
				// TODO Auto-generated method stub
				return 0;
			}

			@Override
			public RequestDispatcher getNamedDispatcher(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getRealPath(String arg0) {
				String filename = arg0.replace("/WEB-INF", "");
				String path = "target/test-classes/";
				if (testCaseClass != null)
					path += testCaseClass.getPackage().getName().replace(".", "/");
				else
					path += filename;
				return path;
			}

			@Override
			public RequestDispatcher getRequestDispatcher(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public URL getResource(String arg0) throws MalformedURLException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public InputStream getResourceAsStream(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Set getResourcePaths(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getServerInfo() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Servlet getServlet(String arg0) throws ServletException {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public String getServletContextName() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Enumeration getServletNames() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public Enumeration getServlets() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public void log(String arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void log(Exception arg0, String arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void log(String arg0, Throwable arg1) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void removeAttribute(String arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void setAttribute(String arg0, Object arg1) {
				// TODO Auto-generated method stub
				
			}
			
		};
	}
}
