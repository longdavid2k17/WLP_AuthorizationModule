package pl.com.kantoch.authorizationmodule.configuration.jwt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pl.com.kantoch.authorizationmodule.configuration.user_details.UserDetailsServiceImplementation;
import pl.com.kantoch.authorizationmodule.tools.JWTTokenService;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class AuthTokenFilter extends OncePerRequestFilter
{
    private final JWTUtil jwtUtils;
    private final JWTTokenService jwtTokenService;
    private final UserDetailsServiceImplementation userDetailsService;

    public AuthTokenFilter(JWTUtil jwtUtils, JWTTokenService jwtTokenService, UserDetailsServiceImplementation userDetailsService) {
        this.jwtUtils = jwtUtils;
        this.jwtTokenService = jwtTokenService;
        this.userDetailsService = userDetailsService;
    }

    private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException
    {
        try
        {
            String jwt = parseJwt(request);
            if (jwt != null && jwtUtils.validateJwtToken(jwt))
            {
                String username = jwtUtils.getUsernameFromJwtToken(jwt);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        catch (Exception e)
        {
            logger.error("Cannot set user authentication: {}", e.getMessage());
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Metoda parsująca otrzymany token w pełnej postaci i zwracająca sam token
     * @param request zapytanie z przesłanym tokenem
     * @return jeśli token ma poprawną budowę zwraca samą sygnaturę, jeśli nie to zwraca null
     */
    private String parseJwt(HttpServletRequest request)
    {
        return jwtTokenService.getToken(request);
    }
}