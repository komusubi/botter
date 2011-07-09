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
package jp.dip.komusubi.botter.gae.module.dao;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import javax.inject.Provider;
import javax.jdo.PersistenceManager;

import jp.dip.komusubi.botter.gae.GaeContext;
import jp.dip.komusubi.botter.gae.GaeContextFactory;
import jp.dip.komusubi.botter.gae.GaeLocalDatastoreResource;
import jp.dip.komusubi.botter.gae.model.airline.Airport;
import jp.dip.komusubi.botter.gae.module.PersistenceManagerProvider;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Query;
import com.google.inject.Binder;
import com.google.inject.Module;


/**
 * @author jun.ozeki
 * @version $Id$
 * @since 2011/05/25
 */
public class JdoAirportDaoTest {
	@Rule
	public GaeLocalDatastoreResource resource = new GaeLocalDatastoreResource();
	private JdoAirportDao target;
	private Provider<PersistenceManager> provider;
	
	@BeforeClass 
	public static void beforeClass() {
		GaeContextFactory.initializeContext(new Module() {
			
			@Override
			public void configure(Binder binder) {
				binder.bind(JdoAirportDao.class);
			}
			
		}, new GaeContextFactory.PersistenceModule());
	}
	
	@Before
	public void before() {
		provider = GaeContext.CONTEXT.getInstance(PersistenceManagerProvider.class);
		target = GaeContext.CONTEXT.getInstance(JdoAirportDao.class);
	}
	
	@After
	public void after() {
		provider.get().close();
	}
	
	@Test
	public void create() {
		List<Airport> airports = Arrays.asList(
					new Airport("OKA", "沖縄那覇"),
					new Airport("ITM", "大阪伊丹"),
					new Airport("HND", "東京羽田"),
					new Airport("NRT", "東京成田"));
		for (Airport airport: airports)
			target.create(airport);
		
		Query query = new Query(Airport.class.getSimpleName());
		assertEquals(4, DatastoreServiceFactory.getDatastoreService().prepare(query).countEntities());
	}
	
	@Test
	public void readByCode() {
		create();
		Airport airport = target.readByCode("ITM");
		assertEquals("ITM", airport.getCode());
		assertEquals("大阪伊丹", airport.getName());
	}
}
