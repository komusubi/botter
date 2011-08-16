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
package jp.dip.komusubi.botter.gae.module;

import static jp.dip.komusubi.botter.gae.TestUtils.parseDate;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.ws.rs.core.MultivaluedMap;

import jp.dip.komusubi.botter.Bird;
import jp.dip.komusubi.botter.Resolver;
import jp.dip.komusubi.botter.UrlUtil;
import jp.dip.komusubi.botter.gae.GaeContext;
import jp.dip.komusubi.botter.gae.GaeContext.ResolverManager;
import jp.dip.komusubi.botter.gae.GaeContextFactory;
import jp.dip.komusubi.botter.gae.GaeLocalDatastoreResource;
import jp.dip.komusubi.botter.gae.model.airline.Airport;
import jp.dip.komusubi.botter.gae.model.airline.AirportDao;
import jp.dip.komusubi.botter.gae.model.airline.FlightStatus;
import jp.dip.komusubi.botter.gae.model.airline.FlightStatusDao;
import jp.dip.komusubi.botter.gae.model.airline.Route;
import jp.dip.komusubi.botter.gae.model.airline.RouteDao;
import jp.dip.komusubi.botter.gae.module.dao.JdoAirportDao;
import jp.dip.komusubi.botter.gae.module.dao.JdoFlightStatusDao;
import jp.dip.komusubi.botter.gae.module.dao.JdoRouteDao;
import junitx.util.PrivateAccessor;

import org.apache.commons.lang3.time.DateUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.google.inject.AbstractModule;
import com.sun.jersey.core.util.MultivaluedMapImpl;

public class FlightStatusTwitterTest {
	@Rule
	public GaeLocalDatastoreResource env = new GaeLocalDatastoreResource();
	private static String now;
	private RouteDao routeDao;
	private AirportDao airportDao;
	private FlightStatusDao flightDao;
	private FlightStatusTwitter target;
	private static GaeContextFactory.PersistenceModule persisteceModule = 
			new GaeContextFactory.PersistenceModule();
	@Before
	public void before() {
		GaeContextFactory.initializeContext(new AbstractModule() {

			@Override
			protected void configure() {
				bind(FlightStatusDao.class).to(JdoFlightStatusDao.class);
				bind(Bird.class).to(ConsoleBird.class);
				bind(AirportDao.class).to(JdoAirportDao.class);
				bind(RouteDao.class).to(JdoRouteDao.class);
				bind(ResolverManager.class).toInstance(new LocalResolverManager());
			}
			
		}, persisteceModule);
		target = GaeContext.CONTEXT.getInstance(FlightStatusTwitter.class);
		routeDao = GaeContext.CONTEXT.getInstance(RouteDao.class);
		airportDao = GaeContext.CONTEXT.getInstance(AirportDao.class);
		flightDao = GaeContext.CONTEXT.getInstance(FlightStatusDao.class);
	}
	
	@After
	public void after() {
		GaeContextFactory.clean();
	}

	private static class LocalResolverManager implements ResolverManager {
		@Override
		public Resolver<Date> getDateResolver() {
			return new Resolver<Date>() {
				@Override
				public Date resolve() {
					try {
						return DateUtils.parseDate(now, new String[]{"yyyy/MM/dd HH:mm:ss"});
					} catch (ParseException e) {
						throw new IllegalStateException(e);
					}
				}
			};
		}
		@Override
		public UrlUtil getUrlUtil() {
			throw new IllegalStateException("this is unit test not implemented yet.");
		}
	}
	private void load() {
		Airport[] airports = {new Airport("HND", "東京羽田"), new Airport("ITM", "大阪伊丹")};
		for (Airport airport: airports) {
			airportDao.create(airport);
		}
		Route[] routes = {new Route(airports[0], airports[1]), 
							new Route(airports[1], airports[0])};
		for (Route route: routes) {
			routeDao.create(route);
		}
	}
	private void loadFlightStatus() throws Exception {
		load();
		FlightStatus fs1 = new FlightStatus();
		fs1.setScheduledDepartureDate(parseDate("2011/06/25 10:15:10"))
			.setScheduledArrivalDate(parseDate("2011/06/25 11:20:05"))
			.setDepartureDate(parseDate("2011/06/25 10:16:05"))
			.setArrivalDate(parseDate("2011/06/25 11:23:00"))
			.setRoute(routeDao.readByAirportCode("HND", "ITM"))
			.setFlightName("JAL121");
		FlightStatus fs2 = new FlightStatus();
		fs2.setScheduledDepartureDate(parseDate("2011/06/25 10:40:05"))
			.setScheduledArrivalDate(parseDate("2011/06/25 11:50:10"))
			.setRoute(routeDao.readByAirportCode("HND", "ITM"))
			.setFlightName("JAL125");
		for (FlightStatus f: Arrays.asList(fs1, fs2))
			flightDao.create(f);
	}
	
	@Test
	public void availableFalse() throws Exception {
		MultivaluedMap<String, String> params = new MultivaluedMapImpl();
		assertFalse(target.available(params));
	}
	
	@Test
	public void availableTrue() throws Exception {
		now = "2011/06/25 11:00:00";
		loadFlightStatus();
		MultivaluedMap<String, String> params = new MultivaluedMapImpl();
		assertTrue(target.available(params));
	}

	@Test
	public void execute() throws Throwable {
		now = "2011/06/30 10:00:00";
		loadFlightStatus();
		List<FlightStatus> flights = flightDao.findAll();
		PrivateAccessor.setField(target, "flights", flights);
		assertTrue(target.execute());
	}
}
