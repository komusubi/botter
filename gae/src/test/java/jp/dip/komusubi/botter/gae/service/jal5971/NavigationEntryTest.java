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

import jp.dip.komusubi.botter.gae.GaeContextFactory;

import org.junit.Before;
import org.junit.Test;



/**
 * @author jun.ozeki
 * @version $Id$
 * @since 2011/03/27
 */
public class NavigationEntryTest {

	private NavigationEntry target;
	
	@Before
	public void before() {
		new GaeContextFactory().initializeContext();
	}
	
	@Test
	public void インスタンス() {
		String content = "≪東北地方太平洋沖地震の影響による運航便情報について≫"
			+ "3月11日に発生した地震の影響により、仙台空港の再開日程が未確定のため、"
			+ "仙台空港発着便については、4月28日まで全便欠航が決定しております。"
			+ "空港再開の目処が立ちましたら、速やかに、新たな運航便を設定いたします。"
			+ "\n"
			+ "なお、計画停電の影響により、空港へ向かう地上交通機関に運休・減便等の影響が懸念されておりますので、"
			+ "ご利用のお客さまは、お早めに空港へお出かけ下さいますようお願い申し上げます。"
			+ "また、福島上空の飛行ルートを通る一部の運航便につきましては、航空管制からの指示に基づき、"
			+ "福島第一原子力発電所から半径30Km上空を迂回した航路にて安全に運航しております。"
			+ "ご利用のお客さまは、最新の運航状況を国内線発着案内にてご確認ください。"
			+ "\r\n"
			+ "お客さまには大変ご迷惑をおかけいたしますことをお詫び申し上げます。"
			+ "\r\n"
			+ "また、その他の本日27日および明日28日の運航状況は、以下のとおりです。"
			+ "\r\n"
			+ "≪本日24日≫"
			+ "\r\n"
			+ "【天気概況】"
			+ "\r\n"
			+ "上空の寒気の影響で北日本の日本海側では雪が降りやすくなっていますが、大きな崩れはない見込みです。"
			+ "西日本は高気圧に覆われ、良好な天候が見込まれます。"
			+ "\r\n"
			+ "【運航概況】"
			+ "\r\n"
			+ "本日は、平常どおりの運航を予定しています。"
			+ "\r\n"
			+ "\r\n"
			+ "≪明日25日≫"
			+ "\r\n"
			+ "【天気概況】"
			+ "\r\n"
			+ "西日本や東日本は高気圧に覆われ、良好な天候が見込まれます。"
			+ "北日本の日本海側では、上空の寒気の影響で降雪が予想され、一時的に強く降るところがあります。"
			+ "\r\n"
			+ "【運航概況】"
			+ "\r\n"
			+ "以下の空港は、遅延、欠航、出発空港への引き返し、他空港への着陸の可能性がある空港です。"
			+ "\r\n"
			+ "秋田（降雪）"
 			+ "その他は、現在のところ平常どおりの運航を予定しています。"
 			+ "\r\n"
 			+ "\r\n"
 			+ "◆次回のご案内は明朝を予定しております。"
 			+ "状況により、掲載時間が変更になる可能性がございます。"
 			+ "その他、個別の便毎の発着状況については、「国内線　発着案内」をご確認ください。";
		target = new NavigationEntry("http://example.jp", content, "hashTag1", "hashTag2");
		System.out.println(target.getText());
	}
}
