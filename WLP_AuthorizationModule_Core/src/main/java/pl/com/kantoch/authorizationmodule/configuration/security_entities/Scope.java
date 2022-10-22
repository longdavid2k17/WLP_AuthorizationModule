package pl.com.kantoch.authorizationmodule.configuration.security_entities;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import pl.com.kantoch.authorizationmodule.configuration.security_entities.user.User;
import pl.com.kantoch.serialization.LocalDateTimeDeserializer;
import pl.com.kantoch.serialization.LocalDateTimeSerializer;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "scopes",uniqueConstraints = {
        @UniqueConstraint(columnNames = "name")
})
public class Scope {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @NotBlank
    @Size(max = 128)
    @Column(name = "name")
    private String name;

    @NotNull
    @NotBlank
    @Size(max = 40)
    @Column(name = "display_name")
    private String displayName;

    @NotBlank
    @Size(max = 512)
    @Column(name = "description")
    private String description;

    @Column(name = "enabled")
    @NotNull
    private Boolean isEnabled;

    @NotNull
    @Column(name = "creation_date")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime creationDate;

    @ManyToOne
    @JoinColumn(name="creation_user_id", nullable=false)
    private User creationUser;

    @NotNull
    @Column(name = "modification_date")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime modificationDate;

    @ManyToOne
    @JoinColumn(name="modification_user_id", nullable=false)
    private User modificationUser;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(	name = "scope_permissions",
            joinColumns = @JoinColumn(name = "scope_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private Set<Permission> permissionSet = new HashSet<>();

    public Scope() {
    }

    public Scope(Long id, String name, String displayName, String description, Boolean isEnabled, LocalDateTime creationDate, User creationUser, LocalDateTime modificationDate, User modificationUser, Set<Permission> permissionSet) {
        this.id = id;
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.isEnabled = isEnabled;
        this.creationDate = creationDate;
        this.creationUser = creationUser;
        this.modificationDate = modificationDate;
        this.modificationUser = modificationUser;
        this.permissionSet = permissionSet;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Boolean getEnabled() {
        return isEnabled;
    }

    public void setEnabled(Boolean enabled) {
        isEnabled = enabled;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public User getCreationUser() {
        return creationUser;
    }

    public void setCreationUser(User creationUser) {
        this.creationUser = creationUser;
    }

    public LocalDateTime getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(LocalDateTime modificationDate) {
        this.modificationDate = modificationDate;
    }

    public User getModificationUser() {
        return modificationUser;
    }

    public void setModificationUser(User modificationUser) {
        this.modificationUser = modificationUser;
    }

    public Set<Permission> getPermissionSet() {
        return permissionSet;
    }

    public void setPermissionSet(Set<Permission> permissionSet) {
        this.permissionSet = permissionSet;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Scope scope = (Scope) o;
        return Objects.equals(id, scope.id) && Objects.equals(name, scope.name) && Objects.equals(displayName, scope.displayName) && Objects.equals(description, scope.description) && Objects.equals(isEnabled, scope.isEnabled) && Objects.equals(creationDate, scope.creationDate) && Objects.equals(creationUser, scope.creationUser) && Objects.equals(modificationDate, scope.modificationDate) && Objects.equals(modificationUser, scope.modificationUser) && Objects.equals(permissionSet, scope.permissionSet);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, displayName, description, isEnabled, creationDate, creationUser, modificationDate, modificationUser, permissionSet);
    }

    @Override
    public String toString() {
        return "Scope{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", displayName='" + displayName + '\'' +
                ", description='" + description + '\'' +
                ", isEnabled=" + isEnabled +
                ", creationDate=" + creationDate +
                ", creationUser=" + creationUser +
                ", modificationDate=" + modificationDate +
                ", modificationUser=" + modificationUser +
                ", permissionSet=" + permissionSet +
                '}';
    }
}
