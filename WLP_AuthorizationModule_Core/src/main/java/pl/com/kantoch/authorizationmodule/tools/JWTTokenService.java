package pl.com.kantoch.authorizationmodule.tools;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

@Service
public class JWTTokenService {

    public String getToken(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        if (StringUtils.hasText(headerAuth)) {
            return getToken(request.getHeader(headerAuth));
        }
        return null;
    }

    public String getToken(String token) {
        if (token.startsWith("Bearer "))
        {
            return token.substring(7);
        }
        return null;
    }
}
