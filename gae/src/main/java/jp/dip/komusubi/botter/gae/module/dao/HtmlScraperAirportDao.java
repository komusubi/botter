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

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jp.dip.komusubi.botter.gae.ParseException;
import jp.dip.komusubi.botter.gae.model.GenericDao;
import jp.dip.komusubi.botter.gae.model.airline.Airport;

import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.AndFilter;
import org.htmlparser.filters.HasAttributeFilter;
import org.htmlparser.filters.NodeClassFilter;
import org.htmlparser.tags.OptionTag;
import org.htmlparser.tags.SelectTag;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

/**
 * @author jun.ozeki
 * @version $Id: HtmlScrapeAirportDao.java 1356 2010-12-31 05:13:01Z jun $
 * @since 2010/10/04
 */
public class HtmlScraperAirportDao implements GenericDao<Airport, String> {
	private static HashMap<String, Airport> map = new HashMap<String, Airport>();
	private String url;
	
	/**
	 * constructor.
	 * @param url
	 */
	public HtmlScraperAirportDao(URL url) {
		this(url.toExternalForm());
	}
	public HtmlScraperAirportDao(String url) {
		this.url = url;
	}
	public HtmlScraperAirportDao() {
		scrape();
	}
	
	protected static void scrape() {
		if (map.size() != 0)
			return;
		Parser parser = new Parser();
		Pattern pattern = Pattern.compile("option +value=\"([A-Z]{3})\".*");
		try {
			HasAttributeFilter attrFilter = new HasAttributeFilter("name", "DPORT");
			NodeClassFilter nodeFilter = new NodeClassFilter(SelectTag.class);
			AndFilter andFilter = new AndFilter(attrFilter, nodeFilter);
			
			parser.setURL("http://www.5971.jal.co.jp/rsv/ArrivalAndDepartureInput.do");
			NodeList nodeList = parser.parse(andFilter);
			NodeList list = new NodeList();
			for (Node node: nodeList.toNodeArray()) {
				node.collectInto(list, new NodeClassFilter(OptionTag.class));
			}
			for (Node node: list.toNodeArray()) {
				Matcher matcher = pattern.matcher(node.getText());
				if (matcher.matches()) {
					String code = matcher.group(1);
					Airport airport = new Airport(code, node.toPlainTextString());
					map.put(code, airport);
				}
			}
		} catch (ParserException e) {
			throw new ParseException(e);
		}
	}
	
	@Override
	public String create(Airport instance) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Airport read(String code) {
		Airport airport = null;
		if (map.containsKey(code))
			airport = map.get(code);
		return airport;
	}

	@Override
	public void update(Airport instance) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void delete(Airport instance) {
		throw new UnsupportedOperationException();
	}

	public List<Airport> findByName(String name) {
		List<Airport> list = Collections.emptyList();
		if (name == null)
			return list;
		for (Entry<String, Airport> entry: map.entrySet()) {
			String airportName = entry.getValue().getName();
			if (name.equals(airportName))
				list = Arrays.asList(entry.getValue());
		}
		return list;
	}

	@Override
	public List<Airport> findAll() {
		return new ArrayList<Airport>(map.values());
	}
}
