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
package jp.dip.komusubi.botter.gae;

import jp.dip.komusubi.botter.BotterException;


/**
 * @author jun.ozeki
 * @version $Id$
 * @since 2010/08/21
 */
public class ParseException extends BotterException {
	private static final long serialVersionUID = -8227401245179801896L;

	/**
	 * コンストラクタ。
	 */
	public ParseException( ) {

	}
	
	/**
	 * コンストラクタ。
	 * @param aMessage メッセージ
	 */
	public ParseException(String aMessage) {
		super(aMessage);
	}
	
	/**
	 * コンストラクタ
	 * @param aThrowable 
	 */
	public ParseException(Throwable aThrowable) {
		super(aThrowable);
	}
	
	/**
	 * コンストラクタ。
	 * @param aMessage メッセージ
	 * @param aThrowable
	 */
	public ParseException(String aMessage, Throwable aThrowable) {
		super(aMessage, aThrowable);
	}

}
