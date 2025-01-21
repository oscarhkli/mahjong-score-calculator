package com.oscarhkli.mahjong.score;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class WinningConditionCalculator {

  public List<WinningHandType> calculateExtraWinningHands(
      WinningConditions winningConditions,
      List<WinningHandType> winningHands,
      List<WinningHandType> bonusWinningHands) {
    var extraWinningHands = new ArrayList<WinningHandType>();
    var isAllKongs = winningHands.contains(WinningHandType.ALL_KONGS);
    if (winningConditions.isWinByKong() && !isAllKongs) {
      extraWinningHands.add(WinningHandType.WIN_BY_KONG);
    }
    if (winningConditions.isWinByDoubleKong() && !isAllKongs) {
      extraWinningHands.add(WinningHandType.WIN_BY_DOUBLE_KONG);
    }
    if (winningConditions.isWinByLastCatch()) {
      extraWinningHands.add(WinningHandType.WIN_BY_LAST_CATCH);
    }
    if (winningConditions.isRobbingKong()) {
      extraWinningHands.add(WinningHandType.ROBBING_KONG);
    }
    var isWinByFlowersOnly = isWinByFlowersOnly(winningHands, bonusWinningHands);
    if (winningConditions.isSelfPick()) {
      if (winningHands.stream().anyMatch(WinningHandType::isLimitHand) || isWinByFlowersOnly) {
        extraWinningHands.add(WinningHandType.SELF_PICK_WITHOUT_EXTRA_FAAN);
      } else {
        extraWinningHands.add(WinningHandType.SELF_PICK);
      }
    } else {
      // Still count as self-pick for some condition, but with zero faan
      if (winningHands.contains(WinningHandType.THIRTEEN_ORPHANS) || isWinByFlowersOnly) {
        extraWinningHands.add(WinningHandType.SELF_PICK_WITHOUT_EXTRA_FAAN);
      }
    }

    return extraWinningHands;
  }

  private static boolean isWinByFlowersOnly(
      List<WinningHandType> winningHands, List<WinningHandType> bonusWinningHands) {
    return winningHands.isEmpty()
        && (bonusWinningHands.contains(WinningHandType.GREAT_FLOWERS)
            || bonusWinningHands.contains(WinningHandType.FLOWER_HANDS));
  }
}
