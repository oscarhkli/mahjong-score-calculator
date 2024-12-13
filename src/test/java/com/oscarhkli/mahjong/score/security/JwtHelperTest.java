package com.oscarhkli.mahjong.score.security;

import static org.assertj.core.api.BDDAssertions.catchThrowableOfType;
import static org.assertj.core.api.BDDAssertions.then;

import io.jsonwebtoken.security.SignatureException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

@SpringBootTest
class JwtHelperTest {

  @MockitoSpyBean private JwtHelper jwtHelper;

  @Test
  void testGenerateWithRolesAndValidateToken() {
    var subject = "test-user";
    var roles = List.of("test-role");
    var token = jwtHelper.generateToken(subject, roles);

    then(token).as("Token should not be null").isNotNull();
    then(jwtHelper.validateToken(token, subject)).as("Token should be valid").isTrue();
    then(jwtHelper.extractSubject(token)).as("Subject should match").isEqualTo(subject);
    then(jwtHelper.extractRoles(token))
        .as("Roles should match")
        .containsExactlyInAnyOrderElementsOf(roles);
  }

  @Test
  void testGenerateRefreshAndValidateToken() {
    var subject = "test-user";
    var token = jwtHelper.generateRefreshToken(subject);

    then(token).as("Token should not be null").isNotNull();
    then(jwtHelper.validateToken(token, subject)).as("Token should be valid").isTrue();
    then(jwtHelper.extractSubject(token)).as("Subject should match").isEqualTo(subject);
  }

  @Test
  void testInvalidToken() {
    var invalidToken =
        "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
    var thrown =
        catchThrowableOfType(
            SignatureException.class, () -> jwtHelper.validateToken(invalidToken, "DUMMY_SUBJECT"));
    then(thrown).as("Invalid token should not be valid").isNotNull();
  }
}
