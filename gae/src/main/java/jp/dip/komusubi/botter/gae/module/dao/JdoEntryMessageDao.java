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
package jp.dip.komusubi.botter.gae.module.dao;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import jp.dip.komusubi.botter.gae.model.Entry;
import jp.dip.komusubi.botter.gae.model.EntryDao;
import jp.dip.komusubi.botter.gae.model.EntryMessage;
import jp.dip.komusubi.botter.gae.util.ConvertModel;

/**
 * @author jun.ozeki
 * @version $Id$
 * @since 2011/05/08
 */
public class JdoEntryMessageDao implements EntryDao {
	private PersistenceManager pm;

	@Inject
	public JdoEntryMessageDao(Provider<PersistenceManager> pmp) {
		this.pm = pmp.get();
	}

	@Override
	public Long create(Entry instance) {
		pm.makePersistent(ConvertModel.toEntryMessage(instance));
		return instance.getId();
	}

	@Override
	public EntryMessage read(Long primaryKey) {
		StringBuilder queryBuilder = new StringBuilder("select from ");
		queryBuilder.append(EntryMessage.class.getName()).append(" where id == ")
				.append(primaryKey);
		return (EntryMessage) pm.newQuery(queryBuilder.toString()).execute();
	}

	@Override
	public void update(Entry instance) {
		pm.makePersistent(ConvertModel.toEntryMessage(instance));
	}

	@Override
	public void delete(Entry instance) {
		pm.deletePersistent(ConvertModel.toEntryMessage(instance));
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Entry> findAll() {
		return (List<Entry>) pm.newQuery("select from " + EntryMessage.class.getName())
				.execute();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<Entry> findByBeforeTweet() {
		Query query = pm.newQuery(Entry.class);
		query.setFilter("tweeted == null");
		return (List<Entry>) query.execute();
	}
}
