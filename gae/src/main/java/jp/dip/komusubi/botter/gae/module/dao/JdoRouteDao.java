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

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import jp.dip.komusubi.botter.gae.model.airline.Route;
import jp.dip.komusubi.botter.gae.model.airline.RouteDao;

import com.google.appengine.api.datastore.Key;

/**
 * @author jun.ozeki
 * @version $Id$
 * @since 2011/05/18
 */
public class JdoRouteDao implements RouteDao { //GenericDao<Route, Key> {
//	private static final Logger logger = LoggerFactory.getLogger(JdoRouteDao.class);
	private PersistenceManager pm;

	@Inject
	public JdoRouteDao(Provider<PersistenceManager> pmp) {
		this.pm = pmp.get();
	}
	
	@Override
	public Key create(Route instance) {
		Route route = pm.makePersistent(instance);
		return route.getKey();
	}

	@Override
	public Route read(Key primaryKey) {
		return pm.getObjectById(Route.class, primaryKey);
	}

	@Override
	public void update(Route instance) {
		pm.makeNontransactional(instance);
	}

	@Override
	public void delete(Route instance) {
		pm.deletePersistent(instance);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Route> findAll() {
		return (List<Route>) pm.newQuery(Route.class.getName()).execute();
	}
	
	// 就航路線を取得
	@SuppressWarnings("unchecked")
	@Override
	public List<Route> findByActivate(boolean active) {
		Query query = pm.newQuery(Route.class);
		query.setFilter("activate == active");
		query.declareParameters("Boolean active");
		return (List<Route>) query.execute(Boolean.valueOf(active));
	}
	
	// find airport code
	@Override
	public Route readByAirportCode(String departureCode, String arrivalCode) {
		Query query = pm.newQuery(Route.class);
		query.setFilter("departure == departureCode"
						+ " && arrival == arrivalCode");
		query.declareParameters("String departureCode, String arrivalCode");
		@SuppressWarnings("unchecked")
		List<Route> routes = (List<Route>) query.execute(departureCode, arrivalCode);
		// FIXME 0件の場合も IllegalStateExceptionでよいの？
		if (routes == null) 
			throw new IllegalStateException("Not found route. departure: " + departureCode
					+ " and arrival: " + arrivalCode);
		if (routes.size() > 1)
			throw new IllegalStateException("departure: " + departureCode 
					+ " and arrival: " + arrivalCode + " found count was " + routes.size());
		Route route = null;
		if (routes.size() == 1)
			route = routes.get(0);
		return route;
	}
	// find active route from airport code
	@Override
	public Route readByAirportCode(String departureCode, String arrivalCode, boolean active) {
		Route route = null; 
		Route result = readByAirportCode(departureCode, arrivalCode);
		if (result.isActivate() == active)
			route = result;
		return route;
	}
}
