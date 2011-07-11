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
package jp.dip.komusubi.botter.util;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import jp.dip.komusubi.botter.BotterException;
import jp.dip.komusubi.botter.UrlUtil;

import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.net.URLCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jun.ozeki
 * @version $Id: BitlyUrlUtil.java 1345 2010-07-18 14:31:51Z jun $
 * @since 2010/07/18
 */
public class BitlyUrlUtil implements UrlUtil {
	private static final Logger logger = LoggerFactory.getLogger(BitlyUrlUtil.class);
	private static final String BASE_URL = "http://api.bit.ly";
	private static final String API_VERSION = "v3";
	private static final String SERVICE_URL = BASE_URL + "/" + API_VERSION;
	private static final String SHORTEN_SERVICE_PATH = "shorten";
	private static final String SHORTEN_SERVICE_URL = SERVICE_URL + "/" + SHORTEN_SERVICE_PATH;
	private static final String PARAMETER_NAME_API_KEY = "apiKey";
	private static final String PARAMETER_NAME_LOGIN = "login";
	private static final String PARAMETER_NAME_FORMAT = "format";
	private static final String PARAMETER_NAME_LONGURL = "longUrl";
	private Map<String, String> parameters = new HashMap<String, String>();
	
	/**
	 * コンストラクタ。
	 * @param id
	 * @param password
	 */
	public BitlyUrlUtil(String id, String apiKey) {
		parameters.put(PARAMETER_NAME_LOGIN, id);
		parameters.put(PARAMETER_NAME_API_KEY, apiKey);
		parameters.put(PARAMETER_NAME_FORMAT, "txt");
	}
	
	@Override
	public String shorten(String url) {
		parameters.put(PARAMETER_NAME_LONGURL, url);
		StringBuilder builder = new StringBuilder(SHORTEN_SERVICE_URL);
		URLCodec codec = new URLCodec();
		
		String responseLine = null;
		boolean done = false;
		for (Entry<String, String> entry: parameters.entrySet()) {
			if (done == false) {
				builder.append("?");
				done = true;
			} else {
				builder.append("&");
			}
			try {
				builder.append(codec.encode(entry.getKey()))
						.append("=")
						.append(codec.encode(entry.getValue()));
			} catch (EncoderException e) {
				throw new BotterException(e);
			}
		}
		responseLine = UrlAccessDelegate.readFirstLine(builder.toString());
		if (responseLine == null)
			logger.warn("bit.ly response is null, URL is :{}", builder.toString());
		return responseLine;
	}

	@Override
	public String shorten(URL url) {
		return shorten(url.toExternalForm());
	}

}
