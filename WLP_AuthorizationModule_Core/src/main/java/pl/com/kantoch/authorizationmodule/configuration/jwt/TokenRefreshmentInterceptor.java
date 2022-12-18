package pl.com.kantoch.authorizationmodule.configuration.jwt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import pl.com.kantoch.authorizationmodule.tools.JWTTokenService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

@Component
public class TokenRefreshmentInterceptor implements HandlerInterceptor {

    private final Logger LOGGER = LoggerFactory.getLogger(TokenRefreshmentInterceptor.class);

    private final JWTTokenService jwtTokenService;
    private final JWTUtil jwtUtil;

    public TokenRefreshmentInterceptor(JWTTokenService jwtTokenService, JWTUtil jwtUtil) {
        this.jwtTokenService = jwtTokenService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        final String token = jwtTokenService.getToken(request);
        final Date expirationDate = jwtUtil.getExpirationDate(token);
        if(expirationDate.after(new Date())){
            //jwtUtil.
            LOGGER.warn("Access token expired for user {}!",jwtUtil.getUsernameFromJwtToken(token));
        }
    }
}
