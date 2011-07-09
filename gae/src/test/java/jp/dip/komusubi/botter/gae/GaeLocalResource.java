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

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.junit.rules.ExternalResource;

import com.google.appengine.tools.development.ApiProxyLocalFactory;
import com.google.appengine.tools.development.LocalServerEnvironment;
import com.google.apphosting.api.ApiProxy;

/**
 * @author jun.ozeki
 * @version $Id$
 * @since 2011/05/24
 */
public class GaeLocalResource extends ExternalResource {

	@Override
	public void before() {
		ApiProxy.setEnvironmentForCurrentThread(new TestEnvironment());
		ApiProxy.setDelegate(new ApiProxyLocalFactory().create(new LocalServerEnvironment() {
			
			@Override
			public void waitForServerToStart() throws InterruptedException {
				
			}
			
			@Override
			public int getPort() {
				return 0;
			}
			
			@Override
			public File getAppDir() {
				return new File(".");
			}
			
			@Override
			public String getAddress() {
				return null;
			}

			@Override
			public boolean enforceApiDeadlines() {
				return false;
			}

			@Override
			public boolean simulateProductionLatencies() {
				return false;
			}
		}));
	}

	@Override
	public void after() {
		ApiProxy.setDelegate(null);
		ApiProxy.setEnvironmentForCurrentThread(null);
	}

	/**
	 * 
	 * @author jun.ozeki
	 * @version $Id$
	 * @since 2011/05/24
	 */
	private static class TestEnvironment implements ApiProxy.Environment {

		public String getAppId() {
			return "Unit Tests";
		}

		public String getVersionId() {
			return "1.0";
		}

		public void setDefaultNamespace(String s) {
		}

		public String getRequestNamespace() {
			return null;
		}

		public String getDefaultNamespace() {
			return null;
		}

		public String getAuthDomain() {
			return null;
		}

		public boolean isLoggedIn() {
			return false;
		}

		public String getEmail() {
			return null;
		}

		public boolean isAdmin() {
			return false;
		}

		@Override
		public Map<String, Object> getAttributes() {
			Map<String, Object> map = new HashMap<String, Object>();
			return map;
		}
	}
}
