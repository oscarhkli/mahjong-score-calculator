package com.oscarhkli.mahjong.score.api;

import com.oscarhkli.mahjong.score.WinningHand;
import com.oscarhkli.mahjong.score.WinningHandType;
import java.util.List;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class WinningHandResponse {

  BreakDown data;

  public static WinningHandResponse of(WinningHand winningHand) {
    return WinningHandResponse.builder().data(BreakDown.of(winningHand)).build();
  }

  @Value
  @Builder
  static class BreakDown {
    int totalFaans;
    List<WinningHandDescription> winningHands;

    public static BreakDown of(WinningHand winningHand) {
      return BreakDown.builder()
          .totalFaans(winningHand.getFaans())
          .winningHands(
              winningHand.getWinningHandTypes().stream().map(WinningHandDescription::of).toList())
          .build();
    }
  }

  @Value
  @Builder
  static class WinningHandDescription {
    WinningHandType type;
    String name;
    int faans;

    public static WinningHandDescription of(WinningHandType winningHandType) {
      return WinningHandDescription.builder()
          .type(winningHandType)
          .name(winningHandType.getWinningHandName())
          .faans(winningHandType.getScore())
          .build();
    }
  }
}
