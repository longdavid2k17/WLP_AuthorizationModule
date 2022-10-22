package pl.com.kantoch.authorizationmodule.configuration.payload.response;

import java.util.Date;
import java.util.List;

/**
 * <p>Klasa opakowująca odpowiedź serwera na poprawny token JWT. Posiada pola token, id, username, email i kolekcję ról.
 * Dane są przekazywane w AuthenticationController </p>
 * @author Dawid Kańtoch
 * @version 1.0
 * @since 1.0
 */
public class UserResponse
{
    private String token;
    private final String type = "Bearer";
    private Long id;
    private String username;
    private String email;
    private List<String> roles;

    private Date expirationDate;

    public UserResponse(String token, Long id, String username , String email, List<String> roles, Date expirationDate)
    {
        this.token = token;
        this.id = id;
        this.username = username;
        this.email = email;
        this.roles = roles;
        this.expirationDate = expirationDate;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getType() {
        return type;
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

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public Date getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Date expirationDate) {
        this.expirationDate = expirationDate;
    }
}