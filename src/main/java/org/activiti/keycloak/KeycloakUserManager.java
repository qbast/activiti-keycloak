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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.Picture;
import org.activiti.engine.identity.User;
import org.activiti.engine.identity.UserQuery;
import org.activiti.engine.impl.Page;
import org.activiti.engine.impl.UserQueryImpl;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.persistence.AbstractManager;
import org.activiti.engine.impl.persistence.entity.IdentityInfoEntity;
import org.activiti.engine.impl.persistence.entity.UserEntity;
import org.activiti.engine.impl.persistence.entity.UserIdentityManager;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User manager that retrieves user list from Keycloak server. Many methods are not implemented:
 * 1) all write operations 
 * 2) querying by anything other than user id
 * 3) checking user password
 * 
 * @author Jakub Stachowski
 *
 */
public class KeycloakUserManager extends AbstractManager implements UserIdentityManager {
	private static Logger logger = LoggerFactory.getLogger(KeycloakUserManager.class);	
	final private UsersResource usersResource;
	
	public KeycloakUserManager(UsersResource usersResource) {
		this.usersResource=usersResource;
	}

	public User createNewUser(String userId) {
		throw new ActivitiException("Write operations are not supported by Keycloak user manager");
	}

	public void insertUser(User user) {
		throw new ActivitiException("Write operations are not supported by Keycloak user manager");
	}

	public void updateUser(User updatedUser) {
		throw new ActivitiException("Write operations are not supported by Keycloak user manager");
	}

	public User findUserById(String userId) {
		UserRepresentation user=usersResource.get(userId).toRepresentation();
		return toUser(user);
	}
	
	public User toUser(UserRepresentation user) {
		if (user==null) return null;
		UserEntity ret=new UserEntity();
		ret.setEmail(user.getEmail());
		ret.setFirstName(user.getFirstName());
		ret.setLastName(user.getLastName());
		ret.setId(user.getId());
		return ret;
		
	}

	public void deleteUser(String userId) {
		throw new ActivitiException("Write operations are not supported by Keycloak user manager");
	}

	public List<User> findUserByQueryCriteria(UserQueryImpl query, Page page) {
		List<User> ret=new ArrayList<User>();
		if (query.getId()!=null) {
			User byId=findUserById(query.getId());
			if (byId!=null) ret.add(byId);
		} else {
			logger.warn("Unknown query type, returning all users");
			List<UserRepresentation> users=usersResource.search("", 0, 100); // FIXME: paging
			for (UserRepresentation r : users)
				ret.add(toUser(r));
		}
		return ret;
			
		
	}

	public long findUserCountByQueryCriteria(UserQueryImpl query) {
		return findUserByQueryCriteria(query, null).size();
	}

	public List<Group> findGroupsByUser(String userId) {
		throw new ActivitiException("Query unsupported by Keycloak user manager");
	}

	public UserQuery createNewUserQuery() {
	    return new UserQueryImpl(Context.getProcessEngineConfiguration().getCommandExecutor());
	}

	public IdentityInfoEntity findUserInfoByUserIdAndKey(String userId, String key) {
		throw new ActivitiException("Query unsupported by Keycloak user manager");
	}

	public List<String> findUserInfoKeysByUserIdAndType(String userId, String type) {
		throw new ActivitiException("Query unsupported by Keycloak user manager");
	}

	public Boolean checkPassword(String userId, String password) {
		logger.warn("Keycloak user manager does not support checking password");
		return false;
	}

	public List<User> findPotentialStarterUsers(String proceDefId) {
		throw new ActivitiException("Query unsupported by Keycloak user manager");
	}

	public List<User> findUsersByNativeQuery(Map<String, Object> parameterMap, int firstResult, int maxResults) {
		throw new ActivitiException("Native queries are not supported by Keycloak user manager");
	}

	public long findUserCountByNativeQuery(Map<String, Object> parameterMap) {
		throw new ActivitiException("Native queries are not supported by Keycloak user manager");
	}

	public boolean isNewUser(User user) {
		throw new ActivitiException("Write operations are not supported by Keycloak user manager");
	}

	public Picture getUserPicture(String userId) {
		return null;
	}

	public void setUserPicture(String userId, Picture picture) {
		throw new ActivitiException("Write operations are not supported by Keycloak user manager");
	}

}
