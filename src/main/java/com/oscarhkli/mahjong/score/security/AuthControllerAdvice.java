package com.oscarhkli.mahjong.score.security;

import com.oscarhkli.mahjong.score.ApiErrorResponse;
import com.oscarhkli.mahjong.score.ApiErrorResponse.ApiError;
import com.oscarhkli.mahjong.score.ApiErrorResponse.ErrorDetails;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Slf4j
@RestControllerAdvice(
    annotations = RestController.class,
    assignableTypes = {AuthController.class})
public class AuthControllerAdvice extends ResponseEntityExceptionHandler {

  @ExceptionHandler(value = {CredentialException.class})
  @ResponseStatus(HttpStatus.UNAUTHORIZED)
  protected ResponseEntity<ApiErrorResponse> handleTokenException(CredentialException ex) {
    var errorResponse =
        new ApiErrorResponse(
            new ApiError(
                Integer.toString(HttpStatus.UNAUTHORIZED.value()),
                ex.getMessage(),
                ex.getCause() != null
                    ? List.of(
                        new ErrorDetails(
                            ex.getCause().getClass().getSimpleName(), ex.getCause().getMessage()))
                    : List.of()));
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
  }
}
