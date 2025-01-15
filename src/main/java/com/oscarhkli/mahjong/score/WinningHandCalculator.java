package com.oscarhkli.mahjong.score;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class WinningHandCalculator {

  private final MeldsFactory meldsFactory;
  private final BonusWinningConditionCalculator bonusWinningConditionCalculator;

}
