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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import jp.dip.komusubi.botter.BotterException;

/**
 * @author jun.ozeki
 * @version $Id: UrlAccessDelegate.java 1345 2010-07-18 14:31:51Z jun $
 * @since 2010/07/18
 */
class UrlAccessDelegate {

	static String readFirstLine(String url) {
		BufferedReader reader = null;
		try {
			reader = getBufferedReader(url);
			return reader.readLine();
		} catch (IOException e) {
			throw new BotterException(e);
		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (IOException e) {
				// ignore
			}
		}
	}
	
	static BufferedReader getBufferedReader(String url) {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(new URL(url).openStream()));
		} catch (IOException e) {
			throw new BotterException(e);
		}
		return reader;
	}
}
