package com.oscarhkli.mahjong.score.security;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
public class AuthController {

  private static final long MINUTE_TO_MILLISECOND = 60 * 1000L;

  private final AuthenticationManager authenticationManager;
  private final JwtHelper jwtHelper; // Your JWT utility for token generation
  private final PasswordEncoder passwordEncoder;
  private final String username;
  private final String password;
  private final String tokenRole;
  private final long refreshTokenExpirationTime;

  public AuthController(
      AuthenticationManager authenticationManager,
      JwtHelper jwtHelper,
      PasswordEncoder passwordEncoder,
      @Value("${auth.username}") String username,
      @Value("${auth.password}") String password,
      @Value("${auth.token-role}") String tokenRole,
      @Value("${auth.refresh-token-expiration-minute}") long refreshTokenExpirationMinute) {
    this.authenticationManager = authenticationManager;
    this.jwtHelper = jwtHelper;
    this.passwordEncoder = passwordEncoder;
    this.username = username;
    this.password = password;
    this.tokenRole = tokenRole;
    this.refreshTokenExpirationTime = refreshTokenExpirationMinute * MINUTE_TO_MILLISECOND;
  }

  @PostMapping("/login")
  public ResponseEntity<TokenResponse> login(
      @RequestHeader HttpHeaders headers, @RequestBody LoginRequest loginRequest) {
    log.info(
        "login [referer: {}, user-agent: {}]",
        headers.getOrEmpty(HttpHeaders.REFERER),
        headers.getOrEmpty(HttpHeaders.USER_AGENT));
    try {
      authenticate(loginRequest);
      var token = jwtHelper.generateToken(loginRequest.username(), List.of(tokenRole));
      var refreshToken = jwtHelper.generateRefreshToken(loginRequest.username());
      var refreshCookie = getRefreshCookie(refreshToken);
      return ResponseEntity.ok()
          .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
          .body(new TokenResponse(token));
    } catch (Exception e) {
      throw new CredentialException("Invalid credentials", e);
    }
  }

  private ResponseCookie getRefreshCookie(String refreshToken) {
    // Set the HttpOnly cookie for the refresh token
    return ResponseCookie.from("refresh_token")
        .value(refreshToken)
        .httpOnly(true)
        .secure(true)
        .path("/")
        .maxAge(refreshTokenExpirationTime)
        .build();
  }

  @PostMapping("/refresh-token")
  public ResponseEntity<TokenResponse> refreshToken(
      @RequestHeader HttpHeaders headers, HttpServletRequest request) {
    log.info(
        "refreshToken [referer: {}, user-agent: {}]",
        headers.getOrEmpty(HttpHeaders.REFERER),
        headers.getOrEmpty(HttpHeaders.USER_AGENT));
    try {
      // Retrieve the refresh token from the HttpOnly cookie
      var cookies = request.getCookies();

      var refreshToken =
          Optional.ofNullable(cookies)
              .flatMap(
                  c ->
                      Arrays.stream(c)
                          .filter(cookie -> cookie.getName().equals("refresh_token"))
                          .findFirst()
                          .map(Cookie::getValue));

      if (refreshToken.isEmpty() || !jwtHelper.validateToken(refreshToken.get(), username)) {
        throw new CredentialException("Invalid or expired refresh token");
      }

      // Generate a new access token using the valid refresh token
      var tokenUsername = jwtHelper.extractSubject(refreshToken.get());
      var newAccessToken = jwtHelper.generateToken(tokenUsername, List.of(tokenRole));

      // Return the new access token
      return ResponseEntity.ok(new TokenResponse(newAccessToken));
    } catch (Exception e) {
      throw new CredentialException("Unable to refresh token", e);
    }
  }

  private void authenticate(LoginRequest loginRequest) {
    // Compare incoming credentials with static ones (e.g., hardcoded or from properties)
    if (!loginRequest.isUsernameEqualTo(username)
        || !passwordEncoder.matches(loginRequest.password(), password)) {
      throw new BadCredentialsException("Invalid credentials before authenticationManager");
    }
    log.info("username and password matches");
    // If the credentials match, create a valid authentication token
    authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(loginRequest.username(), loginRequest.password()));
    log.info("authenticationManager authentication completed");
  }
}
