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
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.inject.Provider;
import javax.jdo.PersistenceManager;

import jp.dip.komusubi.botter.gae.GaeContext;
import jp.dip.komusubi.botter.gae.GaeContextFactory;
import jp.dip.komusubi.botter.gae.GaeLocalDatastoreResource;
import jp.dip.komusubi.botter.gae.model.airline.Airport;
import jp.dip.komusubi.botter.gae.model.airline.Route;
import jp.dip.komusubi.botter.gae.module.PersistenceManagerProvider;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Query;
import com.google.inject.Binder;
import com.google.inject.Module;


/**
 * @author jun.ozeki
 * @version $Id$
 * @since 2011/05/29
 */
public class JdoRouteDaoTest {
	@Rule
	public GaeLocalDatastoreResource resource = new GaeLocalDatastoreResource();
	private JdoRouteDao target;
	private JdoAirportDao airportDao;
	private Provider<PersistenceManager> provider;
	
	@BeforeClass
	public static void beforeClass() {
		GaeContextFactory.initializeContext(new Module() {
			
			@Override
			public void configure(Binder binder) {
				binder.bind(JdoRouteDao.class);
				binder.bind(JdoAirportDao.class);
			}
			
		}, new GaeContextFactory.PersistenceModule());
	}
	
	@Before
	public void before() {
		provider = GaeContext.CONTEXT.getInstance(PersistenceManagerProvider.class);
		airportDao = GaeContext.CONTEXT.getInstance(JdoAirportDao.class);
		target = GaeContext.CONTEXT.getInstance(JdoRouteDao.class);
		init();
	}
	@After
	public void after() {
		provider.get().close();
	}
	private Airport hnd, nrt, itm, oka, fuk, spk;
	private List<Airport> airports;
	private void init() {
		hnd = new Airport("HND", "東京羽田");
		nrt = new Airport("NRT", "東京成田");
		itm = new Airport("ITM", "大阪伊丹");
		oka = new Airport("OKA", "沖縄那覇");
		fuk = new Airport("FUK", "福岡");
		spk = new Airport("SPK", "札幌");
		airports = Arrays.asList(hnd, nrt, itm, oka, fuk, spk);
	}
	
	@Test
	public void create() {
		airportDao.create(hnd);
		Route route = new Route(hnd, itm);
		route.setActivate(true).setTimestamp(new Date());
		target.create(route);

		Query query = new Query(Route.class.getSimpleName());
		assertEquals(1, DatastoreServiceFactory.getDatastoreService().prepare(query).countEntities());
	}
	
	@Test
	public void read() {
		for (Airport airport: airports)
			airportDao.create(airport);
		
		Route route = new Route(hnd, oka);
		Key key = target.create(route);
		System.out.println("key is " + key);
		Route result = target.read(key);

		assertEquals(hnd, result.getDeparture());
	}
	
	// find by activate
	@Test
	public void findByActivate() {
		Route route = new Route(hnd, itm);
		target.create(route);
		
		List<Route> routes = target.findByActivate(true);
		assertEquals(1, routes.size());
	}
	
	// read by airport code
	@Test
	public void readByAirportCode() {
		Route route = new Route(hnd, oka);
		target.create(route);
		
		assertEquals(route, target.readByAirportCode("HND", "OKA"));
		assertNull(target.readByAirportCode("HND", "HND"));
	}
	
	@Test
	public void readByAirportCodeBool() {
		Route hnd_itm = new Route(hnd, itm);
		hnd_itm.setActivate(false).setTimestamp(new Date());
		target.create(hnd_itm);
		
		Route spk_oka = new Route(spk, oka);
		target.create(spk_oka);
		
		assertNull(target.readByAirportCode("HND", "ITM", true));
		assertEquals(hnd_itm, target.readByAirportCode("HND", "ITM", false));
		assertEquals(spk_oka, target.readByAirportCode("SPK", "OKA", true));
	}
}
