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
package jp.dip.komusubi.botter.gae.service.jal5971;

import static org.junit.Assert.assertEquals;
import jp.dip.komusubi.botter.gae.GaeContextFactory;

import org.junit.Before;
import org.junit.Test;


/**
 * @author jun.ozeki
 * @version $Id$
 * @since 2011/04/03
 */
public class WeatherEntryTest {
	private WeatherEntry target;
	
	@Before
	public void before() {
		GaeContextFactory.initializeContext();
	}
	
	@Test
	public void parse() {
		target = new WeatherEntry("http://example.jp", NavigationEntryTest.content, "#jal");
		
		System.out.println("vale: " + target.getText());
		String expected = "≪明日25日≫【天気概況】西日本や東日本は高気圧に覆われ、良好な天候が見込まれます。"
			+ "北日本の日本海側では、上空の寒気の影響で降雪が予想され、一時的に強く降るところがあります。 #jal";
		assertEquals(expected, target.getText());
	}
	
}
