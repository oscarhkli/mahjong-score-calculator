package com.oscarhkli.mahjong.score;

import static org.assertj.core.api.BDDAssertions.then;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class BonusWinningConditionsCalculatorTest {

  private BonusWinningConditionCalculator bonusWinningConditionCalculator =
      new BonusWinningConditionCalculator();

  @ParameterizedTest
  @MethodSource
  void calculateBonusWinningConditions(
      List<MahjongTileType> windPongs,
      List<MahjongTileType> windKongs,
      List<MahjongTileType> dragonPongs,
      List<MahjongTileType> dragonKongs,
      List<MahjongTileType> bonusTiles,
      WindType windSettings,
      boolean hasExposedMelds,
      List<WinningHandType> expected) {
    var windMelds =
        new Melds(MahjongSetType.WIND, List.of(), windPongs, windKongs, null, new int[4], 0, 0);
    var dragonMelds =
        new Melds(MahjongSetType.WIND, List.of(), dragonPongs, dragonKongs, null, new int[4], 0, 0);
    var exposedMelds = mock(ExposedMelds.class);

    given(exposedMelds.isEmpty()).willReturn(hasExposedMelds);

    var winningHandTypes =
        bonusWinningConditionCalculator.calculateBonusWinningHands(
            windMelds, dragonMelds, bonusTiles, windSettings, exposedMelds);

    then(winningHandTypes).containsExactlyInAnyOrderElementsOf(expected);
  }

  private static Stream<Arguments> calculateBonusWinningConditions() {
    return Stream.of(
        Arguments.of(
            List.of(MahjongTileType.EAST),
            List.of(),
            List.of(),
            List.of(),
            List.of(),
            new WindType(MahjongTileType.EAST, MahjongTileType.SOUTH),
            true,
            List.of(
                WinningHandType.PREVAILING_WIND,
                WinningHandType.WIN_FROM_WALL,
                WinningHandType.NO_FLOWERS)),
        Arguments.of(
            List.of(MahjongTileType.SOUTH),
            List.of(MahjongTileType.WEST),
            List.of(),
            List.of(),
            List.of(),
            new WindType(MahjongTileType.WEST, MahjongTileType.SOUTH),
            false,
            List.of(
                WinningHandType.PREVAILING_WIND,
                WinningHandType.SEAT_WIND,
                WinningHandType.NO_FLOWERS)),
        Arguments.of(
            List.of(MahjongTileType.NORTH),
            List.of(MahjongTileType.WEST),
            List.of(),
            List.of(),
            List.of(),
            new WindType(MahjongTileType.NORTH, MahjongTileType.NORTH),
            false,
            List.of(
                WinningHandType.PREVAILING_WIND,
                WinningHandType.SEAT_WIND,
                WinningHandType.NO_FLOWERS)),
        Arguments.of(
            List.of(MahjongTileType.NORTH),
            List.of(MahjongTileType.WEST),
            List.of(MahjongTileType.RED),
            List.of(),
            List.of(),
            new WindType(MahjongTileType.NORTH, MahjongTileType.NORTH),
            false,
            List.of(
                WinningHandType.PREVAILING_WIND,
                WinningHandType.SEAT_WIND,
                WinningHandType.ONE_DRAGON,
                WinningHandType.NO_FLOWERS)),
        Arguments.of(
            List.of(MahjongTileType.NORTH),
            List.of(MahjongTileType.WEST),
            List.of(MahjongTileType.RED),
            List.of(MahjongTileType.WHITE),
            List.of(),
            new WindType(MahjongTileType.NORTH, MahjongTileType.NORTH),
            false,
            List.of(
                WinningHandType.PREVAILING_WIND,
                WinningHandType.SEAT_WIND,
                WinningHandType.ONE_DRAGON,
                WinningHandType.ONE_DRAGON,
                WinningHandType.NO_FLOWERS)),
        Arguments.of(
            List.of(MahjongTileType.NORTH),
            List.of(MahjongTileType.WEST),
            List.of(MahjongTileType.RED, MahjongTileType.GREEN),
            List.of(MahjongTileType.WHITE),
            List.of(),
            new WindType(MahjongTileType.NORTH, MahjongTileType.NORTH),
            false,
            List.of(
                WinningHandType.PREVAILING_WIND,
                WinningHandType.SEAT_WIND,
                WinningHandType.ONE_DRAGON,
                WinningHandType.ONE_DRAGON,
                WinningHandType.ONE_DRAGON,
                WinningHandType.NO_FLOWERS)),
        Arguments.of(
            List.of(),
            List.of(),
            List.of(),
            List.of(),
            List.of(
                MahjongTileType.F1,
                MahjongTileType.F2,
                MahjongTileType.F3,
                MahjongTileType.F4,
                MahjongTileType.S1,
                MahjongTileType.S2,
                MahjongTileType.S3,
                MahjongTileType.S4),
            new WindType(MahjongTileType.NORTH, MahjongTileType.NORTH),
            false,
            List.of(WinningHandType.GREAT_FLOWERS)),
        Arguments.of(
            List.of(),
            List.of(),
            List.of(),
            List.of(),
            List.of(
                MahjongTileType.F1,
                MahjongTileType.F2,
                MahjongTileType.F3,
                MahjongTileType.F4,
                MahjongTileType.S1,
                MahjongTileType.S2,
                MahjongTileType.S4),
            new WindType(MahjongTileType.NORTH, MahjongTileType.NORTH),
            false,
            List.of(WinningHandType.FLOWER_HANDS)),
        Arguments.of(
            List.of(),
            List.of(),
            List.of(),
            List.of(),
            List.of(MahjongTileType.F1, MahjongTileType.F2, MahjongTileType.F3, MahjongTileType.F4),
            new WindType(MahjongTileType.NORTH, MahjongTileType.NORTH),
            false,
            List.of(WinningHandType.ALL_FLOWERS)),
        Arguments.of(
            List.of(),
            List.of(),
            List.of(),
            List.of(),
            List.of(MahjongTileType.S1, MahjongTileType.S2, MahjongTileType.S3, MahjongTileType.S4),
            new WindType(MahjongTileType.NORTH, MahjongTileType.NORTH),
            false,
            List.of(WinningHandType.ALL_FLOWERS)),
        Arguments.of(
            List.of(),
            List.of(),
            List.of(),
            List.of(),
            List.of(
                MahjongTileType.F4,
                MahjongTileType.S1,
                MahjongTileType.S2,
                MahjongTileType.S3,
                MahjongTileType.S4),
            new WindType(MahjongTileType.NORTH, MahjongTileType.NORTH),
            false,
            List.of(WinningHandType.ALL_FLOWERS, WinningHandType.FLOWER_OF_OWN_WIND)),
        Arguments.of(
            List.of(),
            List.of(),
            List.of(),
            List.of(),
            List.of(MahjongTileType.F4, MahjongTileType.S1, MahjongTileType.S4),
            new WindType(MahjongTileType.NORTH, MahjongTileType.NORTH),
            false,
            List.of(WinningHandType.FLOWER_OF_OWN_WIND, WinningHandType.FLOWER_OF_OWN_WIND)),
        Arguments.of(
            List.of(),
            List.of(),
            List.of(),
            List.of(),
            List.of(MahjongTileType.F4, MahjongTileType.S1, MahjongTileType.S4),
            new WindType(MahjongTileType.NORTH, MahjongTileType.EAST),
            false,
            List.of(WinningHandType.FLOWER_OF_OWN_WIND)));
  }
}
