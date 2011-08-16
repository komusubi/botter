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

import static jp.dip.komusubi.botter.gae.GaeContext.CONTEXT;

import java.io.Serializable;
import java.net.URL;
import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import jp.dip.komusubi.botter.gae.GaeContext;
import jp.dip.komusubi.botter.gae.model.Entry;
import jp.dip.komusubi.botter.gae.util.TextContentFomatter;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.google.appengine.api.datastore.Key;

/**
 * @author jun.ozeki
 * @version $Id$
 * @since 2011/05/18
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class FlightStatus implements Entry, Serializable {
	private static final long serialVersionUID = 5216984841712989485L;
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Long id;
	@Persistent
	private String gate;
	@Persistent
	private String flightName;
//	@Persistent
//	private Route route;
	private Key routeKey;
	@Persistent
	private Date scheduledDepartureDate;
	@Persistent
	private Date scheduledArrivalDate;
	@Persistent
	private Date departureDate;
	@Persistent
	private Date arrivalDate;
	@Persistent
	private boolean delay;
	@Persistent
	private String memo;
	private transient RouteDao routeDao =	CONTEXT.getInstance(GaeContext.ROUTE_DAO);
	
	@Override
	public long getId() {
		return id == null ? 0L : id.longValue();
//		return id;
	}
	public FlightStatus setId(long id) {
		this.id = Long.valueOf(id);
		return this;
	}
	public String getGate() {
		return gate;
	}
	public FlightStatus setGate(String gate) {
		this.gate = gate;
		return this;
	}
	public String getFlightName() {
		return flightName;
	}
	public FlightStatus setFlightName(String flightName) {
		this.flightName = flightName;
		return this;
	}
	public Route getRoute() {
		Route route = null;
		if (routeKey != null)
			route = routeDao.read(routeKey);
		return route;
	}
	public FlightStatus setRoute(Route route) {
		if (route.getKey() == null) {
			if (route.getDeparture() == null || route.getArrival() == null)
				throw new IllegalArgumentException("route.getDeparture() or getArrival() is null!");
			
			Route routeInStorage = routeDao.readByAirportCode(route.getDeparture().getCode(),
														route.getArrival().getCode());
			if (routeInStorage == null)
				throw new IllegalArgumentException("route not found in storage");
			routeKey = routeInStorage.getKey();
		}
		routeKey = route.getKey();
		return this;
	}
	public Date getDepartureDate() {
		return departureDate;
	}
	public FlightStatus setDepartureDate(Date departureDate) {
		this.departureDate = departureDate;
		return this;
	}
	public Date getArrivalDate() {
		return arrivalDate;
	}
	public FlightStatus setArrivalDate(Date arrivalDate) {
		this.arrivalDate = arrivalDate;
		return this;
	}
	public boolean isDelay() {
		return delay;
	}
	public FlightStatus setDelay(boolean delay) {
		this.delay = delay;
		return this;
	}
	public String getMemo() {
		return memo;
	}
	public FlightStatus setMemo(String memo) {
		this.memo = memo;
		return this;
	}
	public Date getScheduledDepartureDate() {
		return scheduledDepartureDate;
	}
	public FlightStatus setScheduledDepartureDate(Date scheduledDepartureDate) {
		this.scheduledDepartureDate = scheduledDepartureDate;
		return this;
	}
	public Date getScheduledArrivalDate() {
		return scheduledArrivalDate;
	}
	public FlightStatus setScheduledArrivalDate(Date scheduledArrivalDate) {
		this.scheduledArrivalDate = scheduledArrivalDate;
		return this;
	}

	@Override
	public String getText() {
		return TextContentFomatter.formatText(this);
	}
	@Override
	public Date getDate() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public URL getUrl() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this); 
	}
	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}
	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}
}
