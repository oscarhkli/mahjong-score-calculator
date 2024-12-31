package com.oscarhkli.mahjong.score;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class ExposedMelds {

  List<MahjongTileType> chows;
  List<MahjongTileType> pongs;
  List<MahjongTileType> kongs;

  public ExposedMelds(ExposedMelds unfilteredMelds, MahjongSetType mahjongSetType) {
    var startingTileIndex = mahjongSetType.getStartingTile().getIndex();
    var endingTileIndex = mahjongSetType.getEndingTile().getIndex();

    this.chows =
        unfilteredMelds.getChows().stream()
            .filter(chow -> chow.withinRange(startingTileIndex, endingTileIndex))
            .toList();
    this.pongs =
        unfilteredMelds.getPongs().stream()
            .filter(pong -> pong.withinRange(startingTileIndex, endingTileIndex))
            .toList();
    this.kongs =
        unfilteredMelds.getKongs().stream()
            .filter(kong -> kong.withinRange(startingTileIndex, endingTileIndex))
            .sorted()
            .toList();
  }

  public boolean isEmpty() {
    return chows.isEmpty() && pongs.isEmpty() && kongs.isEmpty();
  }
}
