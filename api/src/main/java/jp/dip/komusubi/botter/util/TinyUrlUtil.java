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

import jp.dip.komusubi.botter.BotterException;
import jp.dip.komusubi.botter.UrlUtil;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.net.URLCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author jun.ozeki
 * @version $Id: TinyUrlUtil.java 1358 2011-01-10 04:16:13Z jun $
 * @since 2010/07/18
 */
public class TinyUrlUtil implements UrlUtil {
	private static final Logger logger = LoggerFactory.getLogger(TinyUrlUtil.class);
	private static final String BASE_URL = "http://tinyurl.com";
	private static final String SHORTEN_SERVICE_PATH = "api-create.php";
	private static final String SHORTEN_SERVICE_URL = BASE_URL + "/" + SHORTEN_SERVICE_PATH;
	
	@Override
	public String shorten(String url) {
		String responseLine = null;
		try {
			URLCodec codec = new URLCodec();
			String requestUrl = SHORTEN_SERVICE_URL + "?url=" + codec.decode(url);
			responseLine = UrlAccessDelegate.readFirstLine(requestUrl);
			if (responseLine == null)
				logger.warn("tinyurl response is null, URL is :{}", requestUrl);
		} catch (DecoderException e) {
			throw new BotterException(e);
		}
		return responseLine;
	}

	@Override
	public String shorten(URL url) {
		return shorten(url.toExternalForm());
	}

}
