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

import static org.junit.Assert.assertEquals;

import java.io.OutputStreamWriter;
import java.util.List;

import jp.dip.komusubi.botter.Bird;
import jp.dip.komusubi.botter.gae.module.ConsoleBird;

import org.junit.Before;
import org.junit.Test;


/**
 * @author jun.ozeki
 * @version $Id$
 * @since 2011/03/21
 */
public class Jal5971ResourceTest {

	private Jal5971Resource target;
	
	@Before
	public void before() {
		Bird bird = new ConsoleBird(new OutputStreamWriter(System.out));
		// FIXME jobManagerProvider のinjection
		target = new Jal5971Resource(bird, null);
	}
	
	@Test
	public void tweet() {
		target.tweet("名前の読みは「やまさき」だが、「やまざき」「ザキさん」と呼ばれることが多い。"
				+ "デビュー以来、本人は若干そのことを気にしていたらしく、以前は「やまざき」と呼ばれる度に"
				+ "「やまさきです」と訂正していた。「学級王ヤマザキ」の主題歌に歌手として起用された（後述）"
				+ "事をきっかけに、表面上はあまり気にしなくなった様子。また本名の読み方は芸名と同じ「ほうせい」である。");
	}

	@Test
	public void chunk() {
		String src = "名前の読みは「やまさき」だが、「やまざき」「ザキさん」と呼ばれることが多い。"
			+ "デビュー以来、本人は若干そのことを気にしていたらしく、以前は「やまざき」と呼ばれる度に"
			+ "「やまさきです」と訂正していた。「学級王ヤマザキ」の主題歌に歌手として起用された（後述）"
			+ "事をきっかけに、表面上はあまり気にしなくなった様子。また本名の読み方は芸名と同じ「ほうせい」である。";

		String expected[] = {"(1/7) 名前の読みは「やまさき」だが、",
							 "(2/7) 「やまざき」「ザキさん」と呼ばれることが多い。デビュー以来、",
							 "(3/7) 本人は若干そのことを気にしていたらしく、",
							 "(4/7) 以前は「やまざき」と呼ばれる度に「やまさきです」と訂正していた。",
							 "(5/7) 「学級王ヤマザキ」の主題歌に歌手として起用された（後述）事をきっ",
							 "(6/7) かけに、表面上はあまり気にしなくなった様子。",
							 "(7/7) また本名の読み方は芸名と同じ「ほうせい」である。"};
		List<String> list = target.chunk(src, 40);
		assertEquals(expected[0], list.get(0));
		assertEquals(expected[1], list.get(1));
		assertEquals(expected[2], list.get(2));
		assertEquals(expected[3], list.get(3));
		assertEquals(expected[4], list.get(4));
		assertEquals(expected[5], list.get(5));
		assertEquals(expected[6], list.get(6));
	}
}
