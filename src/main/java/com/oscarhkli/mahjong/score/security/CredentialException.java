package com.oscarhkli.mahjong.score.security;

public class CredentialException extends RuntimeException {

  public CredentialException(String message) {
    super(message);
  }

  public CredentialException(String message, Throwable cause) {
    super(message, cause);
  }
}
