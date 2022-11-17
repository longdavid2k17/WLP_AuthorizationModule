package pl.com.kantoch.authorizationmodule.configuration.payload.requests;

import pl.com.kantoch.authorizationmodule.configuration.security_entities.role.Role;

import java.util.Collection;

public class GrantRolesRequest {
    private Collection<Role> roleCollection;
    private Long targetUserId;

    public GrantRolesRequest() {
    }

    public GrantRolesRequest(Collection<Role> roleCollection, Long targetUserId) {
        this.roleCollection = roleCollection;
        this.targetUserId = targetUserId;
    }

    public Collection<Role> getRoleCollection() {
        return roleCollection;
    }

    public void setRoleCollection(Collection<Role> roleCollection) {
        this.roleCollection = roleCollection;
    }

    public Long getTargetUserId() {
        return targetUserId;
    }

    public void setTargetUserId(Long targetUserId) {
        this.targetUserId = targetUserId;
    }
}
