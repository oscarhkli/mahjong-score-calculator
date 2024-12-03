package com.oscarhkli.mahjong.score;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WinningHandType {
  TRICK_HAND(-1),
  CHICKEN_HAND(0),
  COMMON_HAND(1),
  MIXED_ORPHANS(1),
  ALL_IN_TRIPLETS(3),
  MIXED_ONE_SUIT(3),
  SMALL_DRAGON(3),
  SMALL_WINDS(3),
  GREAT_DRAGON(5),
  ALL_ONE_SUIT(7),
  ALL_HONOR_TILES(10),
  NINE_GATES(10),
  ORPHANS(10),
  GREAT_WINDS(13),
  THIRTEEN_ORPHANS(13)
  ;

  private final int score;
}
