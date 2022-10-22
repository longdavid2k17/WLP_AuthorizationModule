package pl.com.kantoch.authorizationmodule.configuration.jwt;

import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import pl.com.kantoch.authorizationmodule.configuration.user_details.UserDetailsImpl;
import pl.com.kantoch.authorizationmodule.tools.JWTTokenService;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.Date;

@Component
public class JWTUtil implements Serializable
{
    private static final long serialVersionUID = -2550185165626007488L;
    private final Logger LOGGER = LoggerFactory.getLogger(JWTUtil.class);

    private final JWTTokenService jwtTokenService;

    @Value("${jwt.expiration}")
    public int JWT_TOKEN_EXPIRATION;

    @Value("${jwt.secret}")
    private String JWT_SECRET;

    public JWTUtil(JWTTokenService jwtTokenService) {
        this.jwtTokenService = jwtTokenService;
    }

    public String generateJwtToken(Authentication authentication)
    {
        UserDetailsImpl userPrincipal = (UserDetailsImpl) authentication.getPrincipal();

        return Jwts.builder()
                .setSubject((userPrincipal.getUsername()))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + JWT_TOKEN_EXPIRATION))
                .signWith(SignatureAlgorithm.HS512, JWT_SECRET)
                .compact();
    }

    public String getUsernameFromJwtToken(String token)
    {
        return Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJws(token).getBody().getSubject();
    }

    public String getUsernameFromJwtToken(HttpServletRequest request)
    {
        return Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJws(getToken(request)).getBody().getSubject();
    }

    public boolean validateJwtToken(String authToken)
    {
        try
        {
            Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJws(authToken);
            return true;
        }
        catch (SignatureException e)
        {
            LOGGER.error("Invalid JWT signature: {}", e.getMessage());
        }
        catch (MalformedJwtException e)
        {
            LOGGER.error("Invalid JWT token: {}", e.getMessage());
        }
        catch (ExpiredJwtException e)
        {
            LOGGER.error("JWT token is expired: {}", e.getMessage());
        }
        catch (UnsupportedJwtException e)
        {
            LOGGER.error("JWT token is unsupported: {}", e.getMessage());
        }
        catch (IllegalArgumentException e)
        {
            LOGGER.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }

    private Claims getClaims(String token) {
        return Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJws(token).getBody();
    }

    public Date getExpirationDate(String token) {
        return getClaims(token).getExpiration();
    }

    public String getToken(HttpServletRequest request)
    {
       return jwtTokenService.getToken(request);
    }
}
