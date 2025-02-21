package com.oscarhkli.mahjong.score;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

@Value
@Slf4j
public class Melds {

  MahjongSetType mahjongSetType;
  List<MahjongTileType> chows;
  List<MahjongTileType> pongs;
  List<MahjongTileType> kongs;
  MahjongTileType eye;
  int[] unusedTiles;
  int unusedTileCount;
  int unusedPairs;

  public boolean hasEyes() {
    return this.eye != null;
  }

  public boolean hasOrphanEyes() {
    if (!this.mahjongSetType.getFamily().equals(MahjongConstant.SUITED)) {
      return false;
    }
    if (!this.hasEyes()) {
      return false;
    }
    var startingTile = this.mahjongSetType.getStartingTile();
    var endingTile = MahjongTileType.valueOfIndex(startingTile.getIndex() + 8);
    return Set.of(startingTile, endingTile).contains(this.eye);
  }

  public Set<MahjongTileType> getOrphanPongKongs() {
    if (!this.mahjongSetType.getFamily().equals(MahjongConstant.SUITED)) {
      return Set.of();
    }
    var startingTile = this.mahjongSetType.getStartingTile();
    var endingTile = MahjongTileType.valueOfIndex(startingTile.getIndex() + 8);
    return Stream.concat(this.pongs.stream(), this.kongs.stream())
        .filter(
            mahjongTileType ->
                mahjongTileType.equals(startingTile) || mahjongTileType.equals(endingTile))
        .collect(Collectors.toSet());
  }

  public boolean isAllOneSuit() {
    return this.chows.size() + this.getPongKongSize() == 4 && this.eye != null;
  }

  public int getPongKongSize() {
    return this.pongs.size() + this.kongs.size();
  }

  public boolean isMixedOneSuit(Melds windMelds, Melds dragonMelds) {
    if (!this.mahjongSetType.getFamily().equals(MahjongConstant.SUITED)) {
      return false;
    }
    if (!windMelds.mahjongSetType.equals(MahjongSetType.WIND)) {
      return false;
    }
    if (!dragonMelds.mahjongSetType.equals(MahjongSetType.DRAGON)) {
      return false;
    }
    return this.chows.size()
                + this.getPongKongSize()
                + windMelds.getPongKongSize()
                + dragonMelds.getPongKongSize()
            == 4
        && (this.hasEyes() || windMelds.hasEyes() || dragonMelds.hasEyes());
  }
}
