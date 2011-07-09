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

import static jp.dip.komusubi.botter.gae.TestUtils.parseDate;
import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.List;

import javax.inject.Provider;
import javax.jdo.PersistenceManager;

import jp.dip.komusubi.botter.gae.GaeContext;
import jp.dip.komusubi.botter.gae.GaeContextFactory;
import jp.dip.komusubi.botter.gae.GaeLocalDatastoreResource;
import jp.dip.komusubi.botter.gae.model.airline.Airport;
import jp.dip.komusubi.botter.gae.model.airline.FlightStatus;
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
 * @since 2011/05/22
 */
public class JdoFlightStatusDaoTest {
	@Rule
	public GaeLocalDatastoreResource env = new GaeLocalDatastoreResource();
	
	private JdoFlightStatusDao target;
	private JdoRouteDao routeDao;
	private Provider<PersistenceManager> provider;

	@BeforeClass
	public static void beforeClass() {
		GaeContextFactory.initializeContext(new Module() {
			@Override
			public void configure(Binder binder) {
				binder.bind(JdoFlightStatusDao.class);
				binder.bind(JdoRouteDao.class);
			}
		}, new GaeContextFactory.PersistenceModule());
	}
	
	@Before
	public void before() {
		target = GaeContext.CONTEXT.getInstance(JdoFlightStatusDao.class);
		routeDao = GaeContext.CONTEXT.getInstance(JdoRouteDao.class);
		provider = GaeContext.CONTEXT.getInstance(PersistenceManagerProvider.class);
		provider.get();
		
	}
	
	@After
	public void after() {
		provider.get().close();
	}
	
	@Test
	public void create() { 
		FlightStatus fs = new FlightStatus();
		fs.setArrivalDate(new Date())
			.setDelay(false)
			.setDepartureDate(new Date())
			.setFlightName("JAL5142")
			.setGate("10")
			.setRoute(new Route(new Airport("HND", "東京羽田"), new Airport("ITM", "大阪伊丹")))
			.setScheduledArrivalDate(new Date())
			.setScheduledDepartureDate(new Date());
			Long id = target.create(fs);
			assertEquals(1L, id.longValue());
			
			Query query = new Query(FlightStatus.class.getSimpleName());
			assertEquals(1, DatastoreServiceFactory.getDatastoreService().prepare(query).countEntities());
	}
	
	@Test
	public void readAll() {
		create();
		assertEquals(1, target.findAll().size());
		for (FlightStatus fs:target.findAll()) {
//			assertEquals(new Date(), fs.getDepartureDate());
			assertEquals("JAL5142", fs.getFlightName());
			assertEquals("10", fs.getGate());
			// TODO Airport Key objectの比較をどうする？ 
//			assertEquals(new Airport("HND", "東京羽田"), fs.getRoute().getDeparture());
//			assertEquals(new Airport("ITM", "大阪伊丹"), fs.getRoute().getArrival());
		}
	}
	
	// 事前データ作成用
	private void create(FlightStatus... flights) {
		for (FlightStatus fs: flights)
			target.create(fs);
	}
	
	@Test
	public void findBy出発前路線() throws Exception {
		Route route1 = new Route(new Airport("HND", "東京羽田"), new Airport("ITM", "大阪伊丹"));
//		Route route2 = new Route(new Airport("HND", "東京羽田"), new Airport("OKA", "沖縄那覇"));
		Key routeKey1 = routeDao.create(route1);
//		Key routeKey2 = routeDao.create(route2);

		FlightStatus fs1 = new FlightStatus();
		fs1.setScheduledDepartureDate(parseDate("2011/06/03 13:00"))
			.setScheduledArrivalDate(parseDate("2011/06/03 15:10"))
			.setRoute(route1)
			.setFlightName("JAL432")
			.setGate("14")
			.setDepartureDate(parseDate("2011/06/03 13:13"));
		FlightStatus fs2 = new FlightStatus();
		fs2.setScheduledDepartureDate(parseDate("2011/06/03 14:05"))
		.setScheduledArrivalDate(parseDate("2011/06/03 15:30"))
		.setRoute(route1)
		.setFlightName("JAL433")
		.setGate("15");

		FlightStatus fs3 = new FlightStatus();
		fs3.setScheduledArrivalDate(parseDate("2011/06/03 14:30"))
			.setScheduledArrivalDate(parseDate("2011/06/03 16:10"))
			.setRoute(route1)
			.setFlightName("JAL564")
			.setGate("20");
		
		create(fs1, fs2, fs3);

		List<FlightStatus> flights = target.findByBeforeDeparture(route1);
		assertEquals(2, flights.size());
		assertEquals(fs2, flights.get(0));
		assertEquals(fs3, flights.get(1));
	}

	@Test
	public void findBy到着前路線() throws Exception {
		Route route = new Route(new Airport("SPK", "札幌"), new Airport("OKA", "沖縄那覇"));
		routeDao.create(route);
		
		FlightStatus fs1 = new FlightStatus();
		fs1.setScheduledDepartureDate(parseDate("2011/06/03 9:00"))
			.setScheduledArrivalDate(parseDate("2011/06/03 11:20"))
			.setRoute(route)
			.setFlightName("JAL587")
			.setGate("11")
			.setDepartureDate(parseDate("2011/06/03 9:03"))
			.setArrivalDate(parseDate("2011/06/03 11:29"));
		FlightStatus fs2 = new FlightStatus();
		fs2.setScheduledDepartureDate(parseDate("2011/06/03 10:10"))
			.setScheduledArrivalDate(parseDate("2011/06/03 12:30"))
			.setRoute(route)
			.setFlightName("JAL248")
			.setGate("36")
			.setDepartureDate(parseDate("2011/06/03 10:14"));
		
		create(fs1, fs2);
		
		List<FlightStatus> flights = target.findByBeforeArrival(route);
		// 到着前の便は1便
		assertEquals(1, flights.size());
		// 到着前の便はfs2 と同値
		assertEquals(fs2, flights.get(0));
	}
}
