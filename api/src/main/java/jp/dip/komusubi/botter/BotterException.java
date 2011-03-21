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

/**
 * 
 * @author jun.ozeki 
 * @since 2009/09/10
 * @version $Id: TwitteeException.java 1321 2010-01-11 16:14:14Z ozeki $
 */
public class BotterException extends RuntimeException {
	private static final long serialVersionUID = -3505569058140944695L;

	/**
	 * コンストラクタ。
	 */
	public BotterException( ) {

	}
	
	/**
	 * コンストラクタ。
	 * @param aMessage メッセージ
	 */
	public BotterException(String aMessage) {
		super(aMessage);
	}
	
	/**
	 * コンストラクタ
	 * @param aThrowable 
	 */
	public BotterException(Throwable aThrowable) {
		super(aThrowable);
	}
	
	/**
	 * コンストラクタ。
	 * @param aMessage メッセージ
	 * @param aThrowable
	 */
	public BotterException(String aMessage, Throwable aThrowable) {
		super(aMessage, aThrowable);
	}

}
