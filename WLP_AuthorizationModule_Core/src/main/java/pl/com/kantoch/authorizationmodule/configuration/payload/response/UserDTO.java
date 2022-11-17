package pl.com.kantoch.authorizationmodule.configuration.payload.response;

import pl.com.kantoch.authorizationmodule.configuration.security_entities.role.Role;
import pl.com.kantoch.authorizationmodule.configuration.security_entities.user.User;

import java.util.Set;

public class UserDTO {
    private Long id;
    private String username;
    private String email;
    private String name;
    private String lastName;
    private Boolean active;
    private Set<Role> roles;

    public UserDTO(Long id, String username, String email, String name, String lastName, Boolean active, Set<Role> roles) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.name = name;
        this.lastName = lastName;
        this.active = active;
        this.roles = roles;
    }

    public UserDTO() {
    }

    public static UserDTO build(User user) {
        return new UserDTO(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getName(),
                user.getLastName(),
                user.getActive(),
                user.getRoles());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", lastName='" + lastName + '\'' +
                ", active=" + active +
                ", roles=" + roles +
                '}';
    }
}
