package com.oscarhkli.mahjong.score.security;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.spy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.Cookie;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {

  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private JwtHelper jwtHelper;
  @MockitoSpyBean private AuthenticationManager authenticationManager;

  // Static credentials (or use @Value for properties injected credentials)
  private static final String VALID_USERNAME = "dummyUser";
  private static final String VALID_PASSWORD = "dummyPassword";

  private static final String INVALID_USERNAME = "invalidUser";
  private static final String INVALID_PASSWORD = "wrongPassword";

  // Test for successful login with valid credentials
  @SneakyThrows
  @Test
  @WithMockUser()
  void testLoginSuccess() {
    var fakeAuthenticationToken =
        new UsernamePasswordAuthenticationToken(
            VALID_USERNAME, null, List.of(new SimpleGrantedAuthority("USER")));
    willReturn(fakeAuthenticationToken)
        .given(authenticationManager)
        .authenticate(any(Authentication.class));

    var loginRequest = spy(new LoginRequest(VALID_USERNAME, VALID_PASSWORD));

    mockMvc
        .perform(
            post("/auth/login")
                .secure(true)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isOk())
        .andExpect(cookie().exists("refresh_token")) // Check if the cookie exists
        .andExpect(cookie().httpOnly("refresh_token", true)); // Ensure HttpOnly flag is set
  }

  // Test for failed login with invalid credentials
  @SneakyThrows
  @Test
  void testLoginFailure() {
    var loginRequest = new LoginRequest(INVALID_USERNAME, INVALID_PASSWORD);

    mockMvc
        .perform(
            post("/auth/login")
                .secure(true)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
        .andExpect(status().isUnauthorized()) // Expect HTTP 401 Unauthorized
        .andExpect(
            content()
                .json(
                    """
                        {
                          "error": {
                            "code": "401",
                            "message": "Invalid credentials",
                            "errors": [
                              {
                                "reason": "BadCredentialsException",
                                "message": "Invalid credentials before authenticationManager"
                              }
                            ]
                          }
                        }
                        """));
  }

  // Test for missing username or password
  @SneakyThrows
  @Test
  void testLoginMissingCredentials() {
    var loginRequest = new LoginRequest("", "");

    mockMvc
        .perform(
            post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
                .secure(true))
        .andExpect(status().isUnauthorized()) // Expect HTTP 401 Unauthorized
        .andExpect(
            content()
                .json(
                    """
            {
              "error": {
                "code": "401",
                "message": "Invalid credentials",
                "errors": [
                  {
                    "reason": "BadCredentialsException",
                    "message": "Invalid credentials before authenticationManager"
                  }
                ]
              }
            }"""));
  }

  @SneakyThrows
  @Test
  void testRefreshAccessTokenUsingCookie() {
    // Given: a valid refresh token (assume it's already set in the cookie from the previous login)
    var refreshToken = jwtHelper.generateRefreshToken(VALID_USERNAME);

    // Mock a cookie being set (manually set in request)
    mockMvc
        .perform(
            post("/auth/refresh").secure(true).cookie(new Cookie("refresh_token", refreshToken)))

        // Then: verify new access token is returned
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token").exists()); // Check if token is in the response body
  }

  @SneakyThrows
  @Test
  void testInvalidRefreshTokenShouldReturnUnauthorized() {
    // Given: an invalid or expired refresh token
    var invalidRefreshToken = "invalidRefreshToken";

    // Mock a cookie with invalid refresh token
    mockMvc
        .perform(
            post("/auth/refresh")
                .secure(true)
                .cookie(new Cookie("refresh_token", invalidRefreshToken)))

        // Then: verify response is Unauthorized
        .andExpect(status().isUnauthorized());
  }
}
