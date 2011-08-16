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

import java.io.Serializable;
import java.util.Date;

import javax.jdo.annotations.IdentityType;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * @author jun.ozeki
 * @version $Id$
 * @since 2010/10/04
 */
@PersistenceCapable(identityType = IdentityType.APPLICATION)
public class Airport implements Serializable {
	private static final long serialVersionUID = 6383482842557800998L;
//	@PrimaryKey
//	private Key key;
//	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
//	private Long id;
//	@Persistent
	@PrimaryKey
	private String code;
	@Persistent
	private String name;
//	private String abbreviation;
	@Persistent
	private boolean activate = true;
	@Persistent
	private Date activateAt = new Date(0L);
	@Persistent
	private Date disabledAt;
	
	public Airport(String code) {
		this.code = code;
	}
	public Airport(String code, String name) {
		this(code, name, null);
	}
	public Airport(String code, String name, String abbreviation) {
		this.code = code;
		this.name = name;
//		this.abbreviation = abbreviation;
	}
//	public String getAbbreviation() {
//		return abbreviation;
//	}
//	public void setAbbreviation(String abbreviation) {
//		this.abbreviation = abbreviation;
//	}
	public String getName() {
		return name;
	}
	public Airport setName(String name) {
		this.name = name;
		return this;
	}
	public String getCode() {
		return code;
	}
	public Airport setCode(String code) {
		this.code = code;
		return this;
	}
	public boolean isActivate() {
		return activate;
	}
	public Airport setActivate(boolean activate) {
		this.activate = activate;
		return this;
	}
	public Date getActivateAt() {
		return activateAt;
	}
	public Airport setActivateAt(Date activateAt) {
		this.activateAt = activateAt;
		return this;
	}
	public Date getDisabledAt() {
		return disabledAt;
	}
	public Airport setDisabledAt(Date disabledAt) {
		this.disabledAt = disabledAt;
		return this;
	}
	@Override
	public boolean equals(Object other) {
		return EqualsBuilder.reflectionEquals(this, other);
	}
	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
