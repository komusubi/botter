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
package jp.dip.komusubi.botter.gae.module.jal5971;

import static jp.dip.komusubi.botter.gae.GaeContext.CONTEXT;

import java.util.Calendar;
import java.util.Date;

import jp.dip.komusubi.botter.Resolver;
import jp.dip.komusubi.botter.gae.ParseException;

import org.apache.commons.lang.time.DateFormatUtils;

/**
 * 
 * @author jun.ozeki 
 * @since 2009/12/21
 * @version $Id: EntryParser.java 1356 2010-12-31 05:13:01Z jun $
 */
public class EntryParser {
	private static final long serialVersionUID = -5904118228601201654L;
	private static final String[] LEFT_BRACES = { "【", "≪" };
	private static final String[] RIGHT_BRACES = { "】", "≫" };
	private static final String KEYWORD_TOMORROW = "明日";
	private static final String KEYWORD_DAY = "日";
//	private AbstractFactory factory = TwitteeContext.SINGLETON.getTwitteeFactory();
//	private long id;
	
	// findBrace
//	private int findBrace(String content, Date date) {
//		String leftBrace = null;
//		int index = 0;
//		for (int i = 0; i < LEFT_BRACES.length; i++) {
//			if (content.contains(LEFT_BRACES[i] + KEYWORD_TOMORROW + 
//					DateUtils.formatDate(date, "d") + KEYWORD_DAY + LEFT_BRACES[i])) {
//				leftBrace = LEFT_BRACES[i];
//				index = i;
//				break;
//			}
//		}
//		if (leftBrace == null)
//			throw new ParseException("left braceが見つかりません。");
//		return index;
//	}
	
//	/**
//	 * コンストラクタ。
//	 * @param id primary key
//	 */
//	protected ScraperMessage(long id) {
//		this.id = id;  
//	}
	
	/**
	 * constructor.
	 */
	protected EntryParser() {
//		resolver = 
	}
	
//	@Override
//	public long getId() {
//		return id;
//	}
	
	protected String getDateHeading(String content) {
		Resolver<Date> resolver = CONTEXT.getResolverManager().getDateResolver();
		Calendar cal = Calendar.getInstance();
		cal.setTime(resolver.resolve());
		cal.add(Calendar.DAY_OF_MONTH, 1);
		
		String dateHeading = null;
		boolean find = false;
		for (int i = 0; i < LEFT_BRACES.length; i++) {
			dateHeading = LEFT_BRACES[i] + KEYWORD_TOMORROW + 
			DateFormatUtils.format(cal, "d") + KEYWORD_DAY + RIGHT_BRACES[i];
			if (content.contains(dateHeading)) {
				find = true;
				break;
			}
		}
		if (!find)
			throw new ParseException("left braceが見つかりません。: " + content);
		return dateHeading;
	}
	
	/**
	 * 
	 * @param content
	 * @param tag
	 * @return
	 */
	protected String parse(String content, String tag) {
		String keyword = getDateHeading(content);
		String block = content.substring(content.indexOf(keyword)).replaceAll("\\r\\n", "");
		String piece;
		if (block.contains(tag))
			piece = block.substring(0, block.indexOf(tag));
		else
			throw new ParseException("\"" + tag + "\"の終了デリミタの解析が出来ません。 : " + block);
		return piece;
	}
	
//	@Override
//	public boolean equals(Object obj) {
//		if (!(obj instanceof Entry))
//			return false;
//		Entry entry = (Entry) obj;
//		if (entry.getText() == null)
//			return getText() == null;
//		else
//			return message.getMessage().equals(getMessage());
//	}

}
