package com.oscarhkli.mahjong.score;

import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class ScoreEvaluator {

  public int calculateScore(List<WinningHandType> winningHandTypes) {
    if (winningHandTypes.contains(WinningHandType.TRICK_HAND)) {
      return WinningHandType.TRICK_HAND.getScore();
    }
    var score = 0;
    for (var winningHandType : winningHandTypes) {
      score += winningHandType.getScore();
    }
    return score;
  }

}
