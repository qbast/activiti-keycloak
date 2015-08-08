# activiti-keycloak


This module allows using Keycloak authentication server as backend for Activiti user and groups. By default all Keycloak users from a single realm
are mapped to Activiti users and all Keycloak roles are mapped to Activiti assignment groups. 

It does not provide however authentication support - directly validatinging user name and password is not well suited for OAuth2-based
authentication. For this use Keycloak adapter for spring security.
It also does not support user/group management (create, delete, edit) - this should be done in Keycloak console.

To enable the integration, create instance of KeycloakConfigurator, set necessary properties (Keycloak server url, realm name,
username/password for admin user) and pass it to ProcessEngineConfiguration bean.
Note that the user should have view-users role assigned from realm-management client.

TODO:
- caching mechanism for groups
- abiliti to select which Keycloak roles should be mapped to Activiti groups
- support for more queries

