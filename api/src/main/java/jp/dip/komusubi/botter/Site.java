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
package jp.dip.komusubi.botter;

import java.io.Serializable;
import java.net.URL;
import java.util.List;


/**
 * 
 * @author jun.ozeki 
 * @since 2009/11/07
 * @version $Id: Site.java 1321 2010-01-11 16:14:14Z ozeki $
 */
public interface Site extends Serializable {
	URL getUrl();
	String getTitle();
	List<String> getHashTags();
	//	private URL url;
//	private String title;
//	
//	/**
//	 * コンストラクタ。
//	 * @param url
//	 * @param title
//	 */
//	public Site(URL url, String title) {
//		this.url = url;
//		this.title = title;
//	}
//	public URL getUrl() {
//		return url;
//	}
//	public Site setUrl(URL url) {
//		this.url = url;
//		return this;
//	}
//	public String getTitle() {
//		return title;
//	}
//	public Site setTitle(String title) {
//		this.title = title;
//		return this;
//	}
//	@Override
//	public String toString() {
//		return ToStringBuilder.reflectionToString(this);
//	}
}
