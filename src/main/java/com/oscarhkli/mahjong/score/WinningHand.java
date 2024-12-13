package com.oscarhkli.mahjong.score;

import java.util.List;
import lombok.Value;

@Value
public class WinningHand {

  List<WinningHandType> winningHandTypes;

  public int getFaans() {
    if (this.winningHandTypes.contains(WinningHandType.TRICK_HAND)) {
      return WinningHandType.TRICK_HAND.getScore();
    }
    if (this.winningHandTypes.contains(WinningHandType.ALL_HONOR_TILES)) {
      return WinningHandType.ALL_HONOR_TILES.getScore();
    }
    var score = 0;
    for (var winningHandType : this.winningHandTypes) {
      score += winningHandType.getScore();
    }
    return score;
  }
}
