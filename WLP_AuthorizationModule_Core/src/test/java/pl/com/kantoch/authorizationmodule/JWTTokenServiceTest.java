package pl.com.kantoch.authorizationmodule;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import pl.com.kantoch.authorizationmodule.tools.JWTTokenService;

import static org.junit.jupiter.api.Assertions.assertFalse;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JWTTokenServiceTest {
    @Autowired
    JWTTokenService jwtTokenService;

    @Test
    public void shouldValidateAndReturnToken(){
        String testToken = "Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0MTIz" +
                "IiwiaWF0IjoxNjY2NzIxMTIyLCJleHAiOjE2NjY3MjQxMjJ9.MIsB0lFEeDY6a" +
                "SHZI5kZ1SqGpknkPzrm1kRXzB-amG0e0ln7YGwBr40wGFwF7lvVdtQROespM983kFm7fll6AA";
        String result = jwtTokenService.getToken(testToken);
        assertFalse(result.contains("Bearer"));
    }
}
