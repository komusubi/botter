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

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import jp.dip.komusubi.botter.Bird;
import jp.dip.komusubi.botter.Status;
import jp.dip.komusubi.botter.gae.model.Aggregator;
import jp.dip.komusubi.botter.gae.model.Entry;
import jp.dip.komusubi.botter.gae.model.Job;
import jp.dip.komusubi.botter.gae.module.JobManager;
import jp.dip.komusubi.botter.gae.module.JobManagerProvider;
import jp.dip.komusubi.botter.gae.module.aggregator.FeedAggregator;
import jp.dip.komusubi.botter.gae.module.aggregator.FeedAggregator.UrlEntry;
import jp.dip.komusubi.botter.gae.module.aggregator.HtmlAggregator;
import jp.dip.komusubi.botter.gae.module.jal5971.FlightOverviewScraper;

import org.apache.abdera.Abdera;
import org.apache.abdera.model.Feed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jun.ozeki
 * @version $Id: Jal5971Resource.java 1363 2011-03-06 02:04:40Z jun $
 * @since 2010/12/26
 */
@Path("/bird/jal5971")
public class Jal5971Resource {
	private static final Logger logger = LoggerFactory.getLogger(Jal5971Resource.class);
	private static final int TWEET_MESSAGE_MAX_SIZE = 140; 
	private Bird bird;
	// code point base count
	private Abdera abdera = Abdera.getInstance();
	@Context UriInfo uriInfo;
	private JobManagerProvider jobManagerProvider;
	
	@Inject
	public Jal5971Resource(Bird bird, Provider<JobManagerProvider> provider) {
		this.bird = bird;
		this.jobManagerProvider= provider.get();
	}
	
	@POST
	@Produces({MediaType.APPLICATION_ATOM_XML, MediaType.APPLICATION_JSON})
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Path("/feeder")
	public Feed feeder(MultivaluedMap<String, String> formParam) {
		if (logger.isDebugEnabled()) {
			logger.info("url is {}", formParam.get("url"));
			logger.info("tag is {}", formParam.get("tag"));
		}
		List<String> tags = formParam.get("tag"); 
		if (tags == null)
			tags = new ArrayList<String>();

		Feed feed = abdera.newFeed();
		feed.addAuthor(formParam.getFirst("author"),
						formParam.getFirst("email"),
						uriInfo.getAbsolutePath().toASCIIString());
		if (formParam.get("url") == null || formParam.get("url").size() > 1) {
			Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE);
			return feed;
		}
		FeedAggregator aggregator = new FeedAggregator(
				new UrlEntry(formParam.getFirst("url"), tags.toArray(new String[0]))); 
		
		return toFeed(feed, aggregator, tags);
	}
	
	private Feed toFeed(Feed feed, Aggregator aggregator, List<String> tags) {
		for (Entry e: aggregator.aggregate()) {
			org.apache.abdera.model.Entry entry = abdera.newEntry();
			entry.setId(String.valueOf(e.getId()));
			entry.setContent(e.getText());
			entry.setPublished(e.getDate());
			for (String tag: tags)
				entry.addCategory(tag);
			entry.addLink(e.getUrl().toExternalForm());
			feed.addEntry(entry);
		}
		return feed;
	}
	
	@POST
	@Produces(MediaType.APPLICATION_ATOM_XML)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Path("/scraper")
	public Feed scraper(MultivaluedMap<String, String> formParam) {
		if (logger.isDebugEnabled()) {
		}
		List<String> tags = formParam.get("tag");
		if (tags == null)
			tags = new ArrayList<String>();
		
		Feed feed = abdera.newFeed();
		feed.addAuthor(formParam.getFirst("author"),
						formParam.getFirst("email"),
						uriInfo.getAbsolutePath().toASCIIString());
//		if (formParam.get("url") == null || formParam.get("url").size() > 1) {
//			Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE);
//			return feed;
//		}
		HtmlAggregator aggregator = new HtmlAggregator();
		aggregator.add(new FlightOverviewScraper(tags.toArray(new String[0])));
		
		return toFeed(feed, aggregator, tags);
	}
	
	private String tweet(Aggregator aggregator) {
		List<Entry> list = aggregator.aggregate();
		StringBuilder builder = new StringBuilder();
		for (Entry entry: list) {
			String result = tweet(entry.getText());
			builder.append(result);
		}
		return builder.toString();
	}
	
	@GET
	@Path("/{id}")
	@Produces(MediaType.TEXT_PLAIN)
	public String hear(@PathParam("id") String id) {
		Status status = bird.tweet("hello world " + id);
		return id;
	}
	
	@POST
	@Produces(MediaType.TEXT_PLAIN)
	@Path("/tweet")
	public String tweet(String text) {
		List<String> messages = chunk(text, TWEET_MESSAGE_MAX_SIZE);
		StringWriter writer = new StringWriter();
		if (messages.size() > 1) {
			for (String message: messages) {
				Status status = bird.tweet(message);
				writer.write(status.getText());
			}
		} else {
			Status status = bird.tweet(text);
			writer.write(status.getText());
		}
		return writer.toString();
	}

	// cron
	@GET
	@Path("/cron/{job}")
	public Response cron(@Context HttpHeaders headers, @PathParam("job") String jobId) {
		List<String> crons = headers.getRequestHeader("X-AppEngine-Cron");
		String appEngineCron = crons != null ? crons.get(0) : "false"; 
		// cron 以外からのアクセス 401 error
		if (!Boolean.valueOf(appEngineCron))
			throw new WebApplicationException(Response.Status.UNAUTHORIZED);
		JobManager jobManager = jobManagerProvider.get();
		Job job = jobManager.find(jobId);
		Response response;
		// from query parameter
		if (job.available(uriInfo.getQueryParameters())) {
			if (job.execute())
				response = Response.ok().build();
			else
				response = Response.status(Response.Status.BAD_REQUEST).build();
		} else {
			response = Response.notModified().build();
		}
		return response;
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
