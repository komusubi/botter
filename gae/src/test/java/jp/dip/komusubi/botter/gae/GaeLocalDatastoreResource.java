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
package jp.dip.komusubi.botter.gae;

import java.util.HashMap;
import java.util.Map;

import org.junit.rules.ExternalResource;

import com.google.appengine.api.datastore.dev.LocalDatastoreService;
import com.google.appengine.tools.development.ApiProxyLocal;
import com.google.apphosting.api.ApiProxy;

/**
 * @author jun.ozeki
 * @version $Id$
 * @since 2011/05/24
 */
public class GaeLocalDatastoreResource extends ExternalResource {

	private GaeLocalResource env = new GaeLocalResource();
	
	@Override
	public void before() {
		env.before();
		ApiProxyLocal proxy = (ApiProxyLocal) ApiProxy.getDelegate();
		Map<String, String> map = new HashMap<String, String>();
		map.put(LocalDatastoreService.NO_STORAGE_PROPERTY, Boolean.TRUE.toString());
		proxy.setProperties(map);
	}
	
	@Override
	public void after() {
		ApiProxyLocal proxy = (ApiProxyLocal) ApiProxy.getDelegate();
		LocalDatastoreService datastoreService = (LocalDatastoreService) proxy.getService("datastore_v3");
		datastoreService.clearProfiles();
		env.after();
	}
}
