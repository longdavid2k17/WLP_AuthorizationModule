package pl.com.kantoch.authorizationmodule.configuration.security_entities.role;

import javax.persistence.*;

/**
 * Klasa roli, będąca encją w bazie danych. Posiada pole id oraz nazwa roli
 */
@Entity
@Table(name = "roles")
public class Role
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ERole roleName;

    public Role()
    {

    }

    public Role(ERole roleName)
    {
        this.roleName = roleName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ERole getRoleName() {
        return roleName;
    }

    public void setRoleName(ERole roleName) {
        this.roleName = roleName;
    }
}
