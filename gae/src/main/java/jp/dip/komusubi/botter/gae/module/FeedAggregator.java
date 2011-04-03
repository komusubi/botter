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
package jp.dip.komusubi.botter.gae.module;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import jp.dip.komusubi.botter.BotterException;
import jp.dip.komusubi.botter.gae.model.Aggregator;
import jp.dip.komusubi.botter.gae.model.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.fetcher.FeedFetcher;
import com.sun.syndication.fetcher.impl.FeedFetcherCache;
import com.sun.syndication.fetcher.impl.HashMapFeedInfoCache;
import com.sun.syndication.fetcher.impl.HttpURLFeedFetcher;
import com.sun.syndication.fetcher.impl.SyndFeedInfo;

/**
 * @author jun.ozeki
 * @version $Id: FeedAggregator.java 1356 2010-12-31 05:13:01Z jun $
 * @since 2010/08/13
 */
public class FeedAggregator implements Aggregator {
	private static final Logger logger = LoggerFactory.getLogger(FeedAggregator.class);
	private FeedFetcherCache cache;
//	private List<URL> feedUrls;
	private UrlEntry[] urlEntries;

	public static class UrlEntry {
		private String[] hashTags;
		private URL url;
		/**
		 * constructor.
		 * @param url
		 * @param hashTags
		 * @throws MalformedURLException
		 */
		public UrlEntry(String url, String... hashTags) {
			try {
				this.url = new URL(url);
			} catch (MalformedURLException e) {
				throw new BotterException(e);
			}
			this.hashTags = hashTags;
		}
		/**
		 * constructor.
		 * @param url
		 * @param hashTags
		 */
		public UrlEntry(URL url, String... hashTags) {
			this.url = url;
			this.hashTags = hashTags;
		}
		public URL getUrl() {
			return url;
		}
		public String[] getHashTags() {
			return hashTags;
		}
	}

	/**
	 * constructor.
	 * @param url
	 * @param hashTags
	 */
	public FeedAggregator(URL url, String... hashTags) {
		this(new UrlEntry(url, hashTags));
	}
	
	/**
	 * constructor.
	 * @param urlEntry
	 */
	public FeedAggregator(UrlEntry... urlEntries) {
		this.urlEntries = urlEntries;
		cache = HashMapFeedInfoCache.getInstance();
	}
	
	/**
	 * constructor.
	 */
	public FeedAggregator() {
		
	}
	
	/**
	 * aggregate. 
	 */
	@Override
	public List<Entry> aggregate() {
		for (UrlEntry urlEntry: urlEntries) {
			SyndFeedInfo feedInfo = cache.getFeedInfo(urlEntry.getUrl());
			cache.setFeedInfo(urlEntry.getUrl(), feedInfo);
		}
		FeedFetcher feedFetcher = new HttpURLFeedFetcher(cache);
		List<Entry> entries = new ArrayList<Entry>();
		try {
			for (UrlEntry urlEntry: urlEntries) {
				SyndFeed feed = feedFetcher.retrieveFeed(urlEntry.getUrl());
				if (feed == null) {
					logger.warn("feed is null: {}", urlEntry.getUrl());
					continue;
				}
				for (Iterator<?> it = feed.getEntries().iterator(); it.hasNext(); ) {
					SyndEntry entry = (SyndEntry) it.next();
					entries.add(new SyndEntryMessage(entry, urlEntry.getHashTags()));
				}
				// feed は最新のentryが先頭になるので reverseする。
				Collections.reverse(entries);
			}
		} catch (Exception e) {
			throw new BotterException(e);
		}
		return entries;
	}
}
