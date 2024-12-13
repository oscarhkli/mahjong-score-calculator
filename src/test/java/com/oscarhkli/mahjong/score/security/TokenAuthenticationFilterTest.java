package com.oscarhkli.mahjong.score.security;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

@ExtendWith(MockitoExtension.class)
class TokenAuthenticationFilterTest {

  @Mock private JwtHelper jwtHelper;
  @InjectMocks private TokenAuthenticationFilter tokenAuthenticationFilter;

  @AfterEach
  void tearDown() {
    // Clear SecurityContext after each test to ensure no state leaks
    SecurityContextHolder.clearContext();
  }

  @Test
  void testValidToken() throws Exception {
    var token = "valid.jwt.token";
    var username = "MSC_USER";

    given(jwtHelper.validateToken(token, username)).willReturn(true);
    given(jwtHelper.extractSubject(token)).willReturn(username);

    var request = new MockHttpServletRequest();
    request.addHeader("Authorization", "Bearer " + token);

    var response = new MockHttpServletResponse();
    var filterChain = mock(FilterChain.class);

    tokenAuthenticationFilter.doFilterInternal(request, response, filterChain);

    var context = SecurityContextHolder.getContext();
    var authentication = context.getAuthentication();

    then(authentication).as("Authentication should not be null").isNotNull();
    then(authentication.getName()).as("Username should match").isEqualTo(username);
    BDDMockito.then(filterChain).should().doFilter(request, response);
  }

  @Test
  void testInvalidToken() throws Exception {
    var token = "invalid.jwt.token";
    var username = "MSC_USER";

    given(jwtHelper.validateToken(token, username)).willReturn(false);

    var request = new MockHttpServletRequest();
    request.addHeader("Authorization", "Bearer " + token);

    var response = new MockHttpServletResponse();
    var filterChain = mock(FilterChain.class);

    tokenAuthenticationFilter.doFilterInternal(request, response, filterChain);

    var context = SecurityContextHolder.getContext();
    then(context.getAuthentication())
        .as("Authentication should be null for invalid token")
        .isNull();
    BDDMockito.then(filterChain).should().doFilter(request, response);
  }
}
