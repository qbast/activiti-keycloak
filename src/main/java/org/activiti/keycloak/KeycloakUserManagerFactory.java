package org.activiti.keycloak;

import org.activiti.engine.impl.interceptor.Session;
import org.activiti.engine.impl.interceptor.SessionFactory;
import org.activiti.engine.impl.persistence.entity.UserIdentityManager;

/**
 * Factory that creates Keycloak user manager. This class usually does not need to be used directly, create KeycloakConfigurator instead 
 * 
 * @author Jakub Stachowski
 *
 */
public class KeycloakUserManagerFactory implements SessionFactory {
	final private KeycloakConfigurator configurator;

	public KeycloakUserManagerFactory(KeycloakConfigurator configurator) {
		this.configurator=configurator;
	}
	
	public Class<?> getSessionType() {
		return UserIdentityManager.class;
	}

	
	public Session openSession() {
		return new KeycloakUserManager(configurator.getRealm().users());
	}

}
