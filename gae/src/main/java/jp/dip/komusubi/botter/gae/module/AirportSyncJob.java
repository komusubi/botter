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

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import jp.dip.komusubi.botter.Resolver;
import jp.dip.komusubi.botter.gae.GaeContext;
import jp.dip.komusubi.botter.gae.model.Job;
import jp.dip.komusubi.botter.gae.model.airline.Airport;
import jp.dip.komusubi.botter.gae.model.airline.AirportDao;
import jp.dip.komusubi.botter.gae.model.airline.Route;
import jp.dip.komusubi.botter.gae.model.airline.RouteDao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jun.ozeki
 * @version $Id$
 * @since 2011/06/05
 */
public class AirportSyncJob implements Job {
	public static final String JOBID = "airportSync";
	private static final Logger logger = LoggerFactory.getLogger(AirportSyncJob.class);
	private AirportDao fromDao;
	private AirportDao toDao;
	private RouteDao routeDao;
	private Resolver<Date> dateResolver;
	
	@Inject
	public AirportSyncJob(@Named("baseAirport") AirportDao fromDao, 
								@Named("affectAirport") AirportDao toDao, RouteDao routeDao) {
		this.fromDao = fromDao;
		this.toDao = toDao;
		this.routeDao = routeDao;
		dateResolver = GaeContext.CONTEXT.getResolverManager().getDateResolver();
	}

	public AirportSyncJob() {
		// FIXME harry up!!
//		this(GaeContext.CONTEXT.getInstance(JdoAirportDao.class),
//				GaeContext.CONTEXT.getInstance(HtmlScraperAirportDao.class));
	}

	/**
 * 
	 */
	@Override
	public boolean available(Map<String, List<String>> queryParam) {
		return true;
	}

	/**
	 * 空港情報を本家サイトと同期させます。
	 * <ol>
	 * <li>html scrape 情報と storage 情報を比較(不足があれば永続化)。 </li>
	 * <li>storage 情報から再度 html scrape 情報と比較(不足があれば、disableに更新して永続化)。</li>
	 * <li>airportの数を総当りでrouteを作成</li>
	 * </ol>
	 */
	@Override
	public boolean execute() {
		// first, html base airport data.
		for (Airport airport: fromDao.findAll()) {
			Airport inStorage = toDao.read(airport.getCode());
			if (inStorage == null) {
				toDao.create(airport);
			} else if (!inStorage.equals(airport)) {
				if (!inStorage.isActivate()) { 
					inStorage.setActivate(true)
								.setActivateAt(dateResolver.resolve());
					logger.info("airport has activate: {}", inStorage);
				}
				toDao.update(airport);
			} else {
				if (logger.isDebugEnabled())
					logger.debug("match airport: {}, from html to storage.", airport);
			}
		}
		// second, compare to storage data
		for (Airport airport: toDao.findByActivate(true)) {
			Airport fromHtml = fromDao.read(airport.getCode());
			if (fromHtml == null) {
				airport.setActivate(false)
					.setDisabledAt(dateResolver.resolve());
				// HTMLに情報が存在しない場合、Storage情報をdisableにして更新 
				toDao.update(airport);
			} else if (!fromHtml.equals(airport)) {
				toDao.update(fromHtml);
			} else {
				if (logger.isDebugEnabled())
					logger.debug("match airport: {}, from storage to html", airport);
			}
		}
		// 路線
		for (Airport departure: toDao.findByActivate(true)) {
			for (Airport arrival: toDao.findByActivate(true)) {
				if (departure.equals(arrival))
					continue;
				Route route = routeDao.readByAirportCode(departure.getCode(), arrival.getCode());
				if (route == null) {
					routeDao.create(new Route(departure, arrival));
				} else {
					if (!route.isActivate()) {
						route.setActivate(true)
							.setTimestamp(dateResolver.resolve());
						routeDao.update(route);
					}
				}
			}
		}
		return true;
	}

}
