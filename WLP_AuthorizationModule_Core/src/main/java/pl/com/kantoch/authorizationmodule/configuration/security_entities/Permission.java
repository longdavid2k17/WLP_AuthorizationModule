package pl.com.kantoch.authorizationmodule.configuration.security_entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "permissions",uniqueConstraints = {
        @UniqueConstraint(columnNames = "name")
})
public class Permission {
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

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(	name = "user_permissions",
            joinColumns = @JoinColumn(name = "permission_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> userSet = new HashSet<>();

    public Permission() {
    }

    public Permission(Long id, String name, String displayName, String description, Boolean isEnabled, LocalDateTime creationDate, User creationUser, LocalDateTime modificationDate, User modificationUser, Set<User> userSet) {
        this.id = id;
        this.name = name;
        this.displayName = displayName;
        this.description = description;
        this.isEnabled = isEnabled;
        this.creationDate = creationDate;
        this.creationUser = creationUser;
        this.modificationDate = modificationDate;
        this.modificationUser = modificationUser;
        this.userSet = userSet;
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

    public LocalDateTime getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(LocalDateTime modificationDate) {
        this.modificationDate = modificationDate;
    }

    public Set<User> getUserSet() {
        return userSet;
    }

    public void setUserSet(Set<User> userSet) {
        this.userSet = userSet;
    }

    public User getCreationUser() {
        return creationUser;
    }

    public void setCreationUser(User creationUser) {
        this.creationUser = creationUser;
    }


    public User getModificationUser() {
        return modificationUser;
    }

    public void setModificationUser(User modificationUser) {
        this.modificationUser = modificationUser;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Permission that = (Permission) o;
        return Objects.equals(id, that.id) && Objects.equals(name, that.name) && Objects.equals(displayName, that.displayName) && Objects.equals(description, that.description) && Objects.equals(isEnabled, that.isEnabled) && Objects.equals(creationDate, that.creationDate) && Objects.equals(modificationDate, that.modificationDate) && Objects.equals(userSet, that.userSet) && Objects.equals(creationUser, that.creationUser) && Objects.equals(modificationUser, that.modificationUser);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, displayName, description, isEnabled, creationDate, modificationDate, userSet, creationUser);
    }

    @Override
    public String toString() {
        return "Permission{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", displayName='" + displayName + '\'' +
                ", description='" + description + '\'' +
                ", isEnabled=" + isEnabled +
                ", creationDate=" + creationDate +
                ", creationUser=" + creationUser +
                ", modificationDate=" + modificationDate +
                ", modificationUser=" + modificationUser +
                '}';
    }
}
