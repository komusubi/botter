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

import static jp.dip.komusubi.botter.gae.GaeContext.CONTEXT;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import jp.dip.komusubi.botter.BotterException;
import jp.dip.komusubi.botter.gae.ParseException;
import jp.dip.komusubi.botter.gae.model.Entry;
import jp.dip.komusubi.botter.gae.model.Scraper;

import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.tags.Div;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

/**
 * @author jun.ozeki
 * @version $Id: FlightOverviewScraper.java 1356 2010-12-31 05:13:01Z jun $
 * @since 2010/08/16
 */
public class FlightOverviewScraper implements Scraper {
	private static final String JAL5971_WEATHER_AND_STATUS_URL = "jal5971.weather.status.url";
	private Parser parser;
	private String url;
	private String[] tags;
	
	/**
	 * constructor.
	 */
	public FlightOverviewScraper() {
		this(CONTEXT.getProperty(JAL5971_WEATHER_AND_STATUS_URL));
	}

	/**
	 * constructor.
	 * @param url scrape html.
	 */
	public FlightOverviewScraper(String url) {
		try {
			new URL(url);
		} catch (MalformedURLException e) {
			throw new ParseException("weather and status url is invalid: <" + url + ">", e);
		}
		this.parser = new Parser();
		this.url = url;
	}
	
	/**
	 * constructor.
	 * @param url
	 * @param tags
	 */
	public FlightOverviewScraper(String url, String... tags) {
		this(url);
		this.tags = tags;
	}
	
	public FlightOverviewScraper(String... tags) {
		this();
		this.tags = tags;
	}
	@Override
	public List<Entry> scrape() {
		NodeClassFilter nodeFilter = new NodeClassFilter(Div.class);
		HasAttributeFilter attrFilter = new HasAttributeFilter();
		attrFilter.setAttributeName("class");
		attrFilter.setAttributeValue("weather_info_txtBox");
		AndFilter andFilter = new AndFilter(nodeFilter, attrFilter);
		
		try {
			parser.setResource(url);
			NodeList nodeList = parser.parse(andFilter);
			List<Entry> entries = new ArrayList<Entry>(3);
			entries.add(new WeatherEntry(url, nodeList.asString(), tags));
			entries.add(new NavigationEntry(url, nodeList.asString(), tags));
			return entries; 
		} catch (ParseException e) { // message parse
			throw new ParseException(e);
		} catch (BotterException e) {
			throw e;
		} catch (ParserException e) { // html parser exception
			throw new ParseException(e);
		}
	}
}
