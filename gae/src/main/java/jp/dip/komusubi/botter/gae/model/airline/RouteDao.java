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
package jp.dip.komusubi.botter.gae.model.airline;

import java.util.List;

import jp.dip.komusubi.botter.gae.model.GenericDao;

import com.google.appengine.api.datastore.Key;

/**
 * @author jun.ozeki
 * @version $Id$
 * @since 2011/06/14
 */
public interface RouteDao extends GenericDao<Route, Key>{
	List<Route> findByActivate(boolean active);
	List<Route> findByActivate(boolean active, int count);
	Route readByAirportCode(String departureCode, String arrivalCode);
	Route readByAirportCode(String departureCode, String arrivalCode, boolean active);
}
