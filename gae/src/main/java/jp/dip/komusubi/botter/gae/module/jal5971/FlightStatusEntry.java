/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with this
 * work for additional information regarding copyright ownership. The ASF
 * licenses this file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package jp.dip.komusubi.botter.gae.module.jal5971;

import static jp.dip.komusubi.botter.gae.GaeContext.CONTEXT;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;

import javax.inject.Inject;

import jp.dip.komusubi.botter.Resolver;
import jp.dip.komusubi.botter.gae.model.Entry;
import jp.dip.komusubi.botter.gae.model.airline.Route;
import jp.dip.komusubi.botter.gae.module.dao.JdoAirportDao;
import jp.dip.komusubi.botter.gae.util.TextContentFomatter;

import org.apache.commons.lang.time.DateUtils;
import org.htmlparser.nodes.TextNode;
import org.htmlparser.util.NodeList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jun.ozeki
 * @version $Id: FlightStatusEntry.java 1356 2010-12-31 05:13:01Z jun $
 * @since 2010/09/26
 */
public class FlightStatusEntry implements Entry {
	private static Logger logger = LoggerFactory.getLogger(FlightStatusEntry.class);
	private static String HOUR_FORMAT = "HH:mm";
	private NodeList header;
	private NodeList column;
	private Resolver<Date> resolver = CONTEXT.getResolverManager().getDateResolver();
	private Date created;
	private String url;
	@Inject 
	private JdoAirportDao airportDao;
	private String[] hashTags;
	private Route route; 
	
	/**
	 * constructor.
	 * @param headers
	 * @param columns
	 */
	public FlightStatusEntry(Route route, NodeList header, NodeList column, String... hashTags) {
		this.route = route;
		this.header = header;
		this.column = column;
		this.hashTags = hashTags;
	}
	
	@Override
	public long getId() {
		return 0;
	}

	@Override
	public String getText() {
		return TextContentFomatter.formatText(this);
	}
	@Override
	public Date getDate() {
		return created;
	}

	@Override
	public URL getUrl() {
		try {
			return new URL(url);
		} catch (MalformedURLException e) {
			// ignore 
		}
		return null;
	}
	protected TextNode getElement(NodeList nodeList, int i) {
		TextNode node = null;
		if (i > nodeList.size()) {
			logger.warn("NodeList size is {}. but index is {}.", nodeList.size(), i);
			node = new TextNode("");
		} 
		if (nodeList.elementAt(i) == null)
			node = new TextNode("");
		return node != null ? node : (TextNode) nodeList.elementAt(i);
	}
	
	public String getFlightName() {
		return getElement(column, 0).getText().trim();
	}
	public String getGate() {
		return getElement(column, 5).getText().trim();
	}
	public Route getRoute() {
		return route;
	}
	public Date getScheduledDepartureDate() {
		try {
			return DateUtils.parseDate(getElement(column, 2).getText().trim(), new String[]{HOUR_FORMAT});
		} catch (ParseException e) {
			logger.warn("scheduledDepartureDate() parse error {}", e.getLocalizedMessage());
			return resolver.resolve();
		}
	}
	public Date getScheduledArrivalDate() {
		try {
			return DateUtils.parseDate(getElement(column, 6).getText().trim(), new String[]{HOUR_FORMAT});
		} catch (ParseException e) {
			logger.warn("scheduledArrivalDate() parse error {}", e.getLocalizedMessage());
			return resolver.resolve();
		}
	}
	public Date getDepartureDate() {
		try {
			return DateUtils.parseDate(getElement(column, 3).getText().trim(), new String[]{HOUR_FORMAT});
		} catch (ParseException e) {
			logger.warn("departureDate() parse error {}", e.getLocalizedMessage());
//			return null;
			return resolver.resolve();
		}
	}
	public Date getArrivalDate() {
		try {
			return DateUtils.parseDate(getElement(column, 7).getText().trim(), new String[]{HOUR_FORMAT});
		} catch (ParseException e) {
			logger.warn("arrivalDate() parse error {}", e.getLocalizedMessage());
			return resolver.resolve();
		}
	}
	public String getMemo() {
		return getElement(column, 9).getText().trim();
	}
	public boolean isDelay() {
		return "*".equals(getElement(column, 1).getText().trim());
	}
}
