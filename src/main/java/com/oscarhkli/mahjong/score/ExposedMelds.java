package com.oscarhkli.mahjong.score;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
public class ExposedMelds {

  @JsonSetter(nulls = Nulls.AS_EMPTY)
  List<MahjongTileType> chows;

  @JsonSetter(nulls = Nulls.AS_EMPTY)
  List<MahjongTileType> pongs;

  @JsonSetter(nulls = Nulls.AS_EMPTY)
  List<MahjongTileType> kongs;

  public ExposedMelds() {
    this.chows = List.of();
    this.pongs = List.of();
    this.kongs = List.of();
  }
  
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

  @JsonIgnore
  public boolean isEmpty() {
    return chows.isEmpty() && pongs.isEmpty() && kongs.isEmpty();
  }
}
