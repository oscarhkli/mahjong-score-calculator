package com.oscarhkli.mahjong.score;

import static com.oscarhkli.mahjong.score.MahjongConstant.BONUS;
import static com.oscarhkli.mahjong.score.MahjongConstant.HONOR;
import static com.oscarhkli.mahjong.score.MahjongConstant.SUITED;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MahjongSetType {
  WIND(0, MahjongTileType.EAST, MahjongTileType.NORTH, HONOR, 4),
  DRAGON(1, MahjongTileType.RED, MahjongTileType.WHITE, HONOR, 3),
  CHARACTER(2, MahjongTileType.C1, MahjongTileType.C9, SUITED, 9),
  BAMBOO(3, MahjongTileType.B1, MahjongTileType.B9, SUITED, 9),
  DOT(4, MahjongTileType.D1, MahjongTileType.D9, SUITED, 9),
  FLOWER(5, MahjongTileType.F1, MahjongTileType.F4, BONUS, 4),
  SEASON(6, MahjongTileType.S1, MahjongTileType.S4, BONUS, 4);

  private final int index;
  private final MahjongTileType startingTile;
  private final MahjongTileType endingTile;
  private final String family;
  private final int size;
}
