package com.oscarhkli.mahjong.score;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MahjongTileType {
  EAST(0),
  SOUTH(1),
  WEST(2),
  NORTH(3),
  RED(4),
  GREEN(5),
  WHITE(6),
  C1(7),
  C2(8),
  C3(9),
  C4(10),
  C5(11),
  C6(12),
  C7(13),
  C8(14),
  C9(15),
  B1(16),
  B2(17),
  B3(18),
  B4(19),
  B5(20),
  B6(21),
  B7(22),
  B8(23),
  B9(24),
  D1(25),
  D2(26),
  D3(27),
  D4(28),
  D5(29),
  D6(30),
  D7(31),
  D8(32),
  D9(33),
  F1(34),
  F2(35),
  F3(36),
  F4(37),
  S1(38),
  S2(39),
  S3(40),
  S4(41);

  private final int index;

  public static MahjongTileType valueOfIndex(int index) {
    for (MahjongTileType tileType : MahjongTileType.values()) {
      if (tileType.index == index) {
        return tileType;
      }
    }
    return null;
  }

  public boolean isMahjongSetTypeEqualTo(MahjongSetType mahjongSetType) {
    var start = mahjongSetType.getStartingTile().getIndex();
    var end = start + mahjongSetType.getSize() - 1;
    return this.getIndex() >= start && this.getIndex() <= end;
  }
}
