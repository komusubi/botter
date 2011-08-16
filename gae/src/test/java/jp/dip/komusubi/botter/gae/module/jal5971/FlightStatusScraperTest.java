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
package jp.dip.komusubi.botter.gae.module.jal5971;

import static jp.dip.komusubi.botter.gae.TestUtils.getFileContent;
import static jp.dip.komusubi.botter.gae.TestUtils.parseDate;
import static org.junit.Assert.assertEquals;

import java.net.URL;
import java.util.Date;
import java.util.List;

import jp.dip.komusubi.botter.gae.GaeContext;
import jp.dip.komusubi.botter.gae.GaeContext.ResolverManager;
import jp.dip.komusubi.botter.gae.GaeContextFactory;
import jp.dip.komusubi.botter.gae.GaeContextFactory.MockResolverManagerProvider;
import jp.dip.komusubi.botter.gae.GaeLocalDatastoreResource;
import jp.dip.komusubi.botter.gae.model.Entry;
import jp.dip.komusubi.botter.gae.model.airline.Airport;
import jp.dip.komusubi.botter.gae.model.airline.AirportDao;
import jp.dip.komusubi.botter.gae.model.airline.Route;
import jp.dip.komusubi.botter.gae.model.airline.RouteDao;
import jp.dip.komusubi.botter.gae.module.dao.JdoAirportDao;
import jp.dip.komusubi.botter.gae.module.dao.JdoRouteDao;
import junitx.util.PrivateAccessor;

import org.htmlparser.Node;
import org.htmlparser.util.NodeList;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;


/**
 * @author jun.ozeki
 * @version $Id$
 * @since 2011/05/18
 */
public class FlightStatusScraperTest {
	@Rule
	public GaeLocalDatastoreResource env = new GaeLocalDatastoreResource();
	@Rule 
	public ExpectedException ex = ExpectedException.none();
	private FlightStatusScraper target;
	private String dateStr;
	private static GaeContextFactory.PersistenceModule persistenceModule;
	
	@BeforeClass
	public static void befoerClass() {
		persistenceModule = new GaeContextFactory.PersistenceModule();
	}
	@Before
	public void before() {
	}
	
	public void scenario() {
		GaeContextFactory.initializeContext(new AbstractModule() {
		
			@Override
			public void configure() {
				bind(AirportDao.class).to(JdoAirportDao.class);
				bind(RouteDao.class).to(JdoRouteDao.class);
				bind(String.class)
					.annotatedWith(Names.named("timestamp")).toInstance(dateStr);
				bind(ResolverManager.class).toProvider(MockResolverManagerProvider.class);
			}
		}, persistenceModule);
		
		Airport[] airports = {new Airport("HND", "東京羽田"), new Airport("ITM", "大阪伊丹")};
		AirportDao airportDao = GaeContext.CONTEXT.getInstance(AirportDao.class);
		for (Airport airport: airports) 
			airportDao.create(airport);
				
		Route route = new Route(airports[0], airports[1]);
		RouteDao routeDao = GaeContext.CONTEXT.getInstance(RouteDao.class);
		routeDao.create(route);
		
		target = new FlightStatusScraper(route) {
			@Override
			protected String buildUrl(String baseUrl) {
				return baseUrl;
			}
			@Override
			protected Entry newEntry(NodeList header, final NodeList column, String... tags) {
//				System.out.printf("column is \"%s\"\n", column.toHtml(true));
				return new Entry() {
					private NodeList line = column; 
					@Override
					public long getId() {
						// TODO Auto-generated method stub
						return 0;
					}

					@Override
					public String getText() {
						StringBuilder builder = new StringBuilder();
						for (Node n: line.toNodeArray()) {
//							String s = n.getText();
							builder.append("".equals(n.getText()) ? "" : n.getText().trim());
							builder.append(",");
						}
//						return line.asString();
						return builder.toString();
					}

					@Override
					public Date getDate() {
						// TODO Auto-generated method stub
						return null;
					}

					@Override
					public URL getUrl() {
						// TODO Auto-generated method stub
						return null;
					}
					
				};
			}
		};
	}
	
	
	@After
	public void after() {
		GaeContextFactory.clean();
	}

