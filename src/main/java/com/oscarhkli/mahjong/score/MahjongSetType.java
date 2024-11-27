package com.oscarhkli.mahjong.score;

import static com.oscarhkli.mahjong.score.MahjongConstant.BONUS;
import static com.oscarhkli.mahjong.score.MahjongConstant.HONOR;
import static com.oscarhkli.mahjong.score.MahjongConstant.SUITED;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MahjongSetType {

  WIND(MahjongTileType.EAST, HONOR, 4),
  DRAGON(MahjongTileType.RED, HONOR, 3),
  CHARACTER(MahjongTileType.C1, SUITED, 9),
  BAMBOO(MahjongTileType.B1, SUITED, 9),
  DOT(MahjongTileType.D1, SUITED, 9),
  FLOWER(MahjongTileType.F1, BONUS, 4),
  SEASON(MahjongTileType.S1, BONUS, 4);

  private final MahjongTileType startingTile;
  private final String family;
  private final int size;
}
