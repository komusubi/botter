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
import java.util.Date;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import jp.dip.komusubi.botter.gae.GaeContext;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.google.appengine.api.datastore.Key;

/**
 * @author jun.ozeki
 * @version $Id$
 * @since 2011/05/18
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Route implements Serializable {
	private static final long serialVersionUID = -1276173667765598507L;
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;
//	private Long id;
//	@Extension(vendorName="datanucleus", key="gae.pk-id", value="true")
//	@Persistent
//	private List<String> airportIds;

	@Persistent
	private String departure;
	@Persistent
	private String arrival;
//	private Airport departure;
//	private Airport arrival;
	@Persistent
	private boolean activate = true;
	@Persistent
	private Date activateAt = new Date(0L); // 就航中の場合は1970-01-01
	@Persistent
	private Date deactivated; // 就航終了日
	private transient AirportDao airportDao = 
		CONTEXT.getInstance(GaeContext.AIRPORT_DAO);
	
	/**
	 * constructor.
	 * @param departure
	 * @param arrival
	 */
	public Route(Airport departure, Airport arrival) {
//		this.departure = departure;
//		this.arrival = arrival;
		this(departure.getCode(), arrival.getCode());
	}
	public Route(String departure, String arrival) {
//		airportIds = Arrays.asList(departure, arrival); 
		this.departure = departure;
		this.arrival = arrival;
	}
	public Route setKey(Key key) {
		this.key = key;
		return this;
	}
	public Key getKey() {
		return key;
	}
	public Airport getDeparture() {
		return airportDao.read(departure);
//	public String getDeparture() {
//		return departure;
	}

//	public Route setDeparture(Airport departure) {
	public Route setDeparture(String departure) {
		this.departure = departure;
		return this;
	}

	public Airport getArrival() {
		return airportDao.read(arrival);
//	public String getArrival() {
//		return arrival;
	}

//	public Route setArrival(Airport arrival) {
	public Route setArrival(String arrival) {
		this.arrival = arrival;
		return this;
	}

	public boolean isActivate() {
		return activate;
	}

	public Route setActivate(boolean activate) {
		this.activate = activate;
		return this;
	}

	public Date getActivateAt() {
		return activateAt;
	}

	public Route setActivateAt(Date activateAt) {
		this.activateAt = activateAt;
		return this;
	}

	public Date getDeactivated() {
		return deactivated;
	}

	public Route setDeactivated(Date deactivated) {
		this.deactivated = deactivated;
		return this;
	}
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
	@Override
	public boolean equals(Object other) {
		return EqualsBuilder.reflectionEquals(this, other);
	}
	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}
}
