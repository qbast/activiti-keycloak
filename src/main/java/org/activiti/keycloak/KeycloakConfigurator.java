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

import java.util.Arrays;
import java.util.List;

import org.activiti.engine.cfg.AbstractProcessEngineConfigurator;
import org.activiti.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.activiti.engine.impl.interceptor.SessionFactory;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;

/**
 * Configurer that integrates Keycloak (http://keycloak.jboss.org/) authentication server with Activiti users and groups. 
 * Keycloak will be queries about user information, list of groups and user to group assignment. It is not however useful for user/group management
 * (creating, deleting, updating) which should be done by Keycloak console. It also does not support checking user password.
 * @author Kuba
 *
 */
public class KeycloakConfigurator extends AbstractProcessEngineConfigurator {
	private Keycloak client;
	private RealmResource realm;
	private String clientId;
	private String serverUrl;
	private String realmName;
	private String username;
	private String password;
	
	public void beforeInit(ProcessEngineConfigurationImpl processEngineConfiguration) {
		
	}

	public void configure(ProcessEngineConfigurationImpl processEngineConfiguration) {
		List<SessionFactory> factories=Arrays.asList(
				new KeycloakUserManagerFactory(this),
				new KeycloakMembershipManagerFactory(), 
				new KeycloakGroupManagerFactory(this));
		
		for (SessionFactory f : factories)
			processEngineConfiguration.getSessionFactories().put(f.getSessionType(), f);
	}
	
	public Keycloak getClient() {
		if (client==null)
			client=Keycloak.getInstance(serverUrl, realmName, username, password, clientId);
		return client;
	}
	
	public RealmResource getRealm() {
		if (realm==null)
			realm=getClient().realm(realmName);
		return realm;
	}
	
	public String getClientId() {
		return clientId;
	}
	
	public void setClient(Keycloak client) {
		this.client = client;
	}
	
	/**
	 * Set id of client used to authenticate with Keycloak admin REST interface
	 * @param clientId
	 */
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	/**
	 * Set password used for accessing Keycloak admin REST interface
	 * @param password
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	
	public void setRealm(RealmResource realm) {
		this.realm = realm;
	}
	
	/**
	 * Set Keycloak realm name 
	 * @param realmName
	 */
	public void setRealmName(String realmName) {
		this.realmName = realmName;
	}
	
	/**
	 * Url of keycloak server. 
	 * For example: http://localhost:8080/auth
	 * @param serverUrl
	 */
	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}
	
	/**
	 * User name for accessing Keycloak admin REST interface. The user needs view-users role assigned from realm-management client
	 * @param username
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	

}
