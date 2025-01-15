package com.oscarhkli.mahjong.score;

import static org.assertj.core.api.BDDAssertions.then;

import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class WinningHandTest {

  @ParameterizedTest
  @MethodSource
  void getFaans(List<WinningHandType> winningHandTypes, int expected) {
    var score = new WinningHand(winningHandTypes).getFaans();
    then(score).isEqualTo(expected);
  }

  private static Stream<Arguments> getFaans() {
    return Stream.of(
        Arguments.of(List.of(WinningHandType.TRICK_HAND), -1),
        Arguments.of(List.of(WinningHandType.TRICK_HAND, WinningHandType.ALL_ONE_SUIT), -1),
        Arguments.of(List.of(WinningHandType.CHICKEN_HAND, WinningHandType.ALL_ONE_SUIT), 7),
        Arguments.of(List.of(WinningHandType.COMMON_HAND, WinningHandType.ALL_ONE_SUIT), 8),
        Arguments.of(List.of(WinningHandType.ALL_IN_TRIPLETS, WinningHandType.ALL_ONE_SUIT), 10),
        Arguments.of(List.of(WinningHandType.ALL_HONOR_TILES), 10),
        Arguments.of(
            List.of(
                WinningHandType.SMALL_WINDS,
                WinningHandType.MIXED_ONE_SUIT,
                WinningHandType.ALL_IN_TRIPLETS),
            9));
  }
}
