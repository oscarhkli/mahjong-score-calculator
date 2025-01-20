package com.oscarhkli.mahjong.score;

import java.util.List;
import lombok.Value;

@Value
public class WinningHand {

  private static final int MAX_SCORE = 13;

  List<WinningHandType> winningHandTypes;

  public int getFaans() {
    if (this.winningHandTypes.contains(WinningHandType.TRICK_HAND)) {
      return WinningHandType.TRICK_HAND.getScore();
    }
    var score = 0;
    for (var winningHandType : this.winningHandTypes) {
      score += winningHandType.getScore();
    }
    return Math.min(score, MAX_SCORE);
  }
}
