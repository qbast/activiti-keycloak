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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.identity.Group;
import org.activiti.engine.identity.GroupQuery;
import org.activiti.engine.impl.GroupQueryImpl;
import org.activiti.engine.impl.Page;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.persistence.AbstractManager;
import org.activiti.engine.impl.persistence.entity.GroupEntity;
import org.activiti.engine.impl.persistence.entity.GroupIdentityManager;
import org.keycloak.admin.client.resource.RoleMappingResource;
import org.keycloak.admin.client.resource.RoleScopeResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.RoleRepresentation;

/**
 * Group manager that maps Keycloak roles to Activiti assignment groups. Only read-only methods are implemented as roles management should be
 * done by Keycloak console.
 * 
 * @author Jakub Stachowski
 *
 */
public class KeycloakGroupManager extends AbstractManager implements GroupIdentityManager {
	final private RolesResource rolesResource;
	final private UsersResource usersResource;
	
	public KeycloakGroupManager(RolesResource rolesResource, UsersResource usersResource) {
		this.rolesResource=rolesResource;
		this.usersResource=usersResource;
	}
	
	public Group createNewGroup(String groupId) {
		throw new ActivitiException("Write operations are not supported by Keycloak group manager");
	}

	public void insertGroup(Group group) {
		throw new ActivitiException("Write operations are not supported by Keycloak group manager");
	}

	public void updateGroup(Group updatedGroup) {
		throw new ActivitiException("Write operations are not supported by Keycloak group manager");
	}

	public void deleteGroup(String groupId) {
		throw new ActivitiException("Write operations are not supported by Keycloak group manager");
	}

	public GroupQuery createNewGroupQuery() {
	    return new GroupQueryImpl(Context.getProcessEngineConfiguration().getCommandExecutor());
	}

	public List<Group> findGroupByQueryCriteria(GroupQueryImpl query, Page page) {
		return filterGroupsByQuery(getAllGroups(), query); // TODO: paging
	}

	public long findGroupCountByQueryCriteria(GroupQueryImpl query) {
		return filterGroupsByQuery(getAllGroups(), query).size();
	}

	private List<Group> getAllGroups() {
		List<Group> ret=new ArrayList<Group>();
		List<RoleRepresentation> roles=rolesResource.list();
		for (RoleRepresentation role : roles) {
			Group g=new GroupEntity(role.getId());
			g.setName(role.getName());
			g.setType("assignment");
			ret.add(g);
		}
		return ret;		
	}
	
	private List<Group> filterGroupsByQuery(List<Group> groups, GroupQueryImpl query) {
		Set<Group> ret=new HashSet<Group>();
		Set<String> clientRoles = query.getUserId() == null ? null : getUserRoles(query.getUserId());
		
		for (Group g : groups) 
			if ((query.getId()==null || g.getId().equals(query.getId())) &&
				(query.getName()==null || g.getName().equals(query.getName())) &&
				(query.getNameLike()==null || g.getName().contains(query.getNameLike().replace("%", ""))) &&
				(clientRoles==null || clientRoles.contains(g.getId())) &&
				(query.getType()==null || g.getType().equals(query.getType())))
					ret.add(g);
			
		
		return new ArrayList<Group>(ret);
		
	}
	
	private Set<String> getUserRoles(String userId) {
		Set<String> ret=new HashSet<String>();
		UserResource user=usersResource.get(userId);
		RoleMappingResource mapping=user.roles();
		RoleScopeResource rsc=mapping.realmLevel();
		
		List<RoleRepresentation> clientRoles=rsc.listAll();
		for (RoleRepresentation role : clientRoles) {
			ret.add(role.getId());
		}
		return ret;
		
	}

	public List<Group> findGroupsByUser(String userId) {
		return filterGroupsByQuery(getAllGroups(), (GroupQueryImpl)new GroupQueryImpl().groupMember(userId));
	}

	public List<Group> findGroupsByNativeQuery(Map<String, Object> parameterMap, int firstResult, int maxResults) {
		throw new ActivitiException("Native queries are not supported by Keycloak group manager");
	}

	public long findGroupCountByNativeQuery(Map<String, Object> parameterMap) {
		throw new ActivitiException("Native queries are not supported by Keycloak group manager");	
	}

	public boolean isNewGroup(Group group) {
		throw new ActivitiException("Write operations are not supported by Keycloak group manager");
	}

}
