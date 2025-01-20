package com.oscarhkli.mahjong.score;

import static org.assertj.core.api.BDDAssertions.then;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class WinningConditionCalculatorTest {

  WinningConditionCalculator calculator = new WinningConditionCalculator();

  @ParameterizedTest
  @MethodSource
  void calculateExtraWinningHands(
      WinningConditions conditions,
      List<WinningHandType> winningHands,
      List<WinningHandType> bonusWinningHands,
      List<WinningHandType> expectedExtraWinningHands) {
    var actualExtraWinningHands = calculator.calculateExtraWinningHands(conditions, winningHands, bonusWinningHands);

    then(actualExtraWinningHands).containsExactlyInAnyOrderElementsOf(expectedExtraWinningHands);
  }

  private static Stream<Arguments> calculateExtraWinningHands() {
    return Stream.of(
        Arguments.of(
            new WinningConditions(true, false, false, true, false),
            List.of(),
            List.of(),
            List.of(WinningHandType.SELF_PICK, WinningHandType.WIN_BY_KONG)),
        Arguments.of(
            new WinningConditions(true, false, false, true, false),
            List.of(WinningHandType.ALL_KONGS),
            List.of(),
            List.of(WinningHandType.SELF_PICK_WITHOUT_EXTRA_FAAN)),
        Arguments.of(
            new WinningConditions(true, false, false, false, true),
            List.of(),
            List.of(),
            List.of(WinningHandType.SELF_PICK, WinningHandType.WIN_BY_DOUBLE_KONG)),
        Arguments.of(
            new WinningConditions(true, false, false, false, true),
            List.of(WinningHandType.ALL_KONGS),
            List.of(),
            List.of(WinningHandType.SELF_PICK_WITHOUT_EXTRA_FAAN)),
        Arguments.of(
            new WinningConditions(true, true, false, false, false),
            List.of(WinningHandType.ALL_ONE_SUIT),
            List.of(),
            List.of(WinningHandType.SELF_PICK, WinningHandType.WIN_BY_LAST_CATCH)),
        Arguments.of(
            new WinningConditions(false, false, true, false, false),
            List.of(WinningHandType.ALL_ONE_SUIT),
            List.of(),
            List.of(WinningHandType.ROBBING_KONG)),
        Arguments.of(
            new WinningConditions(true, false, false, false, false),
            List.of(),
            List.of(WinningHandType.FLOWER_HANDS),
            List.of(WinningHandType.SELF_PICK_WITHOUT_EXTRA_FAAN)),
        Arguments.of(
            new WinningConditions(true, false, false, false, false),
            List.of(),
            List.of(WinningHandType.GREAT_FLOWERS),
            List.of(WinningHandType.SELF_PICK_WITHOUT_EXTRA_FAAN)),
        Arguments.of(
            new WinningConditions(true, false, false, false, false),
            List.of(WinningHandType.ALL_ONE_SUIT),
            List.of(WinningHandType.FLOWER_HANDS),
            List.of(WinningHandType.SELF_PICK)),
        Arguments.of(
            new WinningConditions(true, false, false, false, false),
            List.of(WinningHandType.ALL_ONE_SUIT),
            List.of(WinningHandType.FLOWER_HANDS),
            List.of(WinningHandType.SELF_PICK)),
        Arguments.of(
            new WinningConditions(true, false, false, false, false),
            List.of(WinningHandType.THIRTEEN_ORPHANS),
            List.of(),
            List.of(WinningHandType.SELF_PICK_WITHOUT_EXTRA_FAAN)),
        Arguments.of(
            new WinningConditions(true, false, false, false, false),
            List.of(WinningHandType.GREAT_WINDS),
            List.of(),
            List.of(WinningHandType.SELF_PICK_WITHOUT_EXTRA_FAAN)),
        Arguments.of(
            new WinningConditions(true, false, false, false, false),
            List.of(WinningHandType.ORPHANS),
            List.of(),
            List.of(WinningHandType.SELF_PICK_WITHOUT_EXTRA_FAAN)),
        Arguments.of(
            new WinningConditions(true, false, false, false, false),
            List.of(WinningHandType.NINE_GATES),
            List.of(),
            List.of(WinningHandType.SELF_PICK_WITHOUT_EXTRA_FAAN)),
        Arguments.of(
            new WinningConditions(true, false, false, false, false),
            List.of(WinningHandType.ALL_HONOR_TILES),
            List.of(),
            List.of(WinningHandType.SELF_PICK_WITHOUT_EXTRA_FAAN)),
        Arguments.of(
            new WinningConditions(false, false, false, false, false),
            List.of(WinningHandType.THIRTEEN_ORPHANS),
            List.of(),
            List.of(WinningHandType.SELF_PICK_WITHOUT_EXTRA_FAAN)),
        Arguments.of(
            new WinningConditions(false, false, false, false, false),
            List.of(),
            List.of(WinningHandType.GREAT_FLOWERS, WinningHandType.WIN_FROM_WALL),
            List.of(WinningHandType.SELF_PICK_WITHOUT_EXTRA_FAAN)),
        Arguments.of(
            new WinningConditions(false, false, false, false, false),
            List.of(),
            List.of(WinningHandType.FLOWER_HANDS),
            List.of(WinningHandType.SELF_PICK_WITHOUT_EXTRA_FAAN)),
        Arguments.of(
            new WinningConditions(false, false, false, false, false),
            List.of(WinningHandType.THIRTEEN_ORPHANS),
            List.of(WinningHandType.GREAT_FLOWERS),
            List.of(WinningHandType.SELF_PICK_WITHOUT_EXTRA_FAAN)));
  }
}
