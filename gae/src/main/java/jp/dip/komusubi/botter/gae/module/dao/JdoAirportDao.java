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

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import jp.dip.komusubi.botter.gae.model.airline.Airport;
import jp.dip.komusubi.botter.gae.model.airline.AirportDao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jun.ozeki
 * @version $Id$
 * @since 2010/10/04
 */
public class JdoAirportDao implements AirportDao {
	private static final Logger logger = LoggerFactory.getLogger(JdoAirportDao.class);
	private PersistenceManager pm;
	
	@Inject
	public JdoAirportDao(Provider<PersistenceManager> pmp) {
		this.pm = pmp.get();
	}

	@Override
	public String create(Airport instance) {
		Airport airport = pm.makePersistent(instance);
		return airport.getCode();
	}

	@Override
	public Airport read(String primaryKey) {
		Airport airport = null;
		try {
			airport = pm.getObjectById(Airport.class, primaryKey);
		} catch (JDOObjectNotFoundException e) {
			logger.info("JdoAirportDao#read date not found: {}", primaryKey);
		}
		return airport;
	}

	@Override
	public void update(Airport instance) {
		pm.makePersistent(instance);
	}

	@Override
	public void delete(Airport instance) {
		pm.deletePersistent(instance);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Airport> findAll() {
		StringBuilder builder = new StringBuilder("select from ");
		builder.append(Airport.class.getName());
		return (List<Airport>) pm.newQuery(builder.toString()).execute();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Airport> findByActivate(boolean active) {
		Query query = pm.newQuery(Airport.class);
		query.setFilter("activate == active");
		query.declareParameters("Boolean active");
		return (List<Airport>) query.execute(active);
	}
	
	@Override
	public Airport readByCode(String code) {
		StringBuilder builder = new StringBuilder("select from ");
		builder.append(Airport.class.getName())
			.append(" where code == '")
			.append(code).append("'");
		@SuppressWarnings("unchecked")
		List<Airport> list = (List<Airport>) pm.newQuery(builder.toString()).execute();
		Airport airport = null;
		if (list == null) {
			logger.warn("airport code {} is not found.", code);
			list = new ArrayList<Airport>();
		}
		if (list.size() >= 1) {
			logger.warn("aiport code {} is multiple data. expected 1 but {}", code, list.size());
			airport = list.get(0);
		}
		
		return airport;
	}
}
