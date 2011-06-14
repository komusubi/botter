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

import jp.dip.komusubi.botter.gae.model.FeedElement;
import jp.dip.komusubi.botter.gae.model.GenericDao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jun.ozeki
 * @version $Id$
 * @since 2011/05/15
 */
public class JdoFeedElementDao implements GenericDao<FeedElement, String> {
	private static final Logger logger = LoggerFactory.getLogger(JdoFeedElementDao.class);
	private Provider<PersistenceManager> pmp; 
	
	@Inject
	public JdoFeedElementDao(Provider<PersistenceManager> pmp) {
		this.pmp = pmp;
	}
	
	@Override
	public String create(FeedElement instance) {
		pmp.get().makePersistent(instance);
		return instance.getUrl();
	}

	@Override
	public FeedElement read(String primaryKey) {
		StringBuilder queryBuilder = new StringBuilder("select from ");
		queryBuilder.append(FeedElement.class.getName())
					.append(" where url == ")
					.append(primaryKey);
		return (FeedElement) pmp.get().newQuery(queryBuilder.toString()).execute();
	}

	@Override
	public void update(FeedElement instance) {
		pmp.get().makePersistent(instance);
	}

	@Override
	public void delete(FeedElement instance) {
		pmp.get().deletePersistent(instance);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<FeedElement> findAll() {
		return (List<FeedElement>) pmp.get().newQuery("select from " + FeedElement.class.getName()).execute();
	}

	@SuppressWarnings("unchecked")
	public List<FeedElement> findByBotterId(String botterId) {
		StringBuilder queryBuilder = new StringBuilder("select from ");
		queryBuilder.append(FeedElement.class.getName())
					.append(" where botterId == '")
					.append(botterId)
					.append("'");
		return (List<FeedElement>) pmp.get().newQuery(queryBuilder.toString()).execute();
	}
	
	@SuppressWarnings("unchecked")
	public List<FeedElement> findByBotterIdAndActivate(String botterId, boolean activate) {
		// boolean の扱い不明なので。
//		throw new UnsupportedOperationException("boolean の扱い不明なので。");
		StringBuilder queryBuilder = new StringBuilder("select from ");
		queryBuilder.append(FeedElement.class.getName())
					.append(" where botterId == '")
					.append(botterId)
					.append("' && activate == ")
					.append(activate);
		return (List<FeedElement>) pmp.get().newQuery(queryBuilder.toString()).execute();
	}
}
