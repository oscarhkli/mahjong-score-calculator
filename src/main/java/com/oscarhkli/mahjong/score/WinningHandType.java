package com.oscarhkli.mahjong.score;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WinningHandType {
  TRICK_HAND(-1, "Trick Hand"),
  CHICKEN_HAND(0, "Chicken Hand"),
  COMMON_HAND(1, "Common Hand"),
  MIXED_ORPHANS(1, "Mixed Orphans"),
  ONE_DRAGON(1, "A Meld of Dragon"),
  ALL_IN_TRIPLETS(3, "All in Triplets"),
  MIXED_ONE_SUIT(3, "Mixed One Suit"),
  SMALL_DRAGON(3, "Small Dragon"),
  SMALL_WINDS(3, "Small Winds"),
  GREAT_DRAGON(5, "Great Dragon"),
  ALL_ONE_SUIT(7, "All One Suit"),
  ALL_HONOR_TILES(10, "All Honor Tiles"),
  NINE_GATES(10, "Nine Gates"),
  ORPHANS(10, "Orphans"),
  GREAT_WINDS(13, "Great Winds"),
  THIRTEEN_ORPHANS(13, "Thirteen Orphans"),
  ;

  private final int score;
  private final String winningHandName;
}
