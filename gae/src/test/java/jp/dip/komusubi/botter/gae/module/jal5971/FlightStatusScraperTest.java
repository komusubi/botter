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

import static jp.dip.komusubi.botter.gae.TestUtils.parseDate;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
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
				bind(ResolverManager.class).toProvider(MockResolverManagerProvider.class);
				bind(String.class)
					.annotatedWith(Names.named("timestamp")).toInstance(dateStr);
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
		};
	}
	
	@After
	public void after() {
		GaeContextFactory.clean();
	}
	@Ignore
	@Test
	public void scrape() throws Throwable {
		PrivateAccessor.setField(target, "url", getFileContent("hnd-oka.html"));
		List<Entry> entries = target.scrape();
		// url を元に戻す。
		PrivateAccessor.setField(target, "url", "http://yahoo.co.jp");
		for (Entry e: entries) {
			System.out.println("entry : " + e.getText());
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
	
	/**
	 * @param string
	 * @return
	 */
	private String getFileContent(String filename) {
		InputStream is = null;
		try {
			// ./ を付与してこのテストクラスと同一ディレクトリのファイルのURLを取得
			URL url = this.getClass().getResource("./" + filename);
			File file = new File(url.getPath());
			is = new FileInputStream(file);
			byte[] array = new byte[(int) file.length()];
			is.read(array, 0, (int) file.length());
			return new String(array, "Shift_JIS");
		} catch (IOException e) {
			// ignore
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (IOException ex) {
				// ignore
			}
		}
		return "";
	}
	
	
}
