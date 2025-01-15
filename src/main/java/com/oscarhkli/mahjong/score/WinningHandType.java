package com.oscarhkli.mahjong.score;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WinningHandType {
  TRICK_HAND(-1, "Trick Hand"),
  CHICKEN_HAND(0, "Chicken Hand"),
  PREVAILING_WIND(1, "Prevailing Wind"),
  SEAT_WIND(1, "Seat Wind"),
  FLOWER_OF_OWN_WIND(1, "Flower/Season Of Own Wind"),
  NO_FLOWERS(1, "No Flowers/Seasons"),
  SELF_PICK(1, "Self Pick"),
  WIN_FROM_WALL(1, "Win From Wall"),
  COMMON_HAND(1, "Common Hand"),
  MIXED_ORPHANS(1, "Mixed Orphans"),
  ONE_DRAGON(1, "A Meld of Dragon"),
  ALL_FLOWERS(2, "All Flowers/Seasons"),
  FLOWER_HANDS(3, "Flower Hands"),
  ALL_IN_TRIPLETS(3, "All in Triplets"),
  MIXED_ONE_SUIT(3, "Mixed One Suit"),
  SMALL_DRAGON(3, "Small Dragon"),
  SMALL_WINDS(3, "Small Winds"),
  GREAT_DRAGON(5, "Great Dragon"),
  ALL_ONE_SUIT(7, "All One Suit"),
  GREAT_FLOWERS(8, "Great Flowers"),
  ALL_HONOR_TILES(10, "All Honor Tiles"),
  NINE_GATES(10, "Nine Gates"),
  SELF_TRIPLETS(10, "Self Triplets"),
  ORPHANS(10, "Orphans"),
  GREAT_WINDS(13, "Great Winds"),
  THIRTEEN_ORPHANS(13, "Thirteen Orphans"),
  ALL_KONGS(13, "All Kongs"),
  ;

  private final int score;
  private final String winningHandName;
}
