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

import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import jp.dip.komusubi.botter.Resolver;
import jp.dip.komusubi.botter.gae.GaeContext;
import jp.dip.komusubi.botter.gae.GaeContext.ResolverManager;
import jp.dip.komusubi.botter.gae.model.airline.FlightStatus;
import jp.dip.komusubi.botter.gae.model.airline.FlightStatusDao;
import jp.dip.komusubi.botter.gae.model.airline.Route;

import com.google.inject.Provider;

/**
 * @author jun.ozeki
 * @version $Id$
 * @since 2011/05/22
 */
public class JdoFlightStatusDao implements FlightStatusDao { 
//	private static final Logger logger = LoggerFactory.getLogger(JdoFlightStatusDao.class);
	private PersistenceManager pm;
	private JdoRouteDao routeDao;
	private ResolverManager resolverManager = GaeContext.CONTEXT.getResolverManager();
	/**
	 * constructor.
	 * @param pmp
	 */
	@Inject
	public JdoFlightStatusDao(Provider<PersistenceManager> pmp, 
			JdoRouteDao routeDao) {
		pm = pmp.get();
		this.routeDao = routeDao;
	}
	
	@Override
	public Long create(FlightStatus instance) {
		pm.makePersistent(instance);
		return instance.getId();
	}

	@Override
	public FlightStatus read(Long primaryKey) {
		return pm.getObjectById(FlightStatus.class, primaryKey);
	}

	@Override
	public void update(FlightStatus instance) {
		pm.makePersistent(instance);
	}

	@Override
	public void delete(FlightStatus instance) {
		pm.deletePersistent(instance);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FlightStatus> findAll() {
		StringBuilder builder = new StringBuilder("select from ");
		builder.append(FlightStatus.class.getName());
		return (List<FlightStatus>) pm.newQuery(builder.toString()).execute();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FlightStatus> findByBeforeDeparture(Date dateOfTravel, Route specificRoute) {
		Query query = pm.newQuery(FlightStatus.class);
		// 指定した路線で出発時刻が登録されていないデータを検索
		query.setFilter("routeKey == specificKey"
				+ " && scheduledDepartureDate > dateOfTravel"
				+ " && departureDate == null"
				+ " && delay == false");
		query.declareParameters("com.google.appengine.api.datastore.Key specificKey"
				+ ", java.util.Date dateOfTravel");
		query.setOrdering("scheduledDepartureDate desc");
		return (List<FlightStatus>) query.execute(specificRoute.getKey(), dateOfTravel);
	}
	// 出発前の便を検索 find by route and date resolver
	@Override
	public List<FlightStatus> findByBeforeDeparture(Route specificRoute) {
		Resolver<Date> dateResolver = resolverManager.getDateResolver();
		return findByBeforeDeparture(dateResolver.resolve(), specificRoute);
	}
	// 出発前の便を検索
	@Override
	public List<FlightStatus> findByBeforeDeparture(String flightRoute) {
		Resolver<Date> dateResolver = resolverManager.getDateResolver();
		return findByBeforeDeparture(dateResolver.resolve(), flightRoute);
	}
	// 出発前の便を検索
	// specific format for route "HND-ITM", "HND=ITM" or "HND.ITM" 
	@Override
	public List<FlightStatus> findByBeforeDeparture(Date dateOfTravel, String flightRoute) {
		String[] airports = flightRoute.split("-=.");
		Route route = routeDao.readByAirportCode(airports[0], airports[1]);
		return findByBeforeDeparture(dateOfTravel, route);
	}
	// 到着前の便を検索
	@SuppressWarnings("unchecked")
	@Override
	public List<FlightStatus> findByBeforeArrival(Date dateOfTravel, Route specificRoute) {
		Query query = pm.newQuery(FlightStatus.class);
		query.setFilter("routeKey == specificKey"
				+ " && scheduledArrivalDate > dateOfTravel"
				+ " && arrivalDate == null"
				+ " && delay == false");
		query.declareParameters("com.google.appengine.api.datastore.Key specificKey"
				+ ", Date dateOfTravel");
		query.setOrdering("scheduledArrivalDate desc");
		return (List<FlightStatus>) query.execute(specificRoute.getKey(), dateOfTravel);
	}
	// 到着前の便を検索 find by route
	@Override
	public List<FlightStatus> findByBeforeArrival(Route specificRoute) {
		Resolver<Date> dateResolver = resolverManager.getDateResolver();
		return findByBeforeArrival(dateResolver.resolve(), specificRoute);
	}
	@Override
	public List<FlightStatus> findByBeforeArrival(String flightRoute) {
		Resolver<Date> dateResolver = resolverManager.getDateResolver();
		return findByBeforeArrival(dateResolver.resolve(), flightRoute);
	}
	@Override
	public List<FlightStatus> findByBeforeArrival(Date dateOfTravel, String flightRoute) {
		String[] airports = flightRoute.split("-=.");
		Route route = routeDao.readByAirportCode(airports[0], airports[1]);
		return findByBeforeArrival(dateOfTravel, route);
	}
}
