package com.oscarhkli.mahjong.score;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum WinningHandType {
  TRICK_HAND(-1, "Trick Hand", false),
  CHICKEN_HAND(0, "Chicken Hand", false),
  SELF_PICK_WITHOUT_EXTRA_FAAN(0, "Self Pick Without Extra Faan Hand", false),
  PREVAILING_WIND(1, "Prevailing Wind", false),
  SEAT_WIND(1, "Seat Wind", false),
  FLOWER_OF_OWN_WIND(1, "Flower/Season Of Own Wind", false),
  NO_FLOWERS(1, "No Flowers/Seasons", false),
  SELF_PICK(1, "Self Pick", false),
  WIN_FROM_WALL(1, "Win From Wall", false),
  WIN_BY_LAST_CATCH(1, "Win By Last Catch", false),
  ROBBING_KONG(1, "Robbing Kong", false),
  WIN_BY_KONG(1, "Win By Kong", false),
  COMMON_HAND(1, "Common Hand", false),
  MIXED_ORPHANS(1, "Mixed Orphans", false),
  ONE_DRAGON(1, "A Meld of Dragon", false),
  ALL_FLOWERS(2, "All Flowers/Seasons", false),
  FLOWER_HANDS(3, "Flower Hands", false),
  ALL_IN_TRIPLETS(3, "All in Triplets", false),
  MIXED_ONE_SUIT(3, "Mixed One Suit", false),
  SMALL_DRAGON(3, "Small Dragon", false),
  SMALL_WINDS(3, "Small Winds", false),
  GREAT_DRAGON(5, "Great Dragon", false),
  ALL_ONE_SUIT(7, "All One Suit", false),
  GREAT_FLOWERS(8, "Great Flowers", false),
  WIN_BY_DOUBLE_KONG(8, "Win By Double Kong", false),
  SELF_TRIPLETS(8, "Self Triplets", false),
  ALL_HONOR_TILES(10, "All Honor Tiles", true),
  NINE_GATES(10, "Nine Gates", true),
  ORPHANS(10, "Orphans", true),
  GREAT_WINDS(13, "Great Winds", true),
  THIRTEEN_ORPHANS(13, "Thirteen Orphans", true),
  ALL_KONGS(13, "All Kongs", true),
  ;

  private final int score;
  private final String winningHandName;
  private final boolean isLimitHand;
}
