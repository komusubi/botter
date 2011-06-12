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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import jp.dip.komusubi.botter.gae.GaeContextFactory;
import jp.dip.komusubi.botter.gae.model.Entry;
import jp.dip.komusubi.botter.gae.model.airline.Airport;
import jp.dip.komusubi.botter.gae.model.airline.Route;
import jp.dip.komusubi.botter.gae.module.dao.JdoAirportDao;
import junitx.util.PrivateAccessor;

import org.junit.Before;
import org.junit.Test;

import com.google.inject.Binder;
import com.google.inject.Module;


/**
 * @author jun.ozeki
 * @version $Id$
 * @since 2011/05/18
 */
public class FlightStatusScraperTest {
	private FlightStatusScraper target;
	
	@Before
	public void before() {
		GaeContextFactory.initializeContext(new Module() {
			@Override
			public void configure(Binder binder) {
				binder.bind(JdoAirportDao.class);
			}
		}, new GaeContextFactory.PersistenceMoudle());
		
		Route route = new Route(new Airport("HND", "東京羽田"), new Airport("ITM", "大阪伊丹"));
//		route.setKey(new Key
		target = new FlightStatusScraper(route) {
			@Override
			protected String buildUrl(String baseUrl) {
				return baseUrl;
			}
		};
	}

//	private static String watchedLog;
//	@Rule
//	public MethodRule watchman = new TestWatchman() {
//		@Override
//		public void failed(Throwable e, FrameworkMethod method) {
//			watchedLog += method.getName() + " " + e.getClass().getSimpleName() + "\n";
//		}
//		@Override
//		public void succeeded(FrameworkMethod method) {
//			watchedLog += method.getName() + " " + "success!\n";
//		}
//	};
	
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
