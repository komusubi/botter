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

import javax.inject.Singleton;
import javax.jdo.JDOHelper;
import javax.jdo.PersistenceManager;
import javax.jdo.PersistenceManagerFactory;
import javax.servlet.ServletContextEvent;

import jp.dip.komusubi.botter.Bird;
import jp.dip.komusubi.botter.gae.GaeContext.ResolverManager;
import jp.dip.komusubi.botter.gae.GaeContext.ResolverManagerProvider;
import jp.dip.komusubi.botter.gae.module.PersistenceManagerProvider;
import jp.dip.komusubi.botter.gae.module.ConsoleBird;
import jp.dip.komusubi.botter.gae.service.Jal5971Resource;
import jp.dip.komusubi.botter.gae.servlet.filter.PersistenceFilter;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.servlet.GuiceServletContextListener;
import com.google.inject.servlet.ServletModule;
import com.google.inject.servlet.ServletScopes;
import com.sun.jersey.guice.spi.container.servlet.GuiceContainer;

/**
 * @author jun.ozeki
 * @version $Id: Bootstrap.java 1356 2010-12-31 05:13:01Z jun $
 * @since 2010/12/05
 */
public final class Bootstrap extends GuiceServletContextListener {
	private Injector injector;
	
	@Override 
	protected Injector getInjector() {
		return injector = buildInjector();
	}
	
	@Override
	public void contextInitialized(ServletContextEvent servletContextEvent) { 
		super.contextInitialized(servletContextEvent);
		GaeContext.CONTEXT.setInjector(injector);
		GaeContext.CONTEXT.setServletContext(servletContextEvent.getServletContext());
	}
	
	public Injector buildInjector() {
		Injector injector = Guice.createInjector(
				new AppEngineModule(),
				new PersistenceModule(),
				new WebModule());
		return injector;
	}

	/**
	 * 
	 * @author jun.ozeki
	 * @version $Id: Bootstrap.java 1356 2010-12-31 05:13:01Z jun $
	 * @since 2010/12/30
	 */
	private static class WebModule extends ServletModule {
		@Override
		protected void configureServlets() {
			bind(Bird.class).to(ConsoleBird.class);
			bind(Jal5971Resource.class);
			filter("/*").through(PersistenceFilter.class);
			serve("/*").with(GuiceContainer.class);
		}
	}
	
	/**
	 * 
	 * @author jun.ozeki
	 * @version $Id: Bootstrap.java 1356 2010-12-31 05:13:01Z jun $
	 * @since 2010/12/26
	 */
	private static class PersistenceModule extends AbstractModule {

		@Override
		protected void configure() {
			bind(PersistenceManager.class).toProvider(PersistenceManagerProvider.class).in(ServletScopes.REQUEST);
			bind(ResolverManager.class).toProvider(ResolverManagerProvider.class).in(Singleton.class);
		}
	}
	
	/**
	 * 
	 * @author jun.ozeki
	 * @version $Id: Bootstrap.java 1356 2010-12-31 05:13:01Z jun $
	 * @since 2010/12/26
	 */
	private static class AppEngineModule extends AbstractModule {
		private PersistenceManagerFactory pmf = JDOHelper.getPersistenceManagerFactory("transactions-optional");
		
		@Override
		protected void configure() {

		}

		@Provides
		public PersistenceManagerFactory getPersistenceManagerFactory() {
			return pmf ;
		}
	}
}
