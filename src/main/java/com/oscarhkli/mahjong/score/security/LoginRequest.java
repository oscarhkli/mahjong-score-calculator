package com.oscarhkli.mahjong.score.security;

public record LoginRequest(String username, String password) {

  public boolean isUsernameEqualTo(String username) {
    return this.username.equals(username);
  }

}
