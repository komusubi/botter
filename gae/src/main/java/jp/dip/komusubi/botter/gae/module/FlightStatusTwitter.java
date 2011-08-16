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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;

import jp.dip.komusubi.botter.Resolver;
import jp.dip.komusubi.botter.gae.GaeContext;
import jp.dip.komusubi.botter.gae.model.Job;
import jp.dip.komusubi.botter.gae.model.airline.FlightStatus;
import jp.dip.komusubi.botter.gae.model.airline.FlightStatusDao;
import jp.dip.komusubi.botter.gae.model.airline.Route;
import jp.dip.komusubi.botter.gae.model.airline.RouteDao;
import jp.dip.komusubi.botter.gae.module.jal5971.FlightStatusScraper;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jun.ozeki
 * @version $Id$
 * @since 2011/05/26
 */
public class FlightStatusTwitter implements Job {
	public static final String JOBID = "flight";
	private static final Logger logger = LoggerFactory.getLogger(FlightStatusTwitter.class);
	private FlightStatusDao flightStatusDao;
	private RouteDao routeDao;
	private Resolver<Date> dateResolver = GaeContext.CONTEXT.getResolverManager().getDateResolver();
	private List<FlightStatus> flights;
	
	@Inject
	public FlightStatusTwitter(FlightStatusDao flightStatusDao) {
		this.flightStatusDao = flightStatusDao;
	}
	
	@Override
	public boolean available(Map<String, List<String>> param) {
		List<String> values = param.get("route");
		// 15分前時刻を取得
		Calendar limitTime = DateUtils.toCalendar(dateResolver.resolve());
		limitTime.add(Calendar.MINUTE, -15);
		
		// 発着時刻から15分を経過しても実績時刻が設定されていない便情報を取得
		flights = new ArrayList<FlightStatus>();
		// 路線指定なし
		if (values == null || values.size() == 0) {
			flights.addAll(flightStatusDao.findByBeforeDeparture(limitTime.getTime()));
			flights.addAll(flightStatusDao.findByBeforeArrival(limitTime.getTime()));
		} else {
			for (String value: values) {
				// FIXME validate query parameter
				String[] airports = normalize(value);
				Route route = routeDao.readByAirportCode(airports[0], airports[1]);
				if (route == null) {
					logger.warn("not found Route, "
							+"query parameter is wrong:{} and {}", airports[0], airports[1]);
					continue;
				}
				flights.addAll(flightStatusDao.findByBeforeDeparture(limitTime.getTime(), value));
				flights.addAll(flightStatusDao.findByBeforeArrival(limitTime.getTime(), value));
			}
		}
		logger.info("delay flight count is {}", flights.size());
		return flights.size() > 0;
	}

	private String[] normalize(String value) {
		String[] arrays = new String[2];
		if (value != null)
			arrays = value.split("-=,");
		return arrays;
	}

	@Override
	public boolean execute() {
		if (flights == null || flights.size() <= 0)
			return false;
		Map<Route, List<FlightStatus>> routeFlightMap = mapped(flights);
		for (Entry<Route, List<FlightStatus>> e: routeFlightMap.entrySet()) {
			
			FlightStatusScraper scraper = new FlightStatusScraper(e.getKey());
			
			Map<String, FlightStatus> scrapedMap = scraper.getMapped();
			for (FlightStatus storage: e.getValue()) {
				FlightStatus scraped = scrapedMap.get(storage.getFlightName());
				if (scraped == null) {
					logger.warn("wrong flight status: scraped was null, but storage was {}",
							storage);
					continue;
				}
				if (isUpdated(storage, scraped)) {
					logger.info("update flight status:{}", storage.getFlightName());
						flightStatusDao.update(merge(storage, scraped));
				}
			}
		}
		return true;
	}
	
	private FlightStatus merge(FlightStatus storage, FlightStatus scraped) {
		storage.setDepartureDate(scraped.getDepartureDate())
				.setArrivalDate(scraped.getArrivalDate())
				.setDelay(scraped.isDelay())
				.setGate(scraped.getGate())
				.setMemo(scraped.getMemo())
				.setScheduledDepartureDate(scraped.getScheduledDepartureDate())
				.setScheduledArrivalDate(scraped.getScheduledArrivalDate());
				
		return storage;
	}
	
	/**
	 * @param storage
	 * @param scraped
	 * @return
	 */
	private boolean isUpdated(FlightStatus storage, FlightStatus scraped) {
		if ((storage.getDepartureDate() == null && scraped.getDepartureDate() != null)
			|| (storage.getArrivalDate() == null && scraped.getArrivalDate() != null)
			|| (storage.getMemo() == null && scraped.getMemo() != null))
				return true;
		return false;
	}

	private Map<Route, List<FlightStatus>> mapped(List<FlightStatus> lists) {
		Map<Route, List<FlightStatus>> map = new HashMap<Route, List<FlightStatus>>();
		for (FlightStatus flight: lists) {
			if (map.get(flight.getRoute()) == null) {
				List<FlightStatus> flightList = new ArrayList<FlightStatus>();
				flightList.add(flight);
				map.put(flight.getRoute(), flightList);
			} else {
				map.get(flight.getRoute()).add(flight);
			}
		}
		return map;
	}
//	private List<Route> reduce(List<FlightStatus> lists) {
//		List<Route> routes = new ArrayList<Route>();
//		for (FlightStatus flight: lists) {
//			if (!routes.contains(flight.getRoute()))
//				routes.add(flight.getRoute());
//		}
//		return routes;
//	}
}
