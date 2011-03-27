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
package jp.dip.komusubi.botter.gae.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import jp.dip.komusubi.botter.api.Bird;
import jp.dip.komusubi.botter.gae.model.Aggregator;
import jp.dip.komusubi.botter.gae.model.Entry;
import jp.dip.komusubi.botter.gae.module.FeedAggregator;
import jp.dip.komusubi.botter.gae.module.FeedAggregator.UrlEntry;
import jp.dip.komusubi.botter.gae.module.HtmlAggregator;
import jp.dip.komusubi.botter.gae.service.jal5971.FlightOverviewScraper;

/**
 * @author jun.ozeki
 * @version $Id: Jal5971Resource.java 1363 2011-03-06 02:04:40Z jun $
 * @since 2010/12/26
 */
@Path("/bird/jal5971")
public class Jal5971Resource {

	private Bird bird;
	// code point base count
	private static final int TWEET_MESSAGE_MAX_SIZE = 140; 
	
	@Inject
	public Jal5971Resource(Bird bird) {
		this.bird = bird;
	}
	
	@POST
//	@Produces(MediaType.APPLICATION_ATOM_XML)
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/feeder")
	public void feeder() {
//		Arrays.asList(
//				new UrlEntry()
		tweet(new FeedAggregator(new UrlEntry("http://rss.jal.co.jp/f4728/index.rdf", "#JAL")));
	}
	
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/scraper")
	public void scraper() {
		tweet(new HtmlAggregator(new FlightOverviewScraper()));
	}
	
	private void tweet(Aggregator aggregator) {
		List<Entry> list = aggregator.aggregate();
		for (Entry entry: list)
			tweet(entry.getText());
	}
	
	@GET
	@Path("/{id}")
	@Produces(MediaType.TEXT_PLAIN)
	public String hear(@PathParam("id") String id) {
		return "hello world " + id;
	}
	
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/tweet")
	public void tweet(String text) {
		for (String message: chunk(text, TWEET_MESSAGE_MAX_SIZE)) 
			bird.tweet(message);
	}
	
	protected List<String> chunk(String text, int size) {
		String paging = "(%d/%d) ";
		if (text == null)
			return null;
		if (size < 0)
			throw new StringIndexOutOfBoundsException(size);
		if (text.length() <= size)
			return Arrays.asList(new StringBuilder(paging).append(text).toString());

		char period = "。".charAt(0);
		char point = "、".charAt(0);
		boolean page = false;
		List<String> array = new ArrayList<String>();
		StringBuilder builder = null;
		String remain = null;
		int lastIndex = -1;
		for (int i = 0, count = 0; i < text.length(); i++) {
			char c = text.charAt(i);
			if (!page) {
				builder = new StringBuilder(paging);
				if (remain != null)
					builder.append(remain);
				count = builder.codePointCount(0, builder.length());
				page = true;
				remain = null;
			}
			builder.append(c);
			if (!Character.isHighSurrogate(c)) 
				count++;
			if (period == c || point == c) 
				lastIndex = builder.codePointCount(0, builder.length());

			if (size <= count) {
				if (lastIndex != -1) {
					array.add(builder.substring(0, lastIndex));
					remain = builder.substring(lastIndex);
					lastIndex = -1;
				} else {
					array.add(builder.toString());
				} 
				page = false;
			}
		}
		if (builder.length() != 0)
			array.add(builder.toString());
		// 100個以上に分割された場合には page"(%d/%d)"の表記が想定文字数をオーバー
		// する可能性があるため例外をスローしておく。
		if (array.size() >= 100)
			throw new IllegalStateException("array size over 100.");
		
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < array.size(); i++) {
			list.add(String.format(array.get(i), i + 1, array.size()));
		}
		return list;
	}
}
