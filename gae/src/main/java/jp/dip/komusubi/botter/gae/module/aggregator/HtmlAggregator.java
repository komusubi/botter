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
package jp.dip.komusubi.botter.gae.module.aggregator;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import jp.dip.komusubi.botter.gae.model.Aggregator;
import jp.dip.komusubi.botter.gae.model.Entry;
import jp.dip.komusubi.botter.gae.model.Scraper;


/**
 * @author jun.ozeki
 * @version $Id: HtmlAggregator.java 1356 2010-12-31 05:13:01Z jun $
 * @since 2010/08/13
 */
public class HtmlAggregator implements Aggregator {

	private List<Scraper> scrapers;
	
	/**
	 * constructor.
	 */
	public HtmlAggregator(List<Scraper> scrapers) {
		this.scrapers = scrapers;
	}
	
	/**
	 * constructor.
	 * @param scraper
	 */
	public HtmlAggregator(Scraper... scraper) {
		this(new ArrayList<Scraper>());
		scrapers.addAll(scrapers);
	}
	
	public void add(Scraper scraper) {
		scrapers.add(scraper);
	}
	
	@Override
	public List<Entry> aggregate() {
		List<Entry> entries = new ArrayList<Entry>();
		for (Scraper scraper: scrapers) {
			for (Entry entry: scraper.scrape()) {
				entries.add(entry);
			}
		}
		return entries;
	}
}
