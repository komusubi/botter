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

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jp.dip.komusubi.botter.BotterException;
import jp.dip.komusubi.botter.gae.GaeContext;
import jp.dip.komusubi.botter.gae.model.Entry;
import jp.dip.komusubi.botter.gae.model.Scraper;
import jp.dip.komusubi.botter.gae.model.airline.FlightStatus;
import jp.dip.komusubi.botter.gae.model.airline.Route;
import jp.dip.komusubi.botter.gae.util.ConvertModel;

import org.apache.commons.lang.StringUtils;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.HasParentFilter;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jun.ozeki
 * @version $Id$
 * @since 2011/05/18
 */
public class FlightStatusScraper implements Scraper {
	private static final String FLIGHT_STATUS_SCRAPE_TARGET = "jal5971.flight.status.url";
	private static final Logger logger = LoggerFactory.getLogger(FlightStatusScraper.class);
	private Route route;
	private String url;
	private String[] hashTags;
	private Parser parser;
	
	/**
	 * constructor.
	 * @param route
	 */
	public FlightStatusScraper(String url, Route route, String... hashTags) {
		try {
			new URL(url);
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException(e);
		}
		this.url = url;
		this.route = route;
		this.hashTags = hashTags;
		parser = new Parser();
	}
	
	/**
	 * constructor.
	 * @param route
	 */
	public FlightStatusScraper(Route route, String... tags) {
		this(GaeContext.CONTEXT.getProperty(FLIGHT_STATUS_SCRAPE_TARGET), route, tags);
	}
	
	protected String buildUrl(String baseUrl) {
		StringBuilder builder = new StringBuilder(baseUrl);
		builder.append("?DPORT=")
				.append(route.getDeparture().getCode())
				.append("&APORT=")
				.append(route.getArrival().getCode());
		return builder.toString();
	}
	
	@Override
	public List<Entry> scrape() {
		HasParentFilter filter = new HasParentFilter(new HasAttributeFilter("class", "bargainTableA01"), true);
		List<Entry> entries = new ArrayList<Entry>();
		try {
			parser.setResource(buildUrl(url));
			NodeList nodeList = parser.parse(filter);
			NodeList header = new NodeList();
			NodeList column = new NodeList();
			boolean isHeader = true;
//			boolean isLineComplete = false;
			for (Node n: nodeList.toNodeArray()) {
				if (n instanceof TagNode) {
					TagNode tag = (TagNode) n;
					// 発着案内 tableの header部
					if ("thead".equalsIgnoreCase(tag.getTagName()) 
							&& "timetable".equals(tag.getAttribute("id"))) {
						isHeader = true;
					} else if ("tbody".equalsIgnoreCase(tag.getTagName()) 
							&& "bording".equals(tag.getAttribute("id"))) {
						isHeader = false;
					} else if ("tr".equalsIgnoreCase(tag.getTagName())) {
//						isLineComplete = true;
						entries.add(new FlightStatusEntry(route, header, column, hashTags));
						column = new NodeList();
					}
				} else if (n instanceof TextNode) {
					TextNode textNode = (TextNode) n;
					if (StringUtils.isNotBlank(textNode.getText())) {
						if (isHeader) {
							header.add(textNode);
						} else {
							column.add(textNode);
						}
					}	
				}
			}
			
		} catch (ParserException e) {
			throw new BotterException(e);
		}
		return entries;
	}
	
	public List<FlightStatus> getFlightStatuses() {
		List<FlightStatus> flightStatuses = new ArrayList<FlightStatus>();
		for (Entry e:scrape()) {
			FlightStatusEntry entry = (FlightStatusEntry) e;
			FlightStatus fs = ConvertModel.toFlightStatus(entry);
			flightStatuses.add(fs);
		}
		return flightStatuses;
	}
	
	public Map<String, FlightStatus> getMapped() {
		Map<String, FlightStatus> map = new HashMap<String, FlightStatus>();
		for (FlightStatus fs: getFlightStatuses()) {
			map.put(fs.getFlightName(), fs);
		}
		return map;
	}
}
