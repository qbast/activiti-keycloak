/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.activiti.keycloak;

import org.activiti.engine.impl.interceptor.Session;
import org.activiti.engine.impl.interceptor.SessionFactory;
import org.activiti.engine.impl.persistence.entity.GroupIdentityManager;

/**
 * Factory that creates Keycloak group manager. This class usually does not need to be used directly, create KeycloakConfigurator instead 
 * 
 * @author Jakub Stachowski
 *
 */
public class KeycloakGroupManagerFactory implements SessionFactory {
	final private KeycloakConfigurator configurator;
	
	public KeycloakGroupManagerFactory(KeycloakConfigurator configurator) {
		this.configurator=configurator;
	}

	public Class<?> getSessionType() {
		return GroupIdentityManager.class;
	}
	

	public Session openSession() {
		return new KeycloakGroupManager(configurator.getRealm().roles(),configurator.getRealm().users());
	}

}
