/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jp.dip.komusubi.botter.gae.module.aggregator;

import static jp.dip.komusubi.botter.gae.GaeContext.CONTEXT;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;

import jp.dip.komusubi.botter.UrlUtil;
import jp.dip.komusubi.botter.gae.model.Entry;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.syndication.feed.synd.SyndEntry;

/**
 * RSS, Atom feeder aggregation message.
 * @author jun.ozeki 
 * @since 2009/11/05
 * @version $Id: SyndEntryMessage.java 1356 2010-12-31 05:13:01Z jun $
 */
public class SyndEntryMessage implements Entry {
	private static final long serialVersionUID = -5183095497703749566L;
	private static final Logger logger = LoggerFactory.getLogger(SyndEntryMessage.class);
	private SyndEntry entry;
	private String[] hashTags;
	private long id = System.currentTimeMillis();
	
	/**
	 * コンストラクタ。
	 * @param entry
	 */
	public SyndEntryMessage(SyndEntry entry, String... hashTags) {
		this.entry = entry;
		this.hashTags = hashTags;
	}

	@Override
	public long getId() {
		return id; 
	}
	
	@Override
	public Date getDate() {
		return entry.getPublishedDate();
	}

	@Override
	public String getText() {
		UrlUtil urlUtil = CONTEXT.getResolverManager().getUrlUtil();
		StringBuilder builder = new StringBuilder();
		builder.append(entry.getTitle())
				.append(" ")
				.append(urlUtil.shorten(entry.getLink()));
		
		for (String tag: hashTags)
			builder.append(" ").append(tag);
		return builder.toString();
	}

	@Override
	public URL getUrl() {
		URL url = null;
		try {
			url = new URL(entry.getLink());
		} catch (MalformedURLException e) {
			logger.error("指定のURLの値が不正です。: {}", entry.getLink());
		}
		return url;
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Entry)) 
			return false;
		Entry entry = (Entry) obj;
		if (entry.getText() == null)
			return getText() == null;
		return entry.getText().equals(getText());
	}
	
	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}
}