	@Test
	public void scrape() throws Throwable {
		dateStr = "2011/08/11 23:00:00";
		scenario();
		PrivateAccessor.setField(target, "url", 
				getFileContent("hnd-oka.html", this));
		List<Entry> entries = target.scrape();
		// url を元に戻す。
		PrivateAccessor.setField(target, "url", "http://yahoo.co.jp");
		String[] lines = { "JAL901,&nbsp;,06:20,06:27　,出発済み,9,09:00,09:03　,到着済み,",
							  "JAL903,&nbsp;,08:00,08:04　,出発済み,4,10:40,10:27　,到着済み,",
							  "JAL905,&nbsp;,08:25,08:27　,出発済み,9,11:05,10:56　,到着済み,",
							  "JAL907,&nbsp;,09:55,09:58　,出発済み,2,12:35,12:35　,到着済み,",
							  "JAL909,&nbsp;,10:35,10:41　,出発済み,4,13:15,13:20　,到着済み,",
							  "JAL913,&nbsp;,11:35,11:39　,出発済み,11,14:15,14:16　,到着済み,",
							  "JAL915,&nbsp;,11:55,12:02　,出発済み,4,14:35,14:36　,到着済み,",
							  "JAL917,&nbsp;,13:15,13:29　,出発済み,4,15:55,16:11　,到着済み,",
							  "JAL919,&nbsp;,14:50,14:51　,出発済み,4,17:30,17:26　,到着済み,",
							  "JAL3947,*,15:20,--:--　−,18:00,--:--　−,欠航,",
							  "JAL923,&nbsp;,15:55,15:57　,出発済み,9,18:35,18:28　,到着済み,",
							  "JAL925,&nbsp;,17:25,17:36　,出発済み,2,20:05,20:07　,到着済み," };
		int i = 0;
		for (Entry e: entries) {
			assertEquals(lines[i++], e.getText());
		}
	}
	@Ignore
	@Test
	public void actualScrape() {
		target = new FlightStatusScraper(new Route("ITM", "ITM"));
		try {
			target.scrape();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void requestYesterday() throws Exception {
		dateStr = "2011/06/30 10:00:00";
		scenario();
		target = new FlightStatusScraper(new Route("HND", "ITM"), 
						parseDate("2011/06/29 10:00:00"), "");
		String url = "http://yahoo.co.jp";
		String expectedQuery = "?DPORT=HND&APORT=ITM&DATEFLG=1";
		assertEquals(url + expectedQuery, target.buildUrl(url)); 
	}
	@Test
	public void requestToday() throws Exception {
		dateStr = "2011/06/30 12:00:00";
		scenario();
		target = new FlightStatusScraper(new Route("ITM", "HND"),
				parseDate("2011/06/30 23:00:00"));
		String url = "http://yahoo.co.jp";
		String expectedQuery = "?DPORT=ITM&APORT=HND";
		assertEquals(url + expectedQuery, target.buildUrl(url));
	}
	@Test
	public void requestTomorrow() throws Exception {
		dateStr = "2011/06/30 13:00:00";
		scenario();
		target = new FlightStatusScraper(new Route("HND", "ITM"),
				parseDate("2011/07/01 11:00:00"));
		String url = "http://yahoo.co.jp";
		String expectedQuery = "?DPORT=HND&APORT=ITM&DATEFLG=2";
		assertEquals(url + expectedQuery, target.buildUrl(url));
	}
	@Test
	public void requestOutOfRange() throws Exception {
		ex.expect(IllegalStateException.class);
		ex.expectMessage("requested date: ");
		dateStr = "2011/06/30 20:00:00";
		scenario();
		target = new FlightStatusScraper(new Route("HND", "ITM"),
					parseDate("2011/07/03 20:00:00"));
		target.buildUrl("http://yahoo.co.jp");
	}
}
