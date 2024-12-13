package com.oscarhkli.mahjong.score;

import java.util.List;

public record ApiErrorResponse(ApiError error) {

  public record ApiError(String code, String message, List<ErrorDetails> errors) {}

  public record ErrorDetails(String reason, String message) {}
}
