package com.oscarhkli.mahjong.score;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WinningHandType {
  TRICK_HAND(-1),
  CHICKEN_HAND(0),
  COMMON_HAND(1),
  ALL_IN_TRIPLETS(3),
  MIXED_ONE_SUIT(7),
  ALL_ONE_SUIT(7),
  ALL_HONOR_TILES(10)
  ;

  private final int score;
}
